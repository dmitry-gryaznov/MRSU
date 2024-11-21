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

    val gson = Gson()

    fun isTokenValid(): Boolean {
        return false
    }

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
                Log.w("Request failed", e.message.toString())
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {

                if (response.isSuccessful) {
                    val accessTokenMessage =
                        gson.fromJson(response.body?.string(), AccessTokenMessage::class.java)
                    saveAccessToken(context, accessTokenMessage)
                    Log.i("Successful auth request", accessTokenMessage.toString())
                }
                else {
                    Log.i("Unsuccessful auth request", response.code.toString())
                }
            }
        })
    }

    fun saveAccessToken(context: Context, accessTokenMessage: AccessTokenMessage) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("access_token", accessTokenMessage.accessToken).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("access_token", null)
    }

    fun getUserInfo(context: Context) {

        val bearerToken = getToken(context)

        val request = Request.Builder()
            .url("https://papi.mrsu.ru/v1/User")
            .get()
            .addHeader("Authorization", "Bearer $bearerToken")
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                println("Request failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val user = gson.fromJson(response.body?.string(), User::class.java)
                    println("Response: ${user.toString()}")
                } else {
                    println("Error: ${response.code}")
                }
            }
        })
    }


}