package com.ryuxing.bubblelinecatcher.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = false) val msgId :Long,
    val chatId :String,
    val message:String,
    val isStamp:Boolean,
    val sender :String,
    val date: Long
    )

