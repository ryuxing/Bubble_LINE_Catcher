package com.ryuxing.bubblelinecatcher.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.R

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val wrapper : ConstraintLayout = itemView.findViewById(R.id.item_message)
    val messageIcon: ImageView = itemView.findViewById(R.id.message_icon_image)
    val messageIconCard: CardView = itemView.findViewById(R.id.message_icon_card)
    val messageName: TextView = itemView.findViewById(R.id.message_sender_text)
    val messageDate: TextView = itemView.findViewById(R.id.message_dete_text)
    val messageCard: CardView = itemView.findViewById(R.id.message_content_card)
    val messageText: TextView = itemView.findViewById(R.id.message_content_text)
    val messageSticker: ImageView = itemView.findViewById(R.id.message_content_sticker_image)

}