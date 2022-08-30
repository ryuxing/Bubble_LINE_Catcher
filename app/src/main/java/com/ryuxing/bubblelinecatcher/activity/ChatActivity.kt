package com.ryuxing.bubblelinecatcher.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //IntentからChatId取得
        val chatIdFromIntent = intent.getStringExtra("chatId")
        val chatIdFromURI    = (intent.dataString?:"/chat/").split("/chat/")[1]
        Log.d("Chat_Id", chatIdFromIntent.toString())
        Log.d("Chat_Id", chatIdFromURI.toString())
        var chatId = ""
        if(chatIdFromURI==""&& chatIdFromIntent==null) finish()
        else if(chatIdFromURI=="") chatId = chatIdFromIntent.toString()
        else chatId = chatIdFromURI
        Log.d("Chat_Id", chatId)
        //チャットID取得完了


        Log.d("Message",App.dataManager.mDao.getMessages(chatId).toString())

    }
}