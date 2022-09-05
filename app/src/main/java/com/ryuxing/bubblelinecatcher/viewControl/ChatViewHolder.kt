package com.ryuxing.bubblelinecatcher.viewControl

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.R

class ChatViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
    val wrapepr  :ConstraintLayout = itemView.findViewById(R.id.chatItem)
    val chatName:TextView = itemView.findViewById(R.id.chat_item_chatroom_text)
    val chatIcon:ImageView= itemView.findViewById(R.id.chat_item_icon_image)
    val lastDate :TextView = itemView.findViewById(R.id.chat_item_time_text)
    val lastMsg  :TextView = itemView.findViewById(R.id.chat_item_last_message_text)
    val unreadBadge:CardView=itemView.findViewById(R.id.chat_unread_badge)
}