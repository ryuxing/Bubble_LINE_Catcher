package com.ryuxing.bubblelinecatcher.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//@Entity(tableName = "icons")
data class ChatUserIcon (
    //@PrimaryKey(autoGenerate = true) val iconId :Long,
    val icon:String
    ):Serializable{

}