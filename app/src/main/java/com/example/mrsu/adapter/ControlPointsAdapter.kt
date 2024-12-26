package com.example.mrsu.adapters

import ControlDot
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.R
import com.example.mrsu.databinding.ItemControlPointBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ControlPointsAdapter(private val controlPoints: List<ControlDot>) :
    RecyclerView.Adapter<ControlPointsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemControlPointBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(controlPoint: ControlDot) {
            val mark = controlPoint.Mark?.Ball ?: 0.0
            val maxBall = controlPoint.MaxBall ?: 0.0
            val percentage = if (maxBall > 0) (mark / maxBall) * 100 else 0.0

            // Форматирование даты
            val formattedDate = controlPoint.Date?.let { formatDate(it) } ?: "Дата отсутствует"

            // Устанавливаем текст
            binding.controlPointTitle.text = controlPoint.Title
            binding.dueDate.text = formattedDate
            binding.grade.text = "${mark} / ${maxBall}"
            binding.reportLink.text = controlPoint.Report?.DocFile?.URL ?: "Отчёт отсутствует"

            // Задаём цвет плашки в зависимости от процента
            val backgroundColor = when {
                percentage < 50 -> ContextCompat.getColor(binding.root.context, R.color.grade_low)
                percentage < 75 -> ContextCompat.getColor(binding.root.context, R.color.grade_medium)
                percentage < 100 -> ContextCompat.getColor(binding.root.context, R.color.grade_high)
                else -> ContextCompat.getColor(binding.root.context, R.color.grade_full)
            }
            binding.gradeContainer.setBackgroundColor(backgroundColor)

            // Устанавливаем белый текст внутри плашки
            binding.grade.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Ожидаемый формат входной строки
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) // Выходной формат
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString // Возвращаем оригинальный формат в случае ошибки
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemControlPointBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(controlPoints[position])
    }

    override fun getItemCount(): Int = controlPoints.size
}
