package com.ryuxing.bubblelinecatcher.room

import androidx.room.*
import com.ryuxing.bubblelinecatcher.data.ChatMessage
@Dao
interface ChatMessageDao {
    //INSERT or UPDATE Chat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMsg(message : ChatMessage):Long

    //Get All Message in Chat
    @Query("SELECT * FROM messages WHERE chatId=:chatId ORDER BY date DESC")
    fun getMessages(chatId :String): List<ChatMessage>

    //Get Chat
    @Query("SELECT * FROM messages WHERE msgId=:msgId")
    fun getMessage(msgId :Long):List<ChatMessage>

    //Delete Messages of the Chat
    @Query("DELETE FROM messages WHERE chatId=:chatId")
    fun deleteChatMessages(chatId: String)

}