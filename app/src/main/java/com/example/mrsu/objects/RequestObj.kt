package com.example.mrsu.objects

import StudentRatingPlanResponse
import StudentSemester
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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


    //Запросы к Бд
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
//
        Log.i("isTokenValid()", "Token valid. Expires at $expiresAt")
        return true
    }

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

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.w("getAccessTokenRequest()", "Network error: ${e.message}")
                (context as? AppCompatActivity)?.runOnUiThread {
                    onFailure("Нет подключения к интернету. Проверьте сетевое соединение.")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val accessTokenMessage =
                            gson.fromJson(responseBody, AccessTokenMessage::class.java)
                        saveAccessToken(context, accessTokenMessage)
                        Log.i("getAccessTokenRequest()", "Successful request: $accessTokenMessage")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        Log.w("getAccessTokenRequest()", "Parsing problem: ${e.message}")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onFailure("Ошибка парсинга ответа: ${e.message}")
                        }
                    }
                } else {
                    val errorMessage = when (response.code) {
                        400 -> "Некорректные данные. Проверьте введённый логин и пароль."
                        in 500..599 -> "Технические работы на сервере. Попробуйте позже."
                        else -> "Неизвестная ошибка. Код ошибки: ${response.code}"
                    }
                    Log.w("getAccessTokenRequest()", "Server error: $errorMessage")
                    (context as? AppCompatActivity)?.runOnUiThread {
                        onFailure(errorMessage)
                    }
                }
            }
        })
    }


    //Получить информацию о пользователе
    fun getUserInfoRequest(
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
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
                Log.w("getUserInfoRequest()", "Request user failed: ${e.message}")
                (context as? AppCompatActivity)?.runOnUiThread {
                    onFailure("Ошибка сети: ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val user = gson.fromJson(responseBody, User::class.java)
                        saveUser(context, user)
                        Log.i("getUserInfoRequest()", "Successful user request: $user")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        Log.w("getUserInfoRequest()", "Parsing problem: ${e.message}")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onFailure("Ошибка парсинга ответа: ${e.message}")
                        }
                    }
                } else if (response.code == 401) {
                    Log.w("getUserInfoRequest()", "Token dead")
                    (context as? AppCompatActivity)?.runOnUiThread {
                        onFailure("Токен недействителен")
                    }
                } else {
                    Log.w(
                        "getUserInfoRequest()",
                        "Unsuccessful user request: ${response.code}"
                    )
                    (context as? AppCompatActivity)?.runOnUiThread {
                        onFailure("Ошибка сервера: ${response.code}")
                    }
                }
            }
        })
    }

    //Список дисциплин
    fun getStudentSemesterRequest(
        context: Context,
        onSuccess: (StudentSemester) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.i("getStudentSemesterRequest()", "Started")
        val bearerToken = getAccessToken(context)

        if (bearerToken.isNullOrEmpty()) {
            Log.e("getStudentSemesterRequest()", "Token is null or empty")
            onFailure("Токен не найден. Пожалуйста, авторизуйтесь.")
            return
        }

        val url = "https://papi.mrsu.ru/v1/StudentSemester?selector=current"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("getStudentSemesterRequest()", "Request failed: ${e.message}")
                (context as? AppCompatActivity)?.runOnUiThread {
                    onFailure("Ошибка сети: ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val semesterData = gson.fromJson(responseBody, StudentSemester::class.java)
                        Log.i("getStudentSemesterRequest()", "Parsed response: $semesterData")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onSuccess(semesterData)
                        }
                    } catch (e: Exception) {
                        Log.e("getStudentSemesterRequest()", "Parsing failed: ${e.message}")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onFailure("Ошибка парсинга ответа: ${e.message}")
                        }
                    }
                }
                else {
                    val errorMessage = when (response.code) {
                        401 -> "Токен недействителен. Пожалуйста, авторизуйтесь снова."
                        500 -> "На сервере ведутся технические работы."
                        else -> "Ошибка сервера: ${response.code}"
                    }
                    Log.e("getStudentSemesterRequest()", "Error: $errorMessage")
                    (context as? AppCompatActivity)?.runOnUiThread {
                        onFailure(errorMessage)
                    }
                }
            }
        })
    }

    //Получить рейтинг план по id
    fun getStudentRatingPlan(
        context: Context,
        disciplineId: Int,
        onSuccess: (StudentRatingPlanResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.i("getStudentRatingPlan", "Started")
        val bearerToken = getAccessToken(context)

        if (bearerToken.isNullOrEmpty()) {
            Log.e("getStudentRatingPlan", "Token is null or empty")
            onFailure("Токен не найден. Пожалуйста, авторизуйтесь.")
            return
        }

        val url = "https://papi.mrsu.ru/v1/StudentRatingPlan/?id=$disciplineId"
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("getStudentRatingPlan", "Request failed: ${e.message}")
                (context as? AppCompatActivity)?.runOnUiThread {
                    onFailure("Ошибка сети: ${e.message}")
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val parsedResponse = gson.fromJson(responseBody, StudentRatingPlanResponse::class.java)
                        Log.i("getStudentRatingPlan", "Parsed response: $parsedResponse")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onSuccess(parsedResponse)
                        }
                    } catch (e: Exception) {
                        Log.e("getStudentRatingPlan", "Parsing failed: ${e.message}")
                        (context as? AppCompatActivity)?.runOnUiThread {
                            onFailure("Ошибка обработки ответа: ${e.message}")
                        }
                    }
                } else {
                    val errorMessage = when (response.code) {
                        401 -> "Токен недействителен. Пожалуйста, авторизуйтесь снова."
                        500 -> "На сервере ведутся технические работы."
                        else -> "Ошибка сервера: ${response.code}"
                    }
                    Log.e("getStudentRatingPlan", "Error: $errorMessage")
                    (context as? AppCompatActivity)?.runOnUiThread {
                        onFailure(errorMessage)
                    }
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
        Log.i("saveAccessToken()", "Token saved : $accessTokenMessage")
        val localTime = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date(expiresAt * 1000))
        Log.i("saveAccessToken()", "ExpiresAt (local): $localTime")

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