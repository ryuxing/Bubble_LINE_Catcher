package com.ryuxing.bubblelinecatcher.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception
import java.sql.Time
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


class NotificationLoggerService:NotificationListenerService() {
    var notificationService = NotificationService()
    companion object{
        //サービスの開始有無
        var IS_SERVICE_RUNNING = false
        //記録スイッチのON/OFF
        var IS_SERVICE_ENABLED = false
        //記録のスイッチの更新を受付ける
        fun switchServiceEnabled():Boolean{
            IS_SERVICE_ENABLED =
            return IS_SERVICE_ENABLED && IS_SERVICE_RUNNING
        }
        lateinit var context :Context
    }

    //サービス開始処理
    override fun onCreate() {
        super.onCreate()
        IS_SERVICE_RUNNING = true
        //todo:
        // 記録スイッチの最後の状態を取得する
        IS_SERVICE_ENABLED = true
        Log.i("SERVICE_CREATE","Service Started.")
        notificationService.createNotificationChannel(this)
        context = this
    }
    //サービス終了処理
    override fun onDestroy() {
        super.onDestroy()
        IS_SERVICE_RUNNING = false
        Log.i("SERVICE_DESTROY","Service Destroyed")
    }

    @SuppressLint("RestrictedApi")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        //サービス無効・通知なき時は即return
        if(!IS_SERVICE_ENABLED) return
        if(sbn == null) return
        //通知がLINEかつカテゴリーがmsgの時に処理を継続する
        lateinit var msg:ChatMessage
        lateinit var chat:Chat

        val notify = sbn.notification.extras
        var path = ""
        lateinit var icon :Icon

        if(sbn.packageName == "com.kiwibrowser.browser"){
            Log.d("sbn.notification",sbn.notification.toString())
            val mesId = Date().time.milliseconds.toLong(DurationUnit.MICROSECONDS)*100+(Math.random()*100).toLong()
            icon = notify.get("android.largeIcon") as Icon
            //Iconをキャッシュファイルに書き込み
            try {
                var image = icon.loadDrawable(this)!!.toBitmap()
                if(image==null) throw NullPointerException("No image loaded from icon.")
                var file  = File( this.externalCacheDir,"icon_"+mesId)
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, baos)
                file.writeBytes(baos.toByteArray())
                path = file.path
            }catch (e:Exception){
                Log.w("ERROR While ICON SAVE",e.toString())
            }
            val content = notify.getString("android.text", getString(R.string.text_notification_new_message)) //メッセージ
            msg = ChatMessage(
                mesId, //メッセージid
                "🥝"+notify.getString("android.title", getString(R.string.text_unknown_member)), //chat id
                content,  //本文
                false, //テキストorスタンプ
                notify.getString("android.title", getString(R.string.text_unknown_member)), //送信者
                sbn.notification.`when`//メッセージ受信時刻
            )
            //チャットルーム情報を格納
            chat = Chat(
                msg.chatId, //Chat ID
                notify.getString("android.title", msg.sender), //Chat Name
                false,//notify.getString("android.subText", "") != "", //グループかどうか
                notify.getString("android.text", getString(R.string.text_main_activity_new_message)),
                msg.sender,
                path,
                msg.date,
                true
            )


        }else if(sbn.packageName == "jp.naver.line.android"
            && sbn.notification.category == "msg") {

            Log.d("LINE_INTENT",sbn.notification.contentIntent.describeContents().toString())
            /*for (str in sbn.notification.extras.keySet()){
                Log.d("SERVICE_EXTRA_LINE_KEYSET",str)
                Log.d("SERVICE_EXTRA_LINE_DETAIL",sbn.notification.extras.get(str).toString())
            }*/
            //メッセージの取得
            //まずは通知の中身を格納する
            val mesId = notify.getString("line.message.id", "0").toLong()
            icon = notify.get("android.largeIcon") as Icon
            //Iconをキャッシュファイルに書き込み
            try {
                var image = icon.loadDrawable(this)!!.toBitmap()
                if (image == null) throw NullPointerException("No image loaded from icon.")
                var file = File(this.externalCacheDir, "icon_" + mesId)
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, baos)
                file.writeBytes(baos.toByteArray())
                path = file.path
            }catch (e:Exception){
                Log.w("ERROR While ICON SAVE__NotificationLoggerService",e.stackTraceToString())
            }

            //スタンプだった場合は本文ににURLを入れる
            var isSticker = false
            var content = "" as String
            if (notify.containsKey("line.sticker.url")) {
                isSticker = true
                content = notify.get("line.sticker.url").toString()
            } else {
                content = notify.getString(
                    "android.text",
                    getString(R.string.text_notification_new_message)
                ) //メッセージ
            }
            //通知メッセージをChatMessageに格納
            msg = ChatMessage(
                mesId, //メッセージid
                notify.getString("line.chat.id", "0"), //chat id
                content,  //本文
                isSticker, //テキストorスタンプ
                notify.getString("android.title", getString(R.string.text_unknown_member)), //送信者
                sbn.notification.`when` //メッセージ受信時刻
            )
            //ルームID不明はここで切る
            if (msg.chatId == "0") return

            //チャットルーム情報を格納
            chat = Chat(
                msg.chatId, //Chat ID
                notify.getString("android.subText", msg.sender), //Chat Name
                notify.getString("android.subText", "") != "", //グループかどうか
                notify.getString("android.text", getString(R.string.text_main_activity_new_message)),
                msg.sender,
                path,
                msg.date,
                true
            )

        }else return
        //チャットのメッセージが通知からキャンセルされているか見る
        var notifs = super.getActiveNotifications()
        val hash = msg.chatId.hashCode()
        var bool = false
        for (noti in notifs){
            if(noti.id == hash) {
                bool = true
            }
        }
        if(bool == false){
            NotificationService.removeMessages(msg.chatId)
        }

        Log.d("Chat",chat.toString())
        Log.d("Message",msg.toString())
        //オブジェクトを通知用メッセージリストに格納
        val person = Person.Builder().setName(msg.sender).setIcon(IconCompat.createFromIcon(icon)).build()
        val isExist = NotificationService.addMessage(msg,person)
        if (!isExist) NotificationService.addChat(chat)
        //送信者の情報を格納
        chat.pushShortcut(person, this)
        notificationService.sendNotification(msg, chat, person, isExist, this)
        App.dataManager.addMessage(chat, msg,start=true)

    }

}