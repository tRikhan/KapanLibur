package com.example.kapanlibur.data.api

import com.example.kapanlibur.data.model.Holiday
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApiService {
    
    @GET("api")
    suspend fun getAllHolidays(): Response<List<Holiday>>
    
    @GET("api")
    suspend fun getHolidaysByYear(@Query("year") year: Int): Response<List<Holiday>>
    
    @GET("api")
    suspend fun getHolidaysByMonth(@Query("month") month: Int): Response<List<Holiday>>
    
    @GET("api")
    suspend fun getHolidaysByYearAndMonth(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<Holiday>>
    
    @GET("api/today")
    suspend fun getTodayHolidays(): Response<List<Holiday>>
    
    @GET("api/tomorrow")
    suspend fun getTomorrowHolidays(): Response<List<Holiday>>
}
