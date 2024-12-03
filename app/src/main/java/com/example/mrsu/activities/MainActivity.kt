package com.example.mrsu.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.objects.RequestObj.getStudentSemesterRequest
import com.example.mrsu.objects.RequestObj.getUser
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import com.example.mrsu.objects.RequestObj.isNetworkAvailable
import com.example.mrsu.objects.RequestObj.isTokenValid

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i("MainActivity", "onCreate()")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isNetworkAvailable(this)) Toast.makeText(
            this,
            "Нет подключения к сети, проверьте соединение",
            Toast.LENGTH_LONG
        ).show()

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

        setTheme(R.style.Theme_MRSU)
        super.onStart()

        Log.i("MainActivity", "onStart()")

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        setContentView(binding.main)

        getUserInfoRequest(
            context = this,
            onSuccess = {
                val tmp = getUser(this)
                ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                    insets
                }

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
            },
            onFailure = { errorMessage ->
                Log.e("MainActivity", "Failed to load user info: $errorMessage")
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        )

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        getStudentSemesterRequest(
            context = this,
            onSuccess = { semesterData ->
                Log.i("StudentSemester", "Данные получены: $semesterData")
                // Используйте объект StudentSemester
                semesterData.RecordBooks.forEach { recordBook ->
                    Log.i("RecordBook", "Код: ${recordBook.Cod}, Факультет: ${recordBook.Faculty}")
                    recordBook.Disciplines.forEach { discipline ->
                        Log.i("Discipline", "Название: ${discipline.Title}, Семестр: ${discipline.PeriodString}")
                    }
                }
            },
            onFailure = { error ->
                Log.e("StudentSemester", error)
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )

        // Listener для кнопки
        binding.button.setOnClickListener {
            Log.i("MainActivity", "Button clicked: Переход к дисциплинам")
            val intent = Intent(this, DisciplAct::class.java)
            startActivity(intent)
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }


    private fun logout() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Log.i("MainActivity.Logout", "User log outed")
    }

}