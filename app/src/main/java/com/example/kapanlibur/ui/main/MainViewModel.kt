package com.example.kapanlibur.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kapanlibur.data.model.Holiday
import com.example.kapanlibur.data.model.Result
import com.example.kapanlibur.data.repository.HolidayRepository
import com.example.kapanlibur.di.ApiProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {
    
    private val holidayRepository = HolidayRepository(ApiProvider.holidayApiService)
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val _selectedYear = MutableStateFlow(LocalDate.now().year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()
      private val _selectedMonth = MutableStateFlow<Int?>(null)
    val selectedMonth: StateFlow<Int?> = _selectedMonth.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _allHolidays = MutableStateFlow<List<Holiday>>(emptyList())
    
    init {
        loadHolidays()
    }
    
    fun loadHolidays() {
        val year = _selectedYear.value
        val month = _selectedMonth.value
        
        viewModelScope.launch {
            val flow = when {
                month != null -> holidayRepository.getHolidaysByYearAndMonth(year, month)
                year != LocalDate.now().year -> holidayRepository.getHolidaysByYear(year)
                else -> holidayRepository.getAllHolidays()
            }
              flow.collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> _uiState.value.copy(isLoading = true, error = null)
                    is Result.Success -> {
                        _allHolidays.value = result.data
                        val filteredHolidays = filterHolidays(result.data, _searchQuery.value)
                        _uiState.value.copy(
                            holidays = filteredHolidays,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Unknown error occurred"
                    )
                }
            }
        }
    }
    
    fun setYear(year: Int) {
        _selectedYear.value = year
        loadHolidays()
    }
      fun setMonth(month: Int?) {
        _selectedMonth.value = month
        loadHolidays()
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        val filteredHolidays = filterHolidays(_allHolidays.value, query)
        _uiState.value = _uiState.value.copy(holidays = filteredHolidays)
    }
    
    private fun filterHolidays(holidays: List<Holiday>, query: String): List<Holiday> {
        if (query.isBlank()) {
            return holidays
        }
        
        return holidays.filter { holiday ->
            holiday.name.contains(query, ignoreCase = true)
        }
    }
    
    fun retry() {
        loadHolidays()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MainUiState(
    val holidays: List<Holiday> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
