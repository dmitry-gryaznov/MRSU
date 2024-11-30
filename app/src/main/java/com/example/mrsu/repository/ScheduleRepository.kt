package com.example.mrsu.repository

import ScheduleItem
import android.content.Context
import android.util.Log
import com.example.mrsu.objects.RequestObj
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.IOException

class ScheduleRepository(private val context: Context)  {
    private val client = OkHttpClient()
    private val gson = Gson()
    private var token = ""
    private val requestObj = RequestObj

    suspend fun fetchTimeTable(date: String): List<ScheduleItem>? {
        token = requestObj.getAccessToken(context).toString()

        val url = "https://papi.mrsu.ru/v1/StudentTimeTable?date=$date"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    if (!json.isNullOrEmpty()) {
                        Log.i("response", json)
                        return@withContext try {
                            val scheduleItems = gson.fromJson(json, Array<ScheduleItem>::class.java).toList()
                            Log.d("ParsedData", scheduleItems.toString())
                            scheduleItems // Успешно парсим данные и возвращаем список
                        } catch (e: Exception) {
                            Log.e("ParsingError", "Ошибка при парсинге JSON: ${e.message}")
                            null // Возвращаем null в случае ошибки парсинга
                        }
                    } else {
                        Log.e("response", "Пустое тело ответа")
                        null // Пустой ответ
                    }
                } else {
                    Log.e("response", "Ошибка запроса: ${response.code}, ${response.message}")
                    null // Ошибка HTTP-запроса
                }
            } catch (e: IOException) {
                Log.e("NetworkError", "Ошибка сети: ${e.message}")
                null // Ошибка сети
            }
        }

    }
}
