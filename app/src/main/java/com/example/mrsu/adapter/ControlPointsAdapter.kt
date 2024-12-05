package com.example.mrsu.adapters

import ControlDot
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.databinding.ItemControlPointBinding

class ControlPointsAdapter(private val controlPoints: List<ControlDot>) :
    RecyclerView.Adapter<ControlPointsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemControlPointBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(controlPoint: ControlDot) {
            binding.controlPointTitle.text = controlPoint.Title
            binding.dueDate.text = controlPoint.Date ?: "Дата отсутствует"
            binding.grade.text = "Балл: ${controlPoint.Mark?.Ball ?: "Нет данных"} / ${controlPoint.MaxBall}"
            binding.reportLink.text = controlPoint.Report?.DocFile?.URL ?: "Отчёт отсутствует"
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
