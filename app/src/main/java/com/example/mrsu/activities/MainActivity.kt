package com.example.mrsu.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mrsu.R
import com.example.mrsu.fragments.HomeFragment
import com.example.mrsu.fragments.ScheduleFragment
import com.example.mrsu.objects.RequestObj.isTokenValid
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Убедитесь, что токен валиден
        if (!isTokenValid(this)) {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Установка разметки
        setContentView(R.layout.activity_main)

        // Загружаем главный фрагмент по умолчанию
        loadFragment(HomeFragment())

        // Настройка нижней панели навигации
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.schedule -> {
                    loadFragment(ScheduleFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Метод для замены фрагмента в контейнере
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
