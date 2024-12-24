package com.example.mrsu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.R
import com.example.mrsu.dataclasses.ChatMessage

class ChatAdapter(private val chatMessages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderTextView: TextView = view.findViewById(R.id.senderTextView)
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.senderTextView.text = chatMessage.sender
        holder.messageTextView.text = chatMessage.message
    }

    override fun getItemCount(): Int = chatMessages.size
}
