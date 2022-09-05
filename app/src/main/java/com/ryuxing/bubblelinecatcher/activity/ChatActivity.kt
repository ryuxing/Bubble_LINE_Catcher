package com.ryuxing.bubblelinecatcher.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import com.ryuxing.bubblelinecatcher.databinding.ActivityChatBinding
import com.ryuxing.bubblelinecatcher.livedata.ChatViewModel
import com.ryuxing.bubblelinecatcher.service.NotificationLoggerService
import com.ryuxing.bubblelinecatcher.service.NotificationService
import com.ryuxing.bubblelinecatcher.ui.MessageRecyclerAdapter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var rv:RecyclerView
    private var chatId =""
    private var unread = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //IntentからChatId取得
        val chatIdFromIntent = intent.getStringExtra("chatId")
        val chatIdFromURI    = (intent.dataString?:"/chat/").split("/chat/")[1]
        Log.d("Chat_Id", chatIdFromIntent.toString())
        Log.d("Chat_Id", chatIdFromURI.toString())
        if(chatIdFromURI==""&& chatIdFromIntent==null) finish()
        else if(chatIdFromURI=="") chatId = chatIdFromIntent.toString()
        else chatId = chatIdFromURI
        Log.d("Chat_Id", chatId)
        //チャットID取得完了

        binding = ActivityChatBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        val messageList : List<ChatMessage> = App.dataManager.mDao.getMessages(chatId)
        val messageAdapter = MessageRecyclerAdapter(messageList)
        val layoutManager = LinearLayoutManager(this)
        rv = findViewById<RecyclerView>(R.id.message_recycler_view)
        rv.setHasFixedSize(true)
        rv.layoutManager = layoutManager
        rv.adapter = messageAdapter
        Log.d("Message",App.dataManager.mDao.getMessages(chatId).toString())
        findViewById<Button>(R.id.message_goto_latest_button).setOnClickListener(View.OnClickListener {view ->
            val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!

            scrollView.scrollToPosition((scrollView.adapter!!.itemCount) -1)
            findViewById<Button>(R.id.message_goto_latest_button).text = "▼"

            Log.d("COUNT",messageAdapter.itemCount.toString())

        })
        Log.d("COUNT",messageAdapter.itemCount.toString())
        //LiveDataを定義
        val viewModel = ChatViewModel.getChatViewModel(chatId)
        val observer = Observer<HashMap<Long,ChatMessage>>{
            if(it.isEmpty()){
               return@Observer
            }else{
                val prevPos = layoutManager.findLastVisibleItemPosition()
                val prevItem= messageAdapter.itemCount-1
                val count = messageAdapter.updateMessages(it)
                viewModel.receiveMessage()
                App.dataManager.read(chatId)
                Log.d("Scroll","prevPos="+prevPos+", prevItem=prevItem")
                //スクロールするかどうか
                if(prevPos==prevItem){
                    val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!
                    scrollView.scrollToPosition((scrollView.adapter!!.itemCount) -1)
                    unread = 0
                }else{
                    unread +=count
                }
                var text = "▼"
                if(unread>0){
                    text = unread.toString()+ "+ "+text
                }
                findViewById<Button>(R.id.message_goto_latest_button).text = text

            }
            Log.d("OBSERVE_CHAT",chatId)
        }
        viewModel.messages.observe(this,observer)
        Log.d("OBSERVER_CHAT_START",chatId)
        val actionBar = supportActionBar
        val room = App.dataManager.cDao.getChat(chatId).first()
        var roomName = ""
        if (room.isGroup){
            roomName = "\uD83D\uDC65 "
        }
        roomName += room.chatName
        actionBar!!.title = roomName
    }

    override fun onStart() {
        val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!
        scrollView.scrollToPosition((scrollView.adapter!!.itemCount) -1)
        super.onStart()
        App.dataManager.read(chatId)
    }

    override fun onDestroy() {
        ChatViewModel.removeChatViewModel(chatId)
        super.onDestroy()
        //broadcastReceiverを破棄
    }

}