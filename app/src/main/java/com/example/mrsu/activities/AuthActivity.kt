package com.example.mrsu.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityAuthBinding
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.objects.RequestObj.getAccessTokenRequest
import com.example.mrsu.objects.RequestObj.getUserInfo

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val binding: ActivityAuthBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_auth
        );

        setContentView(binding.auth)

        ViewCompat.setOnApplyWindowInsetsListener(binding.auth) { v, insets ->
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