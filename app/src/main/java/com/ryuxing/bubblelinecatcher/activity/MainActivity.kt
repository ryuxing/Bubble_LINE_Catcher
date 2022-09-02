package com.ryuxing.bubblelinecatcher.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.databinding.ActivityMainBinding
import com.ryuxing.bubblelinecatcher.livedata.MainViewModel
import com.ryuxing.bubblelinecatcher.ui.ChatRecyclerAdapter


class MainActivity : AppCompatActivity() {
    companion object{
        val mainViewModel = MainViewModel()
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var rv: RecyclerView
    private lateinit var updateObserver:Observer<Chat>
    private lateinit var readObserver:Observer<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply{setContentView(this.root)}
        //Log.d("Database", App.dataManager.cDao.getAllChats().toString())
        val chatList : List<Chat> = App.dataManager.cDao.getAllChats()
        val chatAdapter = ChatRecyclerAdapter(chatList)
        val layoutManager = LinearLayoutManager(this)
        rv = findViewById<RecyclerView>(R.id.chat_recycler_view)
        rv.setHasFixedSize(true)
        rv.layoutManager = layoutManager
        rv.adapter = chatAdapter
        //ViewModel追加
        updateObserver = Observer<Chat>{
            chatAdapter.updateList(it)
            Log.d("OBSERVE UPDATE",it.toString())
        }
        readObserver =Observer<String>{
            chatAdapter.updateRead(it)

        }
        mainViewModel.liveChat.observeForever(updateObserver)
        mainViewModel.read.observeForever(readObserver)
    }

    override fun onDestroy() {
        mainViewModel.liveChat.removeObserver(updateObserver)
        mainViewModel.read.removeObserver(readObserver)
        super.onDestroy()
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