package com.example.mrsu.activities

import ScheduleAdapter
import ScheduleItem
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityScheduleBinding

import com.example.mrsu.repository.ScheduleRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleBinding
    private val repository = ScheduleRepository(this)
    private var token = ""
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private var currentWeekDate: Calendar = Calendar.getInstance()
    private var selectedDate: Calendar = Calendar.getInstance() // Хранит выбранный день
    private val adapter = ScheduleAdapter()
    private var selectedSubgroup = 1 // 0 = все, 1 = первая подгруппа, 2 = вторая подгруппа

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка RecyclerView
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.scheduleRecyclerView.adapter = adapter

        // Настройка RadioGroup для выбора подгруппы
        binding.subgroupSelector.setOnCheckedChangeListener { _, checkedId ->
            selectedSubgroup = when (checkedId) {
                R.id.firstSubgroup -> 1
                R.id.secondSubgroup -> 2
                else -> 0
            }
            fetchTimeTable() // Обновляем расписание для текущего выбранного дня
        }

        // Обработчики переключения недель
        binding.prevWeekButton.setOnClickListener {
            currentWeekDate.add(Calendar.WEEK_OF_YEAR, -1)
            updateWeek()
        }

        binding.nextWeekButton.setOnClickListener {
            currentWeekDate.add(Calendar.WEEK_OF_YEAR, 1)
            updateWeek()
        }

        // Изначальная настройка недели и дней
        updateWeek()
    }

    private fun updateWeek() {
        // Устанавливаем текст текущей недели
        val startOfWeek = (currentWeekDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val endOfWeek = (currentWeekDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }

        val weekRange =
            "${SimpleDateFormat("d MMM", Locale.getDefault()).format(startOfWeek.time)} - ${SimpleDateFormat("d MMM", Locale.getDefault()).format(endOfWeek.time)}"
        binding.currentWeekText.text = weekRange

        // Обновляем дни недели
        updateDaysContainer(startOfWeek)
    }

    private fun updateDaysContainer(startOfWeek: Calendar) {
        // Очищаем контейнер для кнопок дней
        binding.daysContainer.removeAllViews()

        // Добавляем кнопки для каждого дня недели
        val days = mutableListOf<Calendar>()
        for (i in 0..6) {
            val day = (startOfWeek.clone() as Calendar).apply {
                add(Calendar.DAY_OF_WEEK, i)
            }
            days.add(day)

            // Создаем кнопку для каждого дня
            val button = Button(this).apply {
                text = SimpleDateFormat("E, d MMM", Locale.getDefault()).format(day.time)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                    gravity = Gravity.CENTER
                }
                setOnClickListener {
                    selectedDate = day // Устанавливаем выбранный день
                    fetchTimeTable()
                }
            }
            binding.daysContainer.addView(button)

            // Если это текущий день, сразу его выделяем
            if (day.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                selectedDate = day
                fetchTimeTable()
            }
        }
    }

    private fun fetchTimeTable() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val date = dateFormat.format(selectedDate.time) // Используем выбранный день
                val data = withContext(Dispatchers.IO) {
                    repository.fetchTimeTable(date)
                }
                if (data != null) {
                    adapter.updateData(data, selectedSubgroup) // Обновляем данные в RecyclerView с учетом подгруппы
                } else {
                    Toast.makeText(this@ScheduleActivity, "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ScheduleActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}