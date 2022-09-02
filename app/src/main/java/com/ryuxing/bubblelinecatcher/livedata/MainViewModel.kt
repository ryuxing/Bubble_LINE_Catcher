package com.ryuxing.bubblelinecatcher.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ryuxing.bubblelinecatcher.data.Chat


class MainViewModel:ViewModel() {
    val liveChat = MutableLiveData<Chat>()
    val read = MutableLiveData<String>()
    fun updateChat(chat:Chat){
        liveChat.value = chat
    }
    fun updateRead(chatId:String){
        read.value = chatId
    }
}