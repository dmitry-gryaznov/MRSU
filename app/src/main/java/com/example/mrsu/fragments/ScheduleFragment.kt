package com.example.mrsu.fragments

import ScheduleAdapter
import ScheduleItem
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
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
    private var token = ""
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
        binding.daysContainer.removeAllViews()

        val days = mutableListOf<Calendar>()
        for (i in 0..6) {
            val day = (startOfWeek.clone() as Calendar).apply {
                add(Calendar.DAY_OF_WEEK, i)
            }
            days.add(day)

            val button = Button(requireContext()).apply {
                text = SimpleDateFormat("E, d MMM", Locale.getDefault()).format(day.time)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                    gravity = Gravity.CENTER
                }
                setOnClickListener {
                    selectedDate = day
                    fetchTimeTable()
                }
            }
            binding.daysContainer.addView(button)

            if (day.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                selectedDate = day
                fetchTimeTable()
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
