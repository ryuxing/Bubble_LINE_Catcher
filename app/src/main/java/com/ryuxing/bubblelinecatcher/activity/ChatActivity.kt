package com.ryuxing.bubblelinecatcher.activity


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import com.ryuxing.bubblelinecatcher.databinding.ActivityChatBinding
import com.ryuxing.bubblelinecatcher.livedata.ChatViewModel
import com.ryuxing.bubblelinecatcher.service.NotificationService
import com.ryuxing.bubblelinecatcher.viewControl.MessageRecyclerAdapter
import java.lang.Exception
import android.view.Menu as Menu1

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var rv:RecyclerView
    private var chatId =""
    private var unread = 0
    val hash = this.hashCode()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //IntentからChatId取得
        val chatIdFromIntent = intent.getStringExtra("chatId")
        val chatIdFromURI    = (intent.dataString?:"/chat/").split("/chat/")[1]
        if(chatIdFromURI==""&& chatIdFromIntent==null) finish()
        else if(chatIdFromURI=="") chatId = chatIdFromIntent.toString()
        else chatId = chatIdFromURI
        Log.d("Chat_Id", chatId)
        //チャットID取得完了

        binding = ActivityChatBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        val messageList : List<ChatMessage> = App.dataManager.mDao.getMessages(chatId)
        val messageAdapter = MessageRecyclerAdapter(chatId)
        val layoutManager = LinearLayoutManager(this)
        rv = findViewById<RecyclerView>(R.id.message_recycler_view)
        rv.setHasFixedSize(true)
        rv.layoutManager = layoutManager
        rv.adapter = messageAdapter
        //Log.d("Message",App.dataManager.mDao.getMessages(chatId).toString())
        findViewById<Button>(R.id.message_goto_latest_button).setOnClickListener(View.OnClickListener {view ->
            val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!

            scrollView.smoothScrollToPosition((scrollView.adapter!!.itemCount) -1)
            findViewById<Button>(R.id.message_goto_latest_button).text = "▼"

            Log.d("COUNT",messageAdapter.itemCount.toString())

        })
        Log.d("COUNT",messageAdapter.itemCount.toString())
        //LiveDataを定義
        val viewModel = ChatViewModel.getChatViewModel(chatId)
        Log.d("Activity_HASH", "Activity ${hash} is ${this.isLaunchedFromBubble}")
        viewModel.register()
        val observer = Observer<HashMap<Long,ChatMessage>>{
            if(it.isEmpty()){
               return@Observer
            }else{
                val prevPos = layoutManager.findLastVisibleItemPosition()
                val prevItem= messageAdapter.itemCount-1
                val count = messageAdapter.updateMessages(it)
                viewModel.receiveMessage(hash)
                App.dataManager.read(chatId)
                Log.d("Scroll","prevPos="+prevPos+", prevItem=prevItem")
                //スクロールするかどうか
                if(prevPos==prevItem){
                    val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!
                    unread = 0
                    scrollView.smoothScrollToPosition((scrollView.adapter!!.itemCount) -1)
                    findViewById<Button>(R.id.message_goto_latest_button).alpha = 0.6F
                }else{
                    unread +=count
                    findViewById<Button>(R.id.message_goto_latest_button).alpha = 1F
                }
                var text = "▼"
                if(unread>0){
                    text = unread.toString()+ "+ "+text
                }
                findViewById<Button>(R.id.message_goto_latest_button).text = text

            }
        }
        viewModel.messages.observe(this,observer)
        findViewById<Button>(R.id.message_goto_latest_button).alpha = 0.6F

        rv.addOnScrollListener(scrollListener())
        val actionBar = supportActionBar
        val room = App.dataManager.cDao.getChat(chatId).first()
        if (room.isGroup){
            actionBar!!.setIcon(R.drawable.ic_baseline_group_24)
        }
        actionBar!!.title = room.chatName


    }

    override fun onStart() {
        val scrollView = findViewById<RecyclerView>(R.id.message_recycler_view)!!
        scrollView.scrollToPosition((scrollView.adapter!!.itemCount) -1)
        super.onStart()
        App.dataManager.read(chatId)
    }

    override fun onDestroy() {
        ChatViewModel.unbindChatViewModel(chatId)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu1?): Boolean {
        menuInflater.inflate(R.menu.menu_chat,menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("menu version","hoge")
            if(this.isLaunchedFromBubble){
                Log.d("menu removed","hoge")
                menu?.removeItem(R.id.item_open_with_bubble)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.item_open_with_LINE ->{
                val intent = Intent().also { i->
                    i.setClassName("jp.naver.line.android","jp.naver.line.android.activity.shortcut.ShortcutLauncherActivity")
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    i.putExtra("shortcutType","chatmid")
                    i.putExtra("shortcutTargetId",chatId)
                    i.putExtra("shortcutTargetName","")
                    i.putExtra("shortcutFromOS",false)
                }
                startActivity(intent)
                return true
            }
            R.id.item_open_with_bubble ->{
                val notificationService = NotificationService()
                try{
                    val chat = App.dataManager.cDao.getChat(chatId)[0]
                    val message = App.dataManager.mDao.getLastChatMessage(chatId)[0]
                    notificationService.sendNotification(
                        message,
                        chat,
                        chat.createPerson(),
                        true,
                        this,
                        autoExpandBubble = true,
                        withStockUsing = false
                    )
                }catch(e: Exception){
                    Log.w("Bubble_Error",e.stackTraceToString())
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class scrollListener: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                findViewById<Button>(R.id.message_goto_latest_button).alpha = 1F
            }else{
                val mgr = recyclerView.layoutManager as LinearLayoutManager
                if(mgr.findLastVisibleItemPosition()<mgr.itemCount-1){
                    findViewById<Button>(R.id.message_goto_latest_button).alpha = 1F
                }else{
                    findViewById<Button>(R.id.message_goto_latest_button).text = "▼"
                    findViewById<Button>(R.id.message_goto_latest_button).alpha = 0.6F
                }
            }
            super.onScrollStateChanged(recyclerView, newState)
        }
    }
}