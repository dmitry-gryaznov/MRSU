package com.example.mrsu.fragments

import ScheduleAdapter
import ScheduleItem
import android.content.res.Resources.Theme
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mrsu.R
import com.example.mrsu.databinding.FragmentScheduleBinding
import com.example.mrsu.repository.ScheduleRepository
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: ScheduleRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private var currentWeekDate: Calendar = Calendar.getInstance()
    private var selectedDate: Calendar = Calendar.getInstance()
    private val adapter = ScheduleAdapter()
    private var selectedSubgroup = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = ScheduleRepository(requireContext())

        // Настройка RecyclerView
        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter

        // Настройка RadioGroup для выбора подгруппы
        binding.subgroupSelector.setOnCheckedChangeListener { _, checkedId ->
            selectedSubgroup = when (checkedId) {
                R.id.firstSubgroup -> 1
                R.id.secondSubgroup -> 2
                else -> 0
            }
            fetchTimeTable()
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
        val startOfWeek = (currentWeekDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        val endOfWeek = (currentWeekDate.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }

        val weekRange =
            "${SimpleDateFormat("d MMM", Locale.getDefault()).format(startOfWeek.time)} - ${SimpleDateFormat("d MMM", Locale.getDefault()).format(endOfWeek.time)}"
        binding.currentWeekText.text = weekRange

        updateDaysContainer(startOfWeek)
    }

    private fun updateDaysContainer(startOfWeek: Calendar) {
        val daysButtons = listOf(
            Pair(binding.dayButton1, binding.dayText1),
            Pair(binding.dayButton2, binding.dayText2),
            Pair(binding.dayButton3, binding.dayText3),
            Pair(binding.dayButton4, binding.dayText4),
            Pair(binding.dayButton5, binding.dayText5),
            Pair(binding.dayButton6, binding.dayText6),
            Pair(binding.dayButton7, binding.dayText7)
        )

        val currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        for ((index, day) in daysButtons.withIndex()) {
            val currentDay = (startOfWeek.clone() as Calendar).apply {
                add(Calendar.DAY_OF_WEEK, index)
            }

            val button = day.first
            val text = day.second

            // Обновляем текст кнопки и текстовое поле
            button.text = currentDay.get(Calendar.DAY_OF_MONTH).toString()
            text.text = SimpleDateFormat("EEE", Locale.getDefault()).format(currentDay.time).uppercase()

            // Проверяем текущий день
            if (currentDay.get(Calendar.DAY_OF_YEAR) == currentDayOfYear) {
                button.setBackgroundResource(R.drawable.day_button_selected) // Фон текущего дня
                button.tag = "currentDay" // Устанавливаем метку для текущего дня
                selectedDate = currentDay
            } else {
                button.setBackgroundResource(R.drawable.day_button_default) // Фон по умолчанию
                button.tag = "normalDay"
            }

            // Устанавливаем обработчик нажатия
            button.setOnClickListener {
                selectedDate = currentDay
                fetchTimeTable()

                // Снимаем выделение со всех кнопок, кроме текущего дня
                daysButtons.forEach {
                    val otherButton = it.first
                    if (otherButton.tag != "currentDay") {
                        otherButton.setBackgroundResource(R.drawable.day_button_default)
                    }
                }

                // Устанавливаем выделение для нажатой кнопки
                if (button.tag != "currentDay") {
                    button.setBackgroundResource(R.drawable.day_button_pressed)
                }
            }
        }
    }



    private fun fetchTimeTable() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val date = dateFormat.format(selectedDate.time)
                val data = withContext(Dispatchers.IO) {
                    repository.fetchTimeTable(date)
                }
                if (data != null) {
                    adapter.updateData(data, selectedSubgroup)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки расписания", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
