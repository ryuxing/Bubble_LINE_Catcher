package com.ryuxing.bubblelinecatcher.room

import android.database.Cursor
import android.util.Log
import androidx.room.*
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.data.ChatMessage

@Dao
interface  RestoreDao {

    @Transaction
    fun restore(msgCsr: Cursor, chatCsr: Cursor){
        Log.d("MessageRestore","Restore Start. Last No. = ${msgCsr.columnCount}")

        while (msgCsr.moveToNext()){
            Log.d("msgCsr",msgCsr.columnNames.contentToString())
            addMsg(ChatMessage(
                msgCsr.getLong(0),
                msgCsr.getString(1),
                msgCsr.getString(2),
                msgCsr.getInt(3) == 1,
                msgCsr.getString(4),
                msgCsr.getLong(5)
            ))
            Log.d("MessageRestore","Restored. No.${msgCsr.count}")
        }
        Log.d("ChatRestore","Restore Start. Last No. = ${chatCsr.columnCount}")
        while (chatCsr.moveToNext()){
            addChat(Chat(
                chatCsr.getString(0),
                chatCsr.getString(1),
                chatCsr.getInt(2)==1,
                chatCsr.getString(3),
                chatCsr.getString(4),
                chatCsr.getString(5),
                chatCsr.getLong(6),
                false
            ))
            Log.d("ChatRestore","Restored. No.${chatCsr.count}")
        }
        //

    }
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addMsg(msg: ChatMessage)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addChat(chat:Chat)
    //@Query("INSERT INTO chats SELECT * FROM chats WHERE true ON CONFLICT (select * from messages) as msg")

}