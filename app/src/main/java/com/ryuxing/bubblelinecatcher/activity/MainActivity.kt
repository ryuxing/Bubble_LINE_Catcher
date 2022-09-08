package com.ryuxing.bubblelinecatcher.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.databinding.ActivityMainBinding
import com.ryuxing.bubblelinecatcher.livedata.MainViewModel
import com.ryuxing.bubblelinecatcher.viewControl.ChatRecyclerAdapter
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


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
                    if (pos == 0) rv.smoothScrollToPosition(0)
        }
        readObserver =Observer<String>{
                    chatAdapter.updateRead(it)
        }
        mainViewModel.liveChat.observeForever(updateObserver)
        mainViewModel.read.observeForever(readObserver)
        //menu登録
        registerForContextMenu(rv)
        Log.d("onCreated__MainActivity","created.")
    }

    override fun onStart() {
        super.onStart()
        //通知の権限確認
        permissionGrantredAction()

    }
    override fun onDestroy() {
        mainViewModel.liveChat.removeObserver(updateObserver)
        mainViewModel.read.removeObserver(readObserver)
        super.onDestroy()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        menuInflater.inflate(R.menu.menu_chat_item,menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
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
    private fun permissionGrantredAction():Boolean {
        val sets = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (sets != null && sets.contains(packageName)) {
            return true
        } else {
            //ToDo String対応とマテリアル対応
            val warn_string ="no permission."
            val mySnackbar = Snackbar.make(findViewById(R.id.main_coordinate_layout), "no permission.", Snackbar.LENGTH_INDEFINITE)
            mySnackbar.setBackgroundTint(getColor(R.color.md_theme_dark_error))
            mySnackbar.setTextColor(getColor(R.color.md_theme_dark_errorContainer))
            mySnackbar.setAction("権限を付与", View.OnClickListener {
                //ToDo 元に戻す & InitをManifestで書き換える
                    startForResult.launch(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
                //startActivity(Intent(this,InitActivity::class.java))
                    mySnackbar.dismiss()

            })
            mySnackbar.show()
            return false
        }
    }
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult? ->
            if(permissionGrantredAction()){
                val mySnackbar = Snackbar.make(findViewById(R.id.main_coordinate_layout), "Permission Granted.", Snackbar.LENGTH_SHORT)
                mySnackbar.show()
            }
        }


}