package com.ryuxing.bubblelinecatcher.data

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ryuxing.bubblelinecatcher.activity.MainActivity
import com.ryuxing.bubblelinecatcher.livedata.ChatViewModel
import com.ryuxing.bubblelinecatcher.service.NotificationService
import java.io.File

class Manager(db: Database) {
    val db = db
    val cDao = db.ChatDao()
    val mDao = db.ChatMessageDao()

    //メッセージがきたときに使う
    inner class addMessage(chat: Chat, msg: ChatMessage) :Thread(){
        val chat = chat
        val msg  = msg
        constructor(chat: Chat, msg: ChatMessage,start: Boolean =true) : this(chat,msg) {
            //ブロードキャストかlivedataかでUI側にも送信
            chat.hasUnread = true
            MainActivity.mainViewModel.updateChat(chat)
            ChatViewModel.addMessageToChatView(msg)
            this.start()
        }
        override fun run() {
            cDao.addChat(chat)
            mDao.addMsg(msg)


        }
    }

    //チャットの削除時
    fun removeChat(chatId:String){
        cDao.deleteChat(chatId)
        mDao.deleteChatMessages(chatId)
        NotificationService.removeChat(chatId)
    }
    //既読時の通知
    fun read(chatId:String){
        NotificationService.removeMessages(chatId)
        cDao.readChat(chatId)
        MainActivity.mainViewModel.updateRead(chatId)
        Log.d("BLC_READED__Manager",chatId)
    }
    fun insertOldMessages(file :File){
        //データ取得
        //uri.
        val oldDb = SQLiteDatabase.openDatabase(file,SQLiteDatabase.OpenParams.Builder().addOpenFlags(SQLiteDatabase.OPEN_READONLY).build())
        //val db = super.getReadableDatabase()
        val messageCursor = oldDb.query(
            "messages",
            arrayOf("msgId", "chatId", "message", "type", "sender", "date"),
            null,
            null,
            null,
            null,
            null)
        val chatCursor = oldDb.query(
            "chats",
            arrayOf("chatId", "chatName", "isGroup", "lastMsg", "lastSenderName", "lastSenderIcon", "lastDate", "hasUnread"),
            null,
            null,
            null,
            null,
            null)
        db.RestoreDao().restore(messageCursor,chatCursor)
        oldDb.close()
    }

}