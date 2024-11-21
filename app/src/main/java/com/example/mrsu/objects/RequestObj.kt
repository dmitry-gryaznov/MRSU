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

object RequestObj {

    private val client = OkHttpClient()
    private val gson = Gson()


    //Есть ли токен вообще
    fun isTokenValid(context: Context): Boolean{
        val token = getAccessToken(context)
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        return !token.isNullOrEmpty()//Сюда вписать функцию для проверки ttl
        //!!!!!!!!!!!!
    }

    //Запросы к Бд
    fun getAccessTokenRequest(context: Context, urlString: String, username: String, password: String) {

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
                Log.w("Request token failed", e.message.toString())

            }

            override fun onResponse(call: okhttp3.Call, response: Response) {

                if (response.isSuccessful) {
                    val accessTokenMessage = gson.fromJson(response.body?.string(), AccessTokenMessage::class.java)
                    Log.i("Successful auth request", accessTokenMessage.toString())
                    saveAccessToken(context, accessTokenMessage)
                }
                else {
                    Log.i("Unsuccessful auth request", response.code.toString())
                }
            }
        })
    }

    fun getUserInfoRequest(context: Context) {
        val bearerToken = getAccessToken(context)

        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/User")
            .get()
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.w("Request user failed", e.message.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val user = gson.fromJson(response.body?.string(), User::class.java)
                    Log.i("Successful user request", user.toString())
                    saveUser(context, user)
                } else {
                    Log.i("Unsuccessful user request", response.code.toString())
                }
            }
        })
    }

    fun getStudentTimeTableRequest(context: Context){


        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/StudentTimeTable?date=")
            .post(FormBody.Builder().build())
            .addHeader("Authorization", "Bearer "+ getAccessToken(context))
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.w("Request TimeTable failed", e.message.toString())

            }

            override fun onResponse(call: okhttp3.Call, response: Response) {

                if (response.isSuccessful) {
                    val accessTokenMessage = gson.fromJson(response.body?.string(), AccessTokenMessage::class.java)
                    Log.i("Successful TimeTable request", accessTokenMessage.toString())
                    saveAccessToken(context, accessTokenMessage)
                }
                else {
                    Log.i("Unsuccessful TimeTable request", response.code.toString())
                }
            }
        })
    }
    //SharedPreferences
    private fun saveAccessToken(context: Context, accessTokenMessage: AccessTokenMessage) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("access_token", accessTokenMessage.accessToken).apply()
        Log.i("SharedPreferences edited token", accessTokenMessage.accessToken)
    }

    private fun getAccessToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("access_token", null)
    }

    private fun saveUser(context: Context, user: User){
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("user", gson.toJson(user)).apply()
        Log.i("SharedPreferences edited user", gson.toJson(user))
    }

    public fun getUser(context: Context): User? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userJson = prefs.getString("user", null)

        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

}