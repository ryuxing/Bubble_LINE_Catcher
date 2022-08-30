package com.ryuxing.bubblelinecatcher.data

import com.ryuxing.bubblelinecatcher.service.NotificationService

class Manager(db: Database) {
    val db = db
    val cDao = db.ChatDao()
    val mDao = db.ChatMessageDao()

    //メッセージがきたときに使う
    inner class addMessage(chat: Chat, msg: ChatMessage) :Thread(){
        val chat = chat
        val msg  = msg
        constructor(chat: Chat, msg: ChatMessage,start: Boolean =true) : this(chat,msg) {
            this.start()
        }
        override fun run() {
            cDao.addChat(chat)
            mDao.addMsg(msg)
            //ブロードキャストかlivedataかでUI側にも送信
        }
    }

    //チャットの削除時
    fun removeChat(chatId:String){
        cDao.deleteChat(chatId)
        mDao.deleteChatMessages(chatId)
    }
    //既読時の通知
    fun read(chatId:String){
        NotificationService.removeMessages(chatId)
        cDao.readChat(chatId)
    }

}