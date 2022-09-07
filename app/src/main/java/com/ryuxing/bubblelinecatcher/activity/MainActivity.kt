package com.ryuxing.bubblelinecatcher.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.HorizontalScrollView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.databinding.ActivityMainBinding
import com.ryuxing.bubblelinecatcher.livedata.MainViewModel
import com.ryuxing.bubblelinecatcher.viewControl.ChatRecyclerAdapter


class MainActivity : AppCompatActivity(), View.OnCreateContextMenuListener {
    companion object{
        val mainViewModel = MainViewModel()
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var rv: RecyclerView
    private lateinit var chatAdapter: ChatRecyclerAdapter
    private lateinit var updateObserver:Observer<Chat>
    private lateinit var readObserver:Observer<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply{setContentView(this.root)}
        //Log.d("Database", App.dataManager.cDao.getAllChats().toString())
        chatAdapter = ChatRecyclerAdapter()
        val layoutManager = LinearLayoutManager(this)
        rv = findViewById<RecyclerView>(R.id.chat_recycler_view)
        chatAdapter.reload()
        rv.setHasFixedSize(true)
        rv.layoutManager = layoutManager
        rv.adapter = chatAdapter
        chatAdapter.notifyDataSetChanged()
        //ViewModel追加
        updateObserver = Observer<Chat>{
            val pos = layoutManager.findFirstVisibleItemPosition()
            chatAdapter.updateList(it)
            Log.d("OBSERVE UPDATE",it.toString())
            if (pos==0) rv.smoothScrollToPosition(0)
        }
        readObserver =Observer<String>{
            chatAdapter.updateRead(it)

        }
        mainViewModel.liveChat.observeForever(updateObserver)
        mainViewModel.read.observeForever(readObserver)
        Log.d("onCreated","created")
        //menu登録
        registerForContextMenu(rv)
    }

    override fun onStart() {
        Log.d("onStarted","started")
        super.onStart()
    }
    override fun onDestroy() {
        mainViewModel.liveChat.removeObserver(updateObserver)
        mainViewModel.read.removeObserver(readObserver)
        Log.d("onDestroy","destroyed")
        super.onDestroy()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        Log.d("onCreateContextMenu","destroyed")

        menuInflater.inflate(R.menu.menu_chat_item,menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d("onCreateItemSelected","destroyed")
        val menuId = item.itemId
        val position = chatAdapter.getPos()
        val chatId = chatAdapter.getChatId(position)
        when(menuId) {
            R.id.menu_chat_item_read -> App.dataManager.read(chatId)
            R.id.menu_chat_item_delete -> {
                App.dataManager.removeChat(chatId)
                chatAdapter.deleteChat(chatId)
            }
            else ->return super.onContextItemSelected(item)
        }
        return true
    }
    fun openFiler(view: View){
        val intent = Intent(Intent.ACTION_VIEW)
        //intent.setClassName("com.google.android.documentsui","com.android.documentsui.files.FilesActivity")
        val uri =
            Uri.parse("file://"+Environment.getExternalStorageDirectory().toString() + "/Pictures")
        intent.data = uri
        startActivity(intent)

    }

}