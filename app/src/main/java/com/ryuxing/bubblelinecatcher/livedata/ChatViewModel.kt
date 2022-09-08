package com.ryuxing.bubblelinecatcher.livedata

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryuxing.bubblelinecatcher.data.ChatMessage

class ChatViewModel: ViewModel() {
    companion object{
        var chatViewModelList = HashMap<String,ChatViewModel>()
        fun getChatViewModel(chatId:String):ChatViewModel{
            if(!chatViewModelList.contains(chatId)) {
                val viewModel = ChatViewModel()
                chatViewModelList[chatId] = viewModel
            }
            return chatViewModelList[chatId]!!
        }
        fun unbindChatViewModel(chatId: String) {
            if(chatViewModelList[chatId]!!.unregister()) chatViewModelList.remove(chatId)
        }
        fun addMessageToChatView(message:ChatMessage){
            if(chatViewModelList.containsKey(message.chatId)){
                chatViewModelList[message.chatId]!!.addMessage(message)
            }
            Log.d("CVM_addMessageToChatView",message.toString())
        }
    }
    val messages = MutableLiveData<HashMap<Long,ChatMessage>>()
    var observerCount = 0
    var receiveCount = 0
    fun addMessage(message:ChatMessage){
        var msgList = messages.value ?: HashMap<Long,ChatMessage>()
        msgList[message.msgId] = message
        messages.value = msgList
        receiveCount = 0
        Log.d("CVM_addMessage", message.chatId + " , "+message.msgId)
    }
    fun receiveMessage(hash:Int){
        Log.d("RECEIVE_FROM","Receive from ${hash}.  obs = ${observerCount} ,rcv = ${receiveCount}")
        if(observerCount <= receiveCount){
            messages.value = HashMap<Long,ChatMessage>()
        }
        receiveCount+=1
    }
    fun register(){

        observerCount+=1
    }
    fun unregister():Boolean{
        observerCount-=1
        return observerCount<=0
    }
}