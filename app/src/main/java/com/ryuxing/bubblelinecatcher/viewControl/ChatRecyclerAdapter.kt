package com.ryuxing.bubblelinecatcher.viewControl

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.getDrawable
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

class ChatRecyclerAdapter() : RecyclerView.Adapter<ChatViewHolder>() {
    var roomList = ArrayList<String>()
    var chatList = updateFromDatabase().toMutableList()
    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        //viewを作る
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent,false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        //viewにデータをbindする
        var chat = chatList[position]
        holder.lastDate.text = chat.getTimeString()
        holder.chatName.text = chat.chatName
        //本文にSenderを入れるかの分岐
        var message = chat.lastMsg
        if(chat.isGroup){
            holder.chatName.setCompoundDrawables(getDrawable(App.context,R.drawable.ic_baseline_group_24),null,null,null)
            //holder.chatName.text = "\uD83D\uDC65 "+ chat.chatName
            message = chat.lastSenderName + ": "+message
        }else{
            holder.chatName.setCompoundDrawables(null,null,null,null)
        }
        holder.lastMsg.text = message

        if(!chat.hasUnread){
            holder.unreadBadge.visibility = View.INVISIBLE
        }else{
            holder.unreadBadge.visibility = View.VISIBLE
        }
        //Imageをここで取得
        var icon :Bitmap
        try {
            val stream: InputStream = FileInputStream(File(chat.lastSenderIcon))
            icon = BitmapFactory.decodeStream(BufferedInputStream(stream))
            holder.chatIcon.setImageBitmap(icon)
            stream.close()
        }catch (e :Exception){
            holder.chatIcon.setImageResource(R.drawable.person_icon)

        }

        //Listenerをここで作る
        holder.wrapepr.setOnClickListener(View.OnClickListener { view ->
            val intent = Intent(view.context,ChatActivity::class.java)
            intent.putExtra("chatId",chat.chatId)
            intent.data = Uri.parse("bubbledline://catcher.ryuxing.com/chat/${chat.chatId}")
            view.context.startActivity(intent)
        })
        holder.wrapepr.setOnLongClickListener(View.OnLongClickListener{ view ->
            setPos(holder.layoutPosition)
            Log.d("LongPress","index.toString()")
            return@OnLongClickListener false
        })
    }

    override fun getItemCount() = chatList.size
    fun updateFromDatabase():MutableList<Chat>{
        var list = App.dataManager.cDao.getAllChats()
        for(chat in list){
            roomList.add(chat.chatId)
        }
        Log.d("ChatRecycler_UPDATED",list.size.toString())
        return list.toMutableList()
    }
    fun reload(){
        chatList = updateFromDatabase()
        notifyDataSetChanged()
        Log.d("ChatRecycler_RELOAD","reloaded.")
    }
    fun updateList(chat:Chat){
        val id = chat.chatId
        var index = roomList.indexOf(id)
        Log.d("index",index.toString())

        if(chatList.lastIndex < index ){
            Log.d("index","OUT_OF_RANGE. reload.")
            reload()
        }
        else if(index==-1){
            //追加処理
            chatList.add(0,chat)
            roomList.add(0,id)
            notifyItemInserted(0)

        }
        else if(chatList[index].chatId!=id){
            Log.d("index","COMPLEXED. reload." + chatList[index]+" and "+ id)

            reload()
        }
        else if(index==0){
            chatList[0] = chat
            notifyItemChanged(0)
        }
        else{
            chatList.removeAt(index)
            roomList.removeAt(index)
            chatList.add(0,chat)
            roomList.add(0,id)
            notifyItemMoved(index,0)
            notifyItemChanged(0,{})
        }
    }
    fun updateRead(id:String){
        val index = roomList.indexOf(id)
        if(index==-1) return
        chatList[index].hasUnread = false
        notifyItemChanged(index)

    }
    fun deleteChat(chatId:String){
        val index = roomList.indexOf(chatId)
        if(index==-1) return
        chatList.removeAt(index)
        roomList.removeAt(index)
        notifyItemRemoved(index)

    }
    fun setPos(pos:Int){
        this.selectedPosition = pos
    }
    fun getPos():Int{
        return this.selectedPosition
    }
    fun getChatId(position: Int): String {
        if(position<0) return ""
        return roomList[position]
    }

}