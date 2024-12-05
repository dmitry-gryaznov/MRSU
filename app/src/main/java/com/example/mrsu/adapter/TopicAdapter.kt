package com.example.mrsu.adapters

import Section
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.databinding.ItemTopicBinding

class TopicAdapter(private val topics: List<Section>) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Section) {
            binding.topicDescription.text = topic.Title
            val controlPointsAdapter = ControlPointsAdapter(topic.ControlDots)
            binding.controlPointsRecyclerView.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(binding.root.context)
            binding.controlPointsRecyclerView.adapter = controlPointsAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int = topics.size
}
