package com.example.mrsu.activities

import StudentSemester
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityDiscBinding
import com.example.mrsu.objects.RequestObj

class DisciplAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Устанавливаем DataBinding
        val binding: ActivityDiscBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_disc)

        // Загружаем данные для отображения
        loadStudentSemester(binding)
    }

    private fun loadStudentSemester(binding: ActivityDiscBinding) {
        RequestObj.getStudentSemesterRequest(
            context = this,
            onSuccess = { studentSemester ->
                Log.i("DisciplAct", "Данные получены: $studentSemester")
                displayStudentSemester(studentSemester, binding)
            },
            onFailure = { error ->
                Log.e("DisciplAct", "Ошибка загрузки данных: $error")
            }
        )
    }

    private fun displayStudentSemester(
        studentSemester: StudentSemester,
        binding: ActivityDiscBinding
    ) {
        // Получаем контейнер из XML
        val container = binding.container

        // Проходимся по всем дисциплинам и добавляем их в контейнер
        studentSemester.RecordBooks.forEach { recordBook ->
            recordBook.Disciplines.forEach { discipline ->
                // Создаем кнопку для отображения дисциплины
                val button = Button(this).apply {
                    text = "Название: ${discipline.Title}\nСеместр: ${discipline.PeriodString}"
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.rounded_button_background)
                    setPadding(20, 20, 20, 20)
                }

                // Настраиваем параметры кнопки
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 16) // Добавляет отступы между кнопками
                }

                // Добавляем кнопку в контейнер
                container.addView(button, params)
            }
        }
    }
}
