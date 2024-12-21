package com.example.mrsu.fragments

import TurnstileAdapter
import TurnstileRepository
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mrsu.R
import com.example.mrsu.databinding.FragmentTurnstileBinding
import com.example.mrsu.objects.RequestObj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TurnstileFragment : Fragment() {

    private var _binding: FragmentTurnstileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TurnstileAdapter
    private lateinit var repository: TurnstileRepository
    private lateinit var token: String
    private val requestObj = RequestObj
    private var selectedDate: Calendar = Calendar.getInstance()

    private val currentWeekDate: Calendar = Calendar.getInstance()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTurnstileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = TurnstileRepository()
        adapter = TurnstileAdapter()
        token = context?.let { requestObj.getAccessToken(it).toString() }.toString()


        binding.turnstileRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.turnstileRecyclerView.adapter = adapter


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
        fetchTimeTable()
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
                val date = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault()).format(
                    selectedDate?.time ?: Calendar.getInstance().time
                )
                val data = withContext(Dispatchers.IO) {
                    repository.fetchTurnstileHistory(token,date)
                }
                if (data != null) {
                    adapter.updateData(data)
                } else {
                    Toast.makeText(requireContext(), "Ошибка загрузки истории турникетов", Toast.LENGTH_SHORT).show()
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
