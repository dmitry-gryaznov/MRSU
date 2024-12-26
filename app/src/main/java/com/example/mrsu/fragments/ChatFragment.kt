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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mrsu.R
import com.example.mrsu.adapter.ChatAdapter
import com.example.mrsu.objects.RequestObj
import com.google.android.material.textfield.TextInputEditText

class ChatFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)

        chatAdapter = ChatAdapter(chatMessages) { message ->
            if (message.user.id == RequestObj.getUser(requireContext())?.id) {
                showDeleteMessageDialog(message)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Вы можете удалять только свои сообщения",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val disciplineId = arguments?.getInt("id")
                if (disciplineId != null) {
                    sendMessage(disciplineId, messageText)
                } else {
                    Toast.makeText(requireContext(), "ID дисциплины не найден", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            val disciplineId = arguments?.getInt("id")
            if (disciplineId != null) {
                loadMessages(disciplineId) {
                    swipeRefreshLayout.isRefreshing = false // Останавливаем индикатор
                }
            } else {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(requireContext(), "ID дисциплины не найден", Toast.LENGTH_SHORT)
                    .show()
            }
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

    private fun loadMessages(disciplineId: Int, onComplete: (() -> Unit)? = null) {
        RequestObj.getForumMessages(
            context = requireContext(),
            disciplineId = disciplineId,
            onSuccess = { messages: List<ChatMessage> ->
                activity?.runOnUiThread {
                    displayMessages(messages)
                    onComplete?.invoke()
                }
            },
            onFailure = { error ->
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    onComplete?.invoke()
                }
            }
        )
    }

    private fun sendMessage(disciplineId: Int, messageText: String) {
        RequestObj.sendForumMessage(
            context = requireContext(),
            disciplineId = disciplineId,
            messageText = messageText,
            onSuccess = {
                activity?.runOnUiThread {
                    messageInput.text?.clear()
                    loadMessages(disciplineId)
                }
            },
            onFailure = { error ->
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun showDeleteMessageDialog(message: ChatMessage) {
        val alertDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Удалить сообщение")
            .setMessage("Вы уверены, что хотите удалить это сообщение?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteMessage(message)
            }
            .setNegativeButton("Отмена", null)
            .create()
        alertDialog.show()
    }

    private fun deleteMessage(message: ChatMessage) {
        RequestObj.deleteForumMessage(
            context = requireContext(),
            messageId = message.id,
            onSuccess = {
                activity?.runOnUiThread {
                    chatMessages.remove(message)
                    chatAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Сообщение удалено", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { error ->
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Ошибка удаления: $error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
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
        }.reversed())
        recyclerView.scrollToPosition(chatMessages.size - 1)
        chatAdapter.notifyDataSetChanged()
    }

}
