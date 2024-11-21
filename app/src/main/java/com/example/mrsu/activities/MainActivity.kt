package com.example.mrsu.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.objects.RequestObj.getUserInfo
import com.example.mrsu.objects.RequestObj.getAccessTokenRequest
import com.example.mrsu.objects.RequestObj.isTokenValid

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isTokenValid()) {
            onPause()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

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

        binding.loginButton.setOnClickListener {
            getAccessTokenRequest(
                this, "https://p.mrsu.ru/OAuth/Token",
                binding.emailInput.text.toString(),
                binding.passwordInput.text.toString()
            )
            getUserInfo(this)
        }

    }
}