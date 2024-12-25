package com.example.mrsu.fragments

import ChatMessage
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.R
import com.example.mrsu.adapter.ChatAdapter
import com.example.mrsu.objects.RequestObj
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: TextInputEditText
    private lateinit var sendButton: ImageButton
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)

        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем аргументы
        val disciplineId = arguments?.getInt("id")
        val disciplineName = arguments?.getString("name")

        // Устанавливаем название дисциплины в TextView
        val disciplineTitleView: TextView = view.findViewById(R.id.disciplineTitle)
        disciplineTitleView.text = disciplineName ?: "Название не указано"

        if (disciplineId != null) {
            RequestObj.getForumMessages(
                context = requireContext(),
                disciplineId = disciplineId,
                onSuccess = { messages: List<ChatMessage> -> // Тип указывается явно
                    activity?.runOnUiThread {
                        displayMessages(messages)
                    }
                },
                onFailure = { error ->
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

    }

    private fun displayMessages(messages: List<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages.map { message ->
            ChatMessage(
                id = message.id,
                user = message.user,
                isTeacher = message.isTeacher,
                createDate = message.createDate,
                text = message.text
            )
        })
        chatAdapter.notifyDataSetChanged()
    }

}
