package com.example.mrsu.fragments

import Section
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mrsu.adapters.TopicAdapter
import com.example.mrsu.databinding.FragmentPlanBinding
import com.example.mrsu.objects.RequestObj

class RatingPlanFragment : Fragment() {

    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!

    private val topics = mutableListOf<Section>()
    private lateinit var topicAdapter: TopicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        val disciplineId = arguments?.getInt("id") ?: -1
        fetchRatingPlan(disciplineId)
    }

    private fun setupRecyclerView() {
        topicAdapter = TopicAdapter(topics)
        binding.controlPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.controlPointsRecyclerView.adapter = topicAdapter
    }

    private fun fetchRatingPlan(disciplineId: Int) {
        RequestObj.getStudentRatingPlan(
            context = requireContext(),
            disciplineId = disciplineId,
            onSuccess = { response ->
                Log.i("StudentRatingPlan", "Success: $response")
                topics.clear()
                topics.addAll(response.Sections)
                topicAdapter.notifyDataSetChanged()
            },
            onFailure = { error ->
                Log.e("StudentRatingPlan", "Error: $error")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
