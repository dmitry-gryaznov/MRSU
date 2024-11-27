package com.example.mrsu.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.dataclasses.User
import com.example.mrsu.objects.RequestObj.getUser
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import com.example.mrsu.objects.RequestObj.isTokenValid

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isTokenValid(this)) {
            onPause()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

        getUserInfoRequest(this)
        getUser(this)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        );

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
        binding.userId.text = tmp?.id.toString()
        binding.userName.text = tmp?.userName.toString()
        binding.userBirthDate.text = tmp?.birthDate.toString()
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        this.getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().putString("access_token", null).apply()
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

}