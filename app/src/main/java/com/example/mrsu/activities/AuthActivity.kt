package com.example.mrsu.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityAuthBinding
import com.example.mrsu.objects.RequestObj.getAccessTokenRequest
import com.example.mrsu.objects.RequestObj.getUserInfoRequest

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i("AuthActivity", "onCreate()")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val binding: ActivityAuthBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_auth
        )

        setContentView(binding.auth)

        ViewCompat.setOnApplyWindowInsetsListener(binding.auth) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {

        Log.i("AuthActivity", "onStart()")
        val binding: ActivityAuthBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_auth
        )

        super.onStart()
        binding.loginButton.setOnClickListener {

            Log.i("AuthActivity", "ButtonLogin clicked")

            //Проверка ввода
            if (binding.emailInput.text.toString() == "" || !binding.emailInput.text.toString()
                    .contains('@')
            ) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_LONG).show()
            } else if (binding.passwordInput.text.toString() == "") {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_LONG).show()
            }

            //Отправка запроса
            else {
                getAccessTokenRequest(
                    context = this,
                    urlString = "https://p.mrsu.ru/OAuth/Token",
                    username = binding.emailInput.text.toString(),
                    password = binding.passwordInput.text.toString(),
                    onSuccess = {
                        getUserInfoRequest(
                            context = this,
                            onSuccess = {
                                runOnUiThread {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    Log.i("AuthActivity", "finish()")
                                    finish()
                                }
                            },
                            onFailure = { errorMessage ->
                                runOnUiThread {
                                    Toast.makeText(this, "Ошибка: $errorMessage", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    },
                    onFailure = { errorMessage ->
                        runOnUiThread {
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                )

            }
        }
    }

}