package com.ryuxing.bubblelinecatcher.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.activity.ChatActivity
import com.ryuxing.bubblelinecatcher.data.Chat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception

class ChatRecyclerAdapter(val list: List<Chat>) : RecyclerView.Adapter<ChatViewHolder>() {
    var roomList = ArrayList<String>()
    var chatList = list.toMutableList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //viewを作る
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        //viewにデータをbindする
        var chat = chatList[position]
        roomList.add(chat.chatId)
        holder.lastDate.text = chat.getTimeString()
        holder.chatName.text = chat.chatName
        //本文にSenderを入れるかの分岐
        var message = chat.lastMsg
        if(chat.isGroup) message = chat.lastSenderName + ": "+message
        holder.lastMsg.text = message

        if(!chat.hasUnread){
            holder.unreadBadge.visibility = View.INVISIBLE
        }
        //Imageをここで取得
        var icon :Bitmap
        try {
            val stream: InputStream = FileInputStream(File(chat.lastSenderIcon))
            icon = BitmapFactory.decodeStream(BufferedInputStream(stream))
            holder.chatIcon.setImageBitmap(icon)
        }catch (e :Exception){
            holder.chatIcon.setImageResource(R.drawable.person_icon)

        }

        //Listenerをここで作る
        holder.wrapepr.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(view.context,ChatActivity::class.java)
            intent.putExtra("chatId",chat.chatId)
            intent.data = Uri.parse("bubbledline://catcher.ryuxing.com/chat/${chat.chatId}")
            view.context.startActivity(intent)
            //既読処理
        })
    }

    override fun getItemCount() = list.size

    fun updateList(chat:Chat){
        val id = chat.chatId
        val index = roomList.indexOf(id)

        if(index==-1){
            //追加処理
            chatList.add(0,chat)
            roomList.add(0,id)
            notifyItemInserted(0)

        }
        else{
            chatList.removeAt(index)
            roomList.removeAt(index)
            chatList.add(0,chat)
            roomList.add(0,id)
            notifyItemMoved(index,0)
            notifyItemChanged(0)
        }
    }
    fun updateRead(id:String){
        val index = roomList.indexOf(id)
        if(index==-1) return
        chatList[index].hasUnread = false
        notifyItemChanged(index)

    }

}