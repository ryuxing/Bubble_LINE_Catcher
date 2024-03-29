package com.ryuxing.bubblelinecatcher.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.content.LocusIdCompat
import com.ryuxing.bubblelinecatcher.activity.ChatActivity
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import java.io.File
import java.lang.Exception

class NotificationService {
    companion object {
        val msgList = mutableMapOf<Long, ChatMessage>()
        var chatList = mutableMapOf<String, Chat>()
        var personList = mutableMapOf<Long,Person>()
        var notifyMessageList = mutableMapOf<String, ArrayList<Long>>()
        fun addMessage(msg: ChatMessage, person: Person): Boolean {
            val isExistMessage = msgList.containsKey(msg.msgId)
            msgList.put(msg.msgId, msg)
            personList.put(msg.msgId,person)
            if (!isExistMessage) {
                var list = arrayListOf<Long>()
                if (notifyMessageList.containsKey(msg.chatId)) {
                    list = notifyMessageList.getValue(msg.chatId)
                }
                list.add(msg.msgId)
                notifyMessageList.put(msg.chatId,list)
            }
            return isExistMessage
        }

        fun addChat(chat: Chat) {
            chatList.put(chat.chatId, chat)
        }
        fun removeMessages(chatId:String){
            if(!notifyMessageList.containsKey(chatId)) return
            var list = notifyMessageList.getValue(chatId)
            for (msgId in list) {
                msgList.remove(msgId)
                personList.remove(msgId)
            }
            notifyMessageList.put(chatId,arrayListOf<Long>())

        }
        fun removeChat(chatId: String){
            removeMessages(chatId)
            val notificationManager: NotificationManager =
                NotificationLoggerService.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(chatId.hashCode())

        }
        val channelId_MSG = "MSG"

    }

    fun createNotificationChannel(context: Context): Unit {
        val mChannel = NotificationChannel(
            channelId_MSG,
            context.resources.getString(R.string.channel_MSG),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mChannel.description = context.resources.getString(R.string.channel_MSG_description)
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
    @SuppressLint("RestrictedApi")
    fun sendNotification(
        msg: ChatMessage,
        chat: Chat,
        contact: Person,
        isUpdate: Boolean,
        context: Context,
        suppressNotification:Boolean = false,
        autoExpandBubble:Boolean =false,
        withStockUsing:Boolean =true
    ) {
        var messaging = NotificationCompat.MessagingStyle(contact)
            .setConversationTitle(chat.chatName)
            .setGroupConversation(chat.isGroup)
        if(withStockUsing){
            var chatMsgList = notifyMessageList.getValue(chat.chatId)
            for (msgId in chatMsgList) {
                var msg = msgList.getValue(msgId)
                var senderPerson= personList.getValue(msgId)
                messaging.addMessage(msg.message, msg.date, senderPerson)
            }
        }else{
            messaging.addMessage(msg.message,msg.date,chat.createPerson())
        }
        val pendingIntent = PendingIntent
            .getActivity(
                context,
                0,
                Intent(context, ChatActivity::class.java)
                    .setData(
                        Uri.parse("https://bubbleline.ryuxing.com/chat/${chat.chatId}")
                    )
                    .putExtra("chatId",chat.chatId),
                PendingIntent.FLAG_MUTABLE
            )

        val builder = NotificationCompat.Builder(context, channelId_MSG)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setShortcutId(msg.chatId)
            .setLocusId(LocusIdCompat(chat.chatId))
            .addPerson(contact)
            .setShowWhen(true)
            .setStyle(messaging)
            .setColor(ContextCompat.getColor(context, R.color.ic_launcher_background))
            .setGroup("messages")
            .setContentIntent(pendingIntent)
            .setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(pendingIntent, contact.icon!!)
                    .setSuppressNotification(suppressNotification)
                    .setAutoExpandBubble(autoExpandBubble)
                    .setDesiredHeight(600)
                    .build()
            )
        if (isUpdate) builder.setOnlyAlertOnce(true)

        //アイコンファイルの取得
        var bitmap :Bitmap
        try {
            val source  = ImageDecoder.createSource(File(context.externalCacheDir,"icon_"+msg.chatId))
            bitmap = ImageDecoder.decodeBitmap(source)
        }catch(e: Exception){
            //代替画像を指定
            Log.w("IMAGE_CATCH_ERROR__NotificationService", e.stackTraceToString())
            bitmap = BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_launcher_round
            )
        }
        builder.setLargeIcon(bitmap)
            .setSmallIcon(R.drawable.icon_lancher_foreground)

        //通知発行
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(chat.chatId.hashCode(), builder.build())
        val summary = NotificationCompat.Builder(context, channelId_MSG)
            .setSmallIcon(R.drawable.icon_lancher_foreground)
            .setContentTitle(context.getString(R.string.text_main_activity_new_message))
            .setGroup("messages")
            .setGroupSummary(true)
            .build()
        notificationManager.notify(9999,summary)

    }

}



