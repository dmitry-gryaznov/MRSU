package com.example.mrsu.Api

import com.example.mrsu.dataclasses.TurnstileHistory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface TurnstileApi {
    @GET("v1/Security")
    suspend fun getTurnstileHistory(
        @Header("Authorization") token: String, // Параметр для передачи токена
        @Query("date") date: String
    ): List<TurnstileHistory>
}