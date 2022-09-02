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
        fun removeChatViewModel(chatId: String) {
            chatViewModelList.remove(chatId)
        }
        fun addMessageToChatView(message:ChatMessage){
            if(chatViewModelList.containsKey(message.chatId)){
                chatViewModelList[message.chatId]!!.addMessage(message)
            }
            Log.d("CVM_addMessageToChatView",message.toString())
        }
    }
    val messages = MutableLiveData<HashMap<Long,ChatMessage>>()

    fun addMessage(message:ChatMessage){
        var msgList = messages.value ?: HashMap<Long,ChatMessage>()
        msgList[message.msgId] = message
        messages.value = msgList
        Log.d("CVM_addMessage", message.chatId + " , "+message.msgId)
    }
    fun receiveMessage(){
        messages.value = HashMap<Long,ChatMessage>()
    }
}