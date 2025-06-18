package com.example.kapanlibur.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Holiday(
    val date: String,
    val name: String
) {
    fun getLocalDate(): LocalDate {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }
    
    fun getFormattedDate(): String {
        val localDate = getLocalDate()
        return localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }
    
    fun isUpcoming(): Boolean {
        return getLocalDate().isAfter(LocalDate.now()) || getLocalDate().isEqual(LocalDate.now())
    }
    
    fun getShortName(): String {
        return if (name.length > 30) {
            name.take(27) + "..."
        } else {
            name
        }
    }
}
