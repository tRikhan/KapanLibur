package com.example.kapanlibur.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kapanlibur.R
import com.example.kapanlibur.data.model.Holiday

class HolidayAdapter : ListAdapter<Holiday, HolidayAdapter.HolidayViewHolder>(HolidayDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_holiday, parent, false)
        return HolidayViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
      class HolidayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.tvDay)
        private val monthText: TextView = itemView.findViewById(R.id.tvMonth)
        private val dateText: TextView = itemView.findViewById(R.id.tvDate)
        private val nameText: TextView = itemView.findViewById(R.id.tvName)
        
        fun bind(holiday: Holiday) {
            // Set the circular date display
            val date = holiday.getLocalDate()
            dayText.text = String.format("%02d", date.dayOfMonth)
            monthText.text = date.month.name.substring(0, 3).uppercase()
            
            // Set the holiday details
            dateText.text = holiday.getFormattedDate()
            nameText.text = holiday.name
        }
    }
    
    class HolidayDiffCallback : DiffUtil.ItemCallback<Holiday>() {
        override fun areItemsTheSame(oldItem: Holiday, newItem: Holiday): Boolean {
            return oldItem.date == newItem.date
        }
        
        override fun areContentsTheSame(oldItem: Holiday, newItem: Holiday): Boolean {
            return oldItem == newItem
        }
    }
}
