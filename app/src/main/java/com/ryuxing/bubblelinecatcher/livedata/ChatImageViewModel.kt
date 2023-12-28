package com.ryuxing.bubblelinecatcher.livedata;

import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuxing.bubblelinecatcher.activity.ChatImageListActivity
import com.ryuxing.bubblelinecatcher.data.ChatPhoto
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ChatImageViewModel(chatId:String): ViewModel(){
        companion object{
                val STATUS_NOT_WORK = 0
                val STATUS_GETTING = 1
                val STATUS_FILTERING = 2
                val STATUS_SORTING = 3
                val STATUS_FINISHED = 10
        }
        val chatId = chatId
        val images = MutableLiveData<TreeMap<Long,ChatPhoto>>()
        var status = STATUS_NOT_WORK
        var lastCount = images.value?.size ?:-1
        val currentCounter = MutableLiveData<Int>()
        var job : Job? = null
        fun loadImageList(){
                status = STATUS_NOT_WORK
                //DocumentFileからリストを取得
                job = viewModelScope.launch(Dispatchers.Default) {
                        Log.d("VIEWMODEL","Coroutine Start")
                        val files = ChatImageListActivity.roottree
                                .findFile("files")
                                ?.findFile("chats")
                                ?.findFile(chatId)
                                ?.findFile("messages")
                        Log.d("VIEWMODEL_LIST",files?.name.toString())


                        status = STATUS_GETTING
                        Log.d("VIEWMODEL","Coroutine 1/2 Listing...")
                        val fileList = files?.listFiles()?: arrayOf<DocumentFile>()
                        if (fileList.size == lastCount) images.postValue(images.value)
                        lastCount = files?.listFiles()?.size?:-1
                        Log.d("VIEWMODEL","Coroutine 1/2 Listed Finished. ${fileList.size}")
                        //ここから取得したファイルを型変換する
                        status = STATUS_FILTERING
//                        val filtered = fileList?.asSequence<DocumentFile>()?.filter { return@filter (it.name)?.contains(".thumb") ?: false }?.toList<DocumentFile>()?: listOf<DocumentFile>()
                        var count = 0
                        val filtered = TreeMap<Long, ChatPhoto>()
                        val awaitList = mutableListOf<Deferred<Any?>>()
                        val tmpList : MutableList<ChatPhoto?> = MutableList(fileList.size){null}
                        currentCounter.postValue(0)
                        for (file in fileList) {

                                awaitList.add(async {
                                         val index = fileList.indexOf(file)
                                        val cp = ChatPhoto(file)
                                        tmpList[index]=cp

                                })
                        }
                        awaitList.awaitAll()
                        Log.d("VIEWMODEL", "${tmpList.size}")
                        count = 0
                        for(cp in tmpList){
                                count++
                                currentCounter.postValue(count)
                                if (cp == null) continue
                                Log.d("VIEWMODEL", "${count}")
                                if (!filtered.contains(cp.getName())) {
                                        filtered[cp.getName()] = cp
                                } else {
                                        filtered[cp.getName()]?.merge(cp)
                                }
                        }
                        Log.d("VIEWMODEL","Coroutine 2/2 Filter Finished")
                        status = STATUS_FINISHED
                        Log.d("VIEWMODEL", "Coroutine ${filtered.size}")
                        images.postValue(filtered)


                }

        }
        fun cancelLoading(){
                job?.cancelChildren(null)
        }
}
