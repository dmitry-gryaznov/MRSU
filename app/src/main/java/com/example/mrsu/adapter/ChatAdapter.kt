    package com.example.mrsu.adapter

    import ChatMessage
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.mrsu.R
    import com.example.mrsu.objects.RequestObj
    import java.text.SimpleDateFormat
    import java.util.Locale

    class ChatAdapter(private val chatMessages: List<ChatMessage>) :
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val avatarImageView: ImageView = view.findViewById(R.id.avatarImageView)
            val senderTextView: TextView = view.findViewById(R.id.senderTextView)
            val messageTextView: TextView = view.findViewById(R.id.messageTextView)
            val messageDateTextView: TextView = view.findViewById(R.id.messageDateTextView) // Добавлено
            val messageContainer: View = view.findViewById(R.id.messageContainer) // Контейнер сообщения
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_message, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val chatMessage = chatMessages[position]

            // Устанавливаем имя отправителя
            holder.senderTextView.text = chatMessage.user.fio

            // Устанавливаем текст сообщения или заглушку
            holder.messageTextView.text = chatMessage.text ?: ""

            // Устанавливаем дату сообщения
            holder.messageDateTextView.text = formatDate(chatMessage.createDate)

            // Устанавливаем фон и выравнивание
            val layoutParams = holder.messageContainer.layoutParams as ConstraintLayout.LayoutParams
            val isUserMessage = chatMessage.user.id == RequestObj.getUser(holder.itemView.context)?.id

            if (isUserMessage) {
                // Сообщение пользователя
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                holder.messageContainer.setBackgroundResource(R.drawable.user_message_background)
            } else {
                // Сообщение другого пользователя
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET

                if (chatMessage.isTeacher) {
                    holder.messageContainer.setBackgroundResource(R.drawable.teacher_message_background)
                } else {
                    holder.messageContainer.setBackgroundResource(R.drawable.student_message_background)
                }
            }
            holder.messageContainer.layoutParams = layoutParams

            // Загружаем аватар пользователя
            Glide.with(holder.itemView.context)
                .load(chatMessage.user.photo.urlSmall)
                .circleCrop()
                .into(holder.avatarImageView)
        }


        override fun getItemCount(): Int = chatMessages.size

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString // Возвращаем оригинальный формат в случае ошибки
            }
        }
    }
