package com.ryuxing.bubblelinecatcher.data

import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile

class ChatPhoto {
    companion object{
        const val TYPE_NONE  = 0
        const val TYPE_IMAGE = 1
        const val TYPE_VOICE = 2
    }
    var type: Int = TYPE_NONE;
    var name:String = ""
    var mainDoc:  Uri? = null
    var thumbDoc: Uri? = null
    var lastModified: Long = 0

    constructor(file : DocumentFile){
        lastModified = file.lastModified()
        var filename = file.name?:"0"
        name = filename.replace(Regex("[^0-9]"),"")
        if (filename.contains("voice")){
            type = TYPE_VOICE
            mainDoc = file.uri
        } else {
            type = TYPE_IMAGE
            if (filename.contains(".thumb")){
                thumbDoc = file.uri
            } else {
                mainDoc = file.uri
            }
        }
    }

    fun merge(merge: ChatPhoto?) {
        if (merge == null ||type != TYPE_IMAGE || merge.type != TYPE_IMAGE){
            return
        }
        if (merge.mainDoc  != null) mainDoc  = merge.mainDoc
        else if (merge.thumbDoc != null) {
            thumbDoc = merge.thumbDoc
            lastModified = merge.lastModified
        }
    }
    fun getThumb() = if (thumbDoc==null) thumbDoc else mainDoc
    fun getMain()  = if (mainDoc==null) mainDoc else thumbDoc
    fun getName(): Long {
        return name.toLong()
    }
    override fun equals(other: Any?): Boolean {
        Log.d("comparable","${name} and ${(other as ChatPhoto).name} : ${(other is ChatPhoto) && (other.type == type && other.mainDoc?.equals(mainDoc)?: false && other.thumbDoc?.equals(thumbDoc) ?: false)}")
        //ToDo ("比較処理")
        return if(other is ChatPhoto){
            (other.type == type && other.mainDoc?.equals(mainDoc)?: false && other.thumbDoc?.equals(thumbDoc) ?: false)
        } else false
    }
}