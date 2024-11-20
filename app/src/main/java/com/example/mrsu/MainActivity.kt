package com.example.mrsu

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mrsu.dataclasses.AccessTokenMessage
import com.example.mrsu.dataclasses.User
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class MainActivity : AppCompatActivity() {

    fun updateAccessToken(context: Context, urlString: String, username: String, password: String) {
        val client = OkHttpClient()

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
                println("Request failed: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val accessTokenMessage = gson.fromJson(response.body?.string(), AccessTokenMessage::class.java)
                    saveAccessToken(context, accessTokenMessage)
                    println("Response: ${accessTokenMessage.expiresIn}")
                } else {
                    println("Error: ${response.code}")
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
        val client = OkHttpClient()

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
                    val gson = Gson()
                    val user = gson.fromJson(response.body?.string(), User::class.java)
                    println("Response: ${user.toString()}")
                } else {
                    println("Error: ${response.code}")
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.loginButton)
        val login = findViewById<TextView>(R.id.emailInput)
        val password = findViewById<TextView>(R.id.passwordInput)
        button.setOnClickListener {
            updateAccessToken(this, "https://p.mrsu.ru/OAuth/Token", "ГрязновДЮ", "bitbit777")
            getUserInfo(this)
        }

    }
}