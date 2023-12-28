package com.ryuxing.bubblelinecatcher.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.runtime.currentRecomposeScope
import androidx.core.net.toUri
import androidx.core.view.size
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.ChatPhoto
import com.ryuxing.bubblelinecatcher.databinding.ActivityChatListImageBinding
import com.ryuxing.bubblelinecatcher.livedata.ChatImageViewModel
import com.ryuxing.bubblelinecatcher.viewControl.ImageListRecyclerAdapter
import kotlinx.coroutines.*

class ChatImageListActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatListImageBinding
    private lateinit var rv : RecyclerView
    private lateinit var chatId : String
    lateinit var chatImageList :ChatImageViewModel
    companion object{
        lateinit var roottree : DocumentFile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //IntentからChatId取得
        val chatIdFromIntent = intent.getStringExtra("chatId")
        if(chatIdFromIntent==null) finish()
        else chatId = chatIdFromIntent
        val actionBar = supportActionBar
        val room = App.dataManager.cDao.getChat(chatId).first()
        actionBar!!.title = room.chatName

        //DocumentTreeの初期化
        roottree = DocumentFile.fromTreeUri(this, ("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fjp.naver.line.android").toUri()) ?: return

        //layoutのアタッチ
        binding = ActivityChatListImageBinding.inflate(layoutInflater).apply { setContentView(this.root) }
        val imageListAdapter = ImageListRecyclerAdapter(chatId, this)
        val layoutManager = GridLayoutManager(this,4)
        rv = binding.chatImageListRecycler
        rv.setHasFixedSize(false)
        rv.layoutManager = layoutManager
        rv.adapter = imageListAdapter

        //ImageListの取得処理を開始
        imageListAdapter.submitList(listOf<ChatPhoto>())
        chatImageList = ChatImageViewModel(chatId)
        chatImageList.images.observe(this, Observer {
            //ToDo("ListをＡｄａｐｔｅｒに通知")
            imageListAdapter.submitList(ArrayList(it?.values?.toList()?: listOf<ChatPhoto>()))
            //暫定全変更
            if(chatImageList.status ==ChatImageViewModel.STATUS_FINISHED) {
                findViewById<ProgressBar>(R.id.chat_image_list_progressbar).visibility =
                    ProgressBar.GONE
                findViewById<TextView>(R.id.chat_image_list_counter).visibility =
                    TextView.GONE
                Log.w("Fin","FIN")
                lifecycleScope.launch {
                    val job = async { delay(200)}
                    job.await()
                    findViewById<RecyclerView>(R.id.chat_image_list_recycler).scrollToPosition(imageListAdapter.itemCount-1)

                }
            }
        })
        chatImageList.currentCounter.observe(this, Observer{
            findViewById<TextView>(R.id.chat_image_list_counter).text = "Loading... ${it} / ${chatImageList.lastCount}"

        })
        chatImageList.loadImageList()

    }

    override fun onDestroy() {
        super.onDestroy()
        chatImageList.images.removeObservers(this)
        chatImageList.cancelLoading()
    }
}