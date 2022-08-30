package com.ryuxing.bubblelinecatcher.service

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.data.Chat
import com.ryuxing.bubblelinecatcher.data.ChatMessage
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception


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
    }

    //サービス開始処理
    override fun onCreate() {
        super.onCreate()
        IS_SERVICE_RUNNING = true
        //todo:
        // 記録スイッチの最後の状態を取得する
        IS_SERVICE_ENABLED = true
        Log.d("SERVICE_CREATE","Service Started.")
        notificationService.createNotificationChannel(this)

    }
    //サービス終了処理
    override fun onDestroy() {
        super.onDestroy()
        IS_SERVICE_RUNNING = false
        Log.d("SERVICE_DESTROY","Service Destroyed!!!!!")
    }

    @SuppressLint("RestrictedApi")
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        //サービス無効・通知なき時は即return
        if(!IS_SERVICE_ENABLED) return
        if(sbn == null) return
        //通知がLINEかつカテゴリーがmsgの時に処理を継続する
        if(sbn.packageName == "jp.naver.line.android"
            && sbn.notification.category == "msg") {

            Log.d("SERVICE_RECEIVE_LINE", "LINEの通知取得ができた")
            for (str in sbn.notification.extras.keySet()){
                //Log.d("SERVICE_LINE_KEYSET",str)
                //Log.d("SERVICE_LINE_DETAIL",sbn.notification.extras.get(str).toString())
            }
            //メッセージの取得
            //まずは通知の中身を格納する
            val notify = sbn.notification.extras
            val mesId = notify.getString("line.message.id", "0").toLong()
            var icon = notify.get("android.largeIcon") as Icon
            var path = ""
            //Iconをキャッシュファイルに書き込み
            try {
                var image = icon.loadDrawable(this).toBitmap()
                Log.d("IMAGE", image.toString())
                var file  = File( this.externalCacheDir,"icon_"+mesId)
                val baos = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, baos)
                file.writeBytes(baos.toByteArray())
                Log.d("ICON_URI",file.path)
                path = file.path
            }catch (e:Exception){
                Log.w("ERROR While ICON SAVE",e.toString())
            }

            //スタンプだった場合は本文ににURLを入れる
            var isSticker = false
            var content = "" as String
            if(notify.containsKey("line.sticker.url")){
                isSticker = false
                content = notify.get("line.sticker.url").toString()
            }else{
                content = notify.getString("android.text", getString(R.string.text_notification_new_message)) //メッセージ
            }
            //通知メッセージをChatMessageに格納
            var msg = ChatMessage(
                mesId, //メッセージid
                notify.getString("line.chat.id", "0"), //chat id
                content,  //本文
                isSticker, //テキストorスタンプ
                notify.getString("android.title", getString(R.string.text_unknown_member)), //送信者
                sbn.notification.`when` //メッセージ受信時刻
            )
            //ルームID不明はここで切る
            if (msg.chatId == "0") return

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

            //チャットルーム情報を格納
            val chat = Chat(
                msg.chatId, //Chat ID
                notify.getString("android.subText", msg.sender), //Chat Name
                notify.getString("android.subText", "") != "", //グループかどうか
                notify.getString("android.text", getString(R.string.text_main_activity_new_message)),
                msg.sender,
                path,
                msg.date
            )
            //オブジェクトを通知用メッセージリストに格納
            val person = Person.Builder().setName(msg.sender).setIcon(IconCompat.createFromIcon(icon)).build()
            val isExist = NotificationService.addMessage(msg,person)
            if (!isExist) NotificationService.addChat(chat)
            //送信者の情報を格納
            chat.pushShortcut(person, this)
            Log.d("NOTIFICATION_MESSAGE", NotificationService.notifyMessageList.toString())
            notificationService.sendNotification(msg, chat, person, isExist, this)
            App.dataManager.addMessage(chat, msg,start=true)
        }
    }

}