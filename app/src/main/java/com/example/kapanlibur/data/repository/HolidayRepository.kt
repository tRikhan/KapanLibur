package com.example.kapanlibur.data.repository

import com.example.kapanlibur.data.api.HolidayApiService
import com.example.kapanlibur.data.model.Holiday
import com.example.kapanlibur.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate

class HolidayRepository(
    private val apiService: HolidayApiService
) {
    
    fun getAllHolidays(): Flow<Result<List<Holiday>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllHolidays()
            if (response.isSuccessful) {
                val holidays = response.body() ?: emptyList()
                emit(Result.Success(holidays.sortedBy { it.date }))
            } else {
                emit(Result.Error(Exception("Failed to fetch holidays: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getHolidaysByYear(year: Int): Flow<Result<List<Holiday>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHolidaysByYear(year)
            if (response.isSuccessful) {
                val holidays = response.body() ?: emptyList()
                emit(Result.Success(holidays.sortedBy { it.date }))
            } else {
                emit(Result.Error(Exception("Failed to fetch holidays: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
      fun getHolidaysByMonth(month: Int): Flow<Result<List<Holiday>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHolidaysByMonth(month)
            if (response.isSuccessful) {
                val holidays = response.body() ?: emptyList()
                emit(Result.Success(holidays.sortedBy { it.date }))
            } else {
                emit(Result.Error(Exception("Failed to fetch holidays: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getHolidaysByYearAndMonth(year: Int, month: Int): Flow<Result<List<Holiday>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHolidaysByYearAndMonth(year, month)
            if (response.isSuccessful) {
                val holidays = response.body() ?: emptyList()
                emit(Result.Success(holidays.sortedBy { it.date }))
            } else {
                emit(Result.Error(Exception("Failed to fetch holidays: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getUpcomingHolidays(limit: Int = 3): Flow<Result<List<Holiday>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getAllHolidays()
            if (response.isSuccessful) {
                val holidays = response.body() ?: emptyList()
                val today = LocalDate.now()
                val upcomingHolidays = holidays
                    .filter { 
                        val holidayDate = it.getLocalDate()
                        holidayDate.isAfter(today) || holidayDate.isEqual(today)
                    }
                    .sortedBy { it.date }
                    .take(limit)
                emit(Result.Success(upcomingHolidays))
            } else {
                emit(Result.Error(Exception("Failed to fetch holidays: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
