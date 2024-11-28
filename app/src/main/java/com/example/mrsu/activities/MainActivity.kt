package com.example.mrsu.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.objects.RequestObj.getUser
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import com.example.mrsu.objects.RequestObj.isTokenValid

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i("MainActivity", "onCreate()")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isTokenValid(this)) {
            onPause()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            Log.i("MainActivity", "finish()")
            finish()
            return
        }

    }

    override fun onStart() {
        super.onStart()

        Log.i("MainActivity", "onStart()")

        getUserInfoRequest(this)
        getUser(this)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        setContentView(binding.main)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tmp = getUser(this)

        Glide.with(this)
            .load(tmp?.photo?.urlSmall)
            .into(binding.userPhoto)
        binding.userId.text = "ID: " + tmp?.id.toString().take(8)
        binding.userName.text = "ФИО пользователя: " + tmp?.userName.toString()
        binding.userBirthDate.text = "Дата рождения: " + tmp?.birthDate.toString().take(10)

        binding.logoutButton.setOnClickListener {
            Log.i("MainActivity", "ButtonLogout clicked")
            logout()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            Log.i("MainActivity", "finish()")
            finish()
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit()
        //    .putString("access_token", null).apply()
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Log.i("MainActivity.Logout", "User log outed")
    }

}