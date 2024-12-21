package com.example.mrsu.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityMainBinding
import com.example.mrsu.fragments.DisciplFragment
import com.example.mrsu.fragments.HomeFragment
import com.example.mrsu.fragments.ScheduleFragment
import com.example.mrsu.fragments.TurnstileFragment
import com.example.mrsu.objects.RequestObj.getStudentSemesterRequest
import com.example.mrsu.objects.RequestObj.getUser
import com.example.mrsu.objects.RequestObj.getUserInfoRequest
import com.example.mrsu.objects.RequestObj.isTokenValid

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка токена
        if (!isTokenValid(this)) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        getUserInfoRequest(
            context = this,
            onSuccess = {
                val user = getUser(this)
                // Настройка нижней панели навигации
                setupBottomNavigation()

                // Загрузка фрагмента по умолчанию
                loadFragment(HomeFragment())
            },
            onFailure = { errorMessage ->
                Log.e("MainActivity", "Failed to load user info: $errorMessage")
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        )

        // Настройка привязки разметки через DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        // Настройка кнопки Logout
    }

    override fun onStart() {
        super.onStart()

        // Загружаем информацию о пользователе


        // Загружаем информацию о семестре
        getStudentSemesterRequest(
            context = this,
            onSuccess = { semesterData ->
                Log.i("StudentSemester", "Данные получены: $semesterData")
            },
            onFailure = { error ->
                Log.e("StudentSemester", error)
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }

                R.id.schedule -> {
                    loadFragment(ScheduleFragment())
                    true
                }

                R.id.discipline -> {
                    loadFragment(DisciplFragment())
                    true
                }

                R.id.turnstile -> {
                    loadFragment(TurnstileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

}
