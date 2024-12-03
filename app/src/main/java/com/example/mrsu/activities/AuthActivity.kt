package com.example.mrsu.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityAuthBinding
import com.example.mrsu.objects.RequestObj.getAccessTokenRequest
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import java.util.concurrent.Executor

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricManager: BiometricManager
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        setContentView(binding.auth)

        ViewCompat.setOnApplyWindowInsetsListener(binding.auth) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Настройка Biometric API
        setupBiometricAuthentication()
    }

    override fun onStart() {
        super.onStart()

        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
        ) {
            // Показываем биометрию автоматически
            promptBiometricAuthentication()
        }

        binding.loginButton.setOnClickListener {
            // Проверка ввода
            if (binding.emailInput.text.toString().isEmpty() || !binding.emailInput.text.toString()
                    .contains('@')
            ) {
                Toast.makeText(this, "Введите email", Toast.LENGTH_LONG).show()
            } else if (binding.passwordInput.text.toString().isEmpty()) {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_LONG).show()
            } else {
                performLogin()
            }
       //биометрической аутентификации
        binding.biometricButton.setOnClickListener {
            promptBiometricAuthentication()
        }
    }
    }

    private fun setupBiometricAuthentication() {
        biometricManager = BiometricManager.from(this)
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)


                    // Выполнить автоматический вход
                    val savedEmail =
                        getSharedPreferences("app_prefs", MODE_PRIVATE).getString("email", "")
                    val savedPassword =
                        getSharedPreferences("app_prefs", MODE_PRIVATE).getString("password", "")


                    if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                        getAccessTokenRequest(
                            context = this@AuthActivity,
                            urlString = "https://p.mrsu.ru/OAuth/Token",
                            username = savedEmail,
                            password = savedPassword,
                            onSuccess = {
                                val intent = Intent(this@AuthActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(this@AuthActivity, errorMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        )
                    } else {
                        Toast.makeText(
                            this@AuthActivity,
                            "Данные не найдены. Выполните вход вручную.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })
    }

    private fun promptBiometricAuthentication() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Вход по биометрии")
            .setSubtitle("Используйте отпечаток пальца или Face ID для входа")
            .setNegativeButtonText("Отмена")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun performLogin() {
        getAccessTokenRequest(
            context = this,
            urlString = "https://p.mrsu.ru/OAuth/Token",
            username = binding.emailInput.text.toString(),
            password = binding.passwordInput.text.toString(),
            onSuccess = {
                // Сохранение данных для будущей биометрической аутентификации
                getSharedPreferences("app_prefs", MODE_PRIVATE).edit().apply {
                    putString("email", binding.emailInput.text.toString())
                    putString("password", binding.passwordInput.text.toString())
                    apply()
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            onFailure = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }
}