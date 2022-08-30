package com.ryuxing.bubblelinecatcher.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryuxing.bubblelinecatcher.data.Chat

@Dao
interface ChatDao {
    //INSERT or UPDATE Chat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addChat(chat : Chat):Long

    //Get All Chats with Sort
    @Query ("SELECT * FROM chats ORDER BY lastDate DESC")
    fun getAllChats(): List<Chat>



    //Get All Chats with Sort
    @Query ("SELECT * FROM chats ORDER BY hasUnread DESC ,lastDate DESC")
    fun getAllChatsSortWithUnread(): List<Chat>

    //Get Chat
    @Query ("SELECT * FROM chats WHERE chatId=:chatId")
    fun getChat(chatId :String):List<Chat>

    //Update Read info
    @Query("UPDATE chats SET hasUnread=0 WHERE chatId=:chatId")
    fun readChat(chatId :String)

    @Query("DELETE FROM chats WHERE chatId=:chatId")
    fun deleteChat(chatId: String)


}