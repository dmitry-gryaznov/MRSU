package com.example.mrsu.fragments

import StudentSemester
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityDiscBinding
import com.example.mrsu.objects.RequestObj

class DisciplFragment : Fragment() {

    private lateinit var binding: ActivityDiscBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Устанавливаем DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_disc, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Загружаем данные для отображения
        loadStudentSemester()
    }

    private fun loadStudentSemester() {
        RequestObj.getStudentSemesterRequest(
            context = requireContext(),
            onSuccess = { studentSemester ->
                Log.i("DisciplFragment", "Данные получены: $studentSemester")
                displayStudentSemester(studentSemester)
            },
            onFailure = { error ->
                Log.e("DisciplFragment", "Ошибка загрузки данных: $error")
            }
        )
    }

    private fun displayStudentSemester(studentSemester: StudentSemester) {
        // Получаем контейнер из XML
        val container = binding.container

        // Проходимся по всем дисциплинам и добавляем их в контейнер
        studentSemester.RecordBooks.forEach { recordBook ->
            recordBook.Disciplines.forEach { discipline ->
                // Создаем кнопку для отображения дисциплины
                val button = Button(requireContext()).apply {
                    text = "Название: ${discipline.Title}\nСеместр: ${discipline.PeriodString}"
                    isAllCaps = false
                    gravity = Gravity.CENTER_VERTICAL
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
