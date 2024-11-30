package com.example.mrsu.objects

import android.content.Context
import android.util.Log

import com.example.mrsu.dataclasses.AccessTokenMessage
import com.example.mrsu.dataclasses.User
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RequestObj {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun isTokenValid(context: Context): Boolean {
        Log.i("isTokenValid()", "Started")
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString("access_token", null)
        val expiresAt = sharedPref.getLong("token_expires_at", 0)
        val currentTime = System.currentTimeMillis() / 1000 // Текущее время в секундах

        if (accessToken.isNullOrEmpty()) {
            Log.w("isTokenValid()", "No token")
            return false
        }

        if (expiresAt <= currentTime) {
            Log.w("isTokenValid()", "Token dead. Time : $currentTime, Expired : $expiresAt")
            return false
        }

        Log.i("isTokenValid()", "Token valid. Expires at $expiresAt")
        return true
    }

    //Запросы к Бд

    //Получить токен
    fun getAccessTokenRequest(
        context: Context,
        urlString: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.i("getAccessTokenRequest()", "Started")

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("client_id", "8")
            .add("client_secret", "qweasd")
            .add("grant_type", "password")
            .build()

        val request = Request.Builder()
            .url(urlString)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        Log.i("ee", request.toString())
        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.w("getAccessTokenRequest()", e.message.toString())
                onFailure("Ошибка сети: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val accessTokenMessage =
                            gson.fromJson(responseBody, AccessTokenMessage::class.java)
                        saveAccessToken(context, accessTokenMessage)
                        Log.w("getAccessTokenRequest()", "Successful request: $accessTokenMessage")

                        onSuccess()
                    } catch (e: Exception) {
                        Log.w("getAccessTokenRequest()", "Parsing problem: " + e.message.toString())
                        onFailure("Ошибка парсинга ответа: ${e.message}")
                    }
                } else {

                    Log.w("getAccessTokenRequest()", "Server problem: " + { response.code })
                    onFailure("Ошибка сервера: ${response.code}")
                }
            }
        })
    }

    //Получить информацию о пользователе
    fun getUserInfoRequest(context: Context) {
        Log.i("getUserInfoRequest()", "Started")
        val bearerToken = getAccessToken(context)

        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/User")
            .get()
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.w("getUserInfoRequest()", "Request user failed: " + e.message.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val user = gson.fromJson(response.body?.string(), User::class.java)
                    Log.i("getUserInfoRequest()", "Successful user request")
                    saveUser(context, user)
                }
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                else if (response.code == 401) {
                    Log.w("getUserInfoRequest()", "Token dead")
                }
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                else {
                    Log.w(
                        "getUserInfoRequest(): ",
                        "Unsuccessful user request" + response.code.toString()
                    )
                }
            }
        })
    }

    //Получить расписанние (андрей сделает свое)
    fun getStudentTimeTable(context: Context) {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault())
        val currentDate = formatter.format(Date())
        // URL с закодированной датой
        val url = "https://papi.mrsu.ru/v1/StudentTimeTable?date=$currentDate"

        // Получение токена (замените функцией для вашего приложения)
        val accessToken = getAccessToken(context)

        // Создание клиента
        val client = OkHttpClient()

        // Создание запроса
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        // Выполнение запроса
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Обработка ошибки
                Log.e("TimeTableRequest", "Request failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                // Проверяем успешность ответа
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.i("TimeTableRequest", "Response: $responseBody")
                    // Обработайте JSON-ответ при необходимости
                } else {
                    Log.e(
                        "TimeTableRequest",
                        "Error: ${response.code}, Message: ${response.message}"
                    )
                }
            }
        })
    }

    //SharedPreferences
    private fun saveAccessToken(context: Context, accessTokenMessage: AccessTokenMessage) {

        Log.i("saveAccessToken()", "Started")

        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        // Текущее время в секундах
        val currentTime = System.currentTimeMillis() / 1000
        val expiresAt = currentTime + accessTokenMessage.expiresIn

        sharedPref.edit()
            .putString("access_token", accessTokenMessage.accessToken)
            .putString("refresh_token", accessTokenMessage.refreshToken)
            .putLong("token_expires_at", expiresAt)
            .apply()
        val expDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(expiresAt * 1000))
        Log.i("saveAccessToken()", "Token saved : $expDate")
    }

    public fun getAccessToken(context: Context): String? {
        Log.i("getAccessToken()", "Started")
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("access_token", null)
    }

    private fun saveUser(context: Context, user: User) {
        Log.i("saveUser()", "Started")
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("user", gson.toJson(user)).apply()
        Log.i("saveUser()", "User saved : " + user.toString())
    }

    fun getUser(context: Context): User? {
        Log.i("getUser()", "Started")
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userJson = prefs.getString("user", null)

        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

}