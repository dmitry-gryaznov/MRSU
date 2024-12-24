package com.example.mrsu.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.R
import com.example.mrsu.adapter.ChatAdapter
import com.example.mrsu.dataclasses.ChatMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private val chatMessages = mutableListOf<ChatMessage>(
        ChatMessage("Alice", "Hello! How are you?", System.currentTimeMillis() - 60000),
        ChatMessage("Bob", "I'm fine, thanks! And you?", System.currentTimeMillis() - 30000),
        ChatMessage("Alice", "I'm doing great, thank you!", System.currentTimeMillis() - 10000),
        ChatMessage("Alice", "Hello! How are you?", System.currentTimeMillis() - 60000),
        ChatMessage("Bob", "I'm fine, thanks! And you?", System.currentTimeMillis() - 30000),
        ChatMessage("Alice", "I'm doing great, thank you!", System.currentTimeMillis() - 10000),
        ChatMessage("Alice", "Hello! How are you?", System.currentTimeMillis() - 60000),
        ChatMessage("Bob", "I'm fine, thanks! And you?", System.currentTimeMillis() - 30000),
        ChatMessage("Alice", "I'm doing great, thank you!", System.currentTimeMillis() - 10000),
        ChatMessage("Alice", "Hello! How are you?", System.currentTimeMillis() - 60000),
        ChatMessage("Bob", "I'm fine, thanks! And you?", System.currentTimeMillis() - 30000),
        ChatMessage("Alice", "I'm doing great, thank you!", System.currentTimeMillis() - 10000)
    )

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
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                val newMessage = ChatMessage("User", messageText, System.currentTimeMillis())
                chatMessages.add(newMessage)
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)
                messageInput.setText("")
            }
        }

        return view
    }
}
