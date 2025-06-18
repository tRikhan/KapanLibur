package com.example.kapanlibur

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kapanlibur.databinding.ActivityMainBinding
import com.example.kapanlibur.ui.main.HolidayAdapter
import com.example.kapanlibur.ui.main.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val holidayAdapter = HolidayAdapter()
    private var currentYearRange = 2015..LocalDate.now().year
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupToolbar()
        setupRecyclerView()
        setupSpinners()
        setupSearchBar()
        observeViewModel()
    }
    
    override fun onResume() {
        super.onResume()
        // Check if we need to update the year spinner when app resumes (e.g., new year)
        val currentYear = LocalDate.now().year
        if (currentYear > currentYearRange.last) {
            updateYearSpinner()
        }
    }
    
    private fun setupToolbar() {
        // No toolbar setup needed - using custom header instead
    }
    
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = holidayAdapter
        }
    }
    
    private fun setupSpinners() {
        updateYearSpinner()
        setupMonthSpinner()
    }
    
    private fun updateYearSpinner() {
        // Update year range to include current year if needed
        val currentYear = LocalDate.now().year
        val newRange = currentYearRange.first..currentYear
        
        if (newRange != currentYearRange) {
            currentYearRange = newRange
        }
        
        // Year spinner - only show current year and previous years
        val years = currentYearRange.toList().reversed() // Show from current year back to start year
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = yearAdapter
        
        // Set current year as default
        val currentYearIndex = years.indexOf(currentYear)
        if (currentYearIndex >= 0) {
            binding.yearSpinner.setSelection(currentYearIndex)
        }
        
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setYear(years[position])
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun setupMonthSpinner() {
        // Month spinner
        val months = listOf(
            "Semua Bulan",
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthSpinner.adapter = monthAdapter
        
        // Set "All Months" as default
        binding.monthSpinner.setSelection(0)
        
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val month = if (position == 0) null else position
                viewModel.setMonth(month)
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun setupSearchBar() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }
            
            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s?.toString()?.trim() ?: ""
                viewModel.setSearchQuery(searchQuery)
            }
        })
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                binding.progressBar.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
                
                if (uiState.holidays.isEmpty() && !uiState.isLoading) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    holidayAdapter.submitList(uiState.holidays)
                }
                
                uiState.error?.let { error ->
                    showError(error)
                    viewModel.clearError()
                }
            }
        }
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                viewModel.retry()
            }
            .show()
    }
}