package com.ryuxing.bubblelinecatcher.viewControl

import android.app.ActionBar
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import com.ryuxing.bubblelinecatcher.data.MyDate
import com.ryuxing.bubblelinecatcher.data.Sticker
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception

class MessageRecyclerAdapter(val list: List<ChatMessage>) : RecyclerView.Adapter<MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message,parent,false)
        return MessageViewHolder(view)
    }
    val messageList = list.toMutableList()
    val mesIdList = ArrayList<Long>()
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        var message = messageList[position]
        mesIdList.add(message.msgId)
        holder.messageDate.text = MyDate.toTextDate(message.date)
        //送信者によって条件を分岐
        if(message.sender == "me"){
            holder.messageName.visibility = View.GONE
            holder.messageIconCard.visibility = View.GONE
            holder.messageText.setBackgroundResource(R.drawable.message_outgoing)
            //色の指定
            //holder.messageText.backgroundTintMode = R.color.chat_sender
            holder.messageText.setPadding(5,5,15,5)
            val cs = ConstraintSet()
            cs.clone(holder.wrapper)
            cs.setHorizontalBias(R.id.message_content_card,1.toFloat())
            cs.applyTo(holder.wrapper)
        }else{
            holder.messageName.text = message.sender
            var icon : Bitmap
            try {
                val stream: InputStream = FileInputStream(File( App.context.externalCacheDir,"icon_"+message.msgId))
                icon = BitmapFactory.decodeStream(BufferedInputStream(stream))
                holder.messageIcon.setImageBitmap(icon)
            }catch (e : Exception){
                holder.messageIcon.setImageResource(R.drawable.person_icon)
                Log.w("MESSAGE_ICON_LOAD_FAILURE",e.stackTraceToString())
            }
        }
        //TextかStickerか
        if(message.isStamp){
            val cardParam = holder.messageCard.layoutParams
            cardParam.width = 450
            cardParam.height= 450
            holder.messageCard.layoutParams = cardParam
            holder.messageText.visibility = View.GONE
            holder.messageSticker.visibility = View.VISIBLE
            val stickerUrl = message.message
            val stickerView = holder.messageSticker
            holder.messageText.text = "スタンプ "+ stickerUrl
            Sticker.setSticker(stickerUrl,stickerView)
        }else{
            holder.messageText.visibility = View.VISIBLE
            val cardParam = holder.messageCard.layoutParams
            cardParam.width = RecyclerView.LayoutParams.WRAP_CONTENT
            cardParam.height= RecyclerView.LayoutParams.WRAP_CONTENT
            val messageParam = holder.messageText.layoutParams
            messageParam.width = ViewGroup.LayoutParams.WRAP_CONTENT

            holder.messageSticker.visibility = View.GONE
            holder.messageCard.layoutParams = cardParam
            holder.messageText.layoutParams = messageParam
            holder.messageText.text = message.message
        }
    }

    override fun getItemCount() = messageList.size

    fun updateMessages(map: HashMap<Long,ChatMessage>):Int{
        var changeIndex = ArrayList<Int>()
        var addCount = 0
        val lastIndex = itemCount
        map.forEach{(mesId,message)->
            val index = mesIdList.indexOf(mesId)
            if(index==-1){
                mesIdList.add(mesId)
                messageList.add(message)
                addCount ++
            }else{
                messageList[index] = message
                changeIndex.add(index)
            }
        }
        for (index in changeIndex){
            notifyItemChanged(index)
        }
        notifyItemRangeInserted(lastIndex,addCount)
        return addCount
    }

}