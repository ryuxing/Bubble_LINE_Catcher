package com.ryuxing.bubblelinecatcher.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ryuxing.bubblelinecatcher.room.ChatDao
import com.ryuxing.bubblelinecatcher.room.ChatMessageDao

@Database(entities = [Chat::class, ChatMessage::class], version= 1, exportSchema = false)
abstract class Database :RoomDatabase(){
    abstract fun ChatDao(): ChatDao
    abstract fun ChatMessageDao(): ChatMessageDao
}