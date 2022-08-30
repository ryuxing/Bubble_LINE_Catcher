package com.ryuxing.bubblelinecatcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ryuxing.bubblelinecatcher.activity.ChatActivity
import com.ryuxing.bubblelinecatcher.activity.MainActivity
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = false) val chatId : String,
    val chatName : String,
    val isGroup : Boolean,
    val lastMsg : String,
    val lastSenderName: String,
    val lastSenderIcon:String,
    val lastDate: Long,
    val hasUnread  : Boolean = true
):BaseObservable(){
    fun getTimeString():String{
        val time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastDate),ZoneId.systemDefault())
        val ref  = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        var formatter:DateTimeFormatter
        if(time > ref){
            formatter = DateTimeFormatter.ofPattern("HH:mm")
        }else if(time > ref.truncatedTo(ChronoUnit.YEARS)){
            formatter = DateTimeFormatter.ofPattern(" MM/dd(E) HH:mm")
        }else{
            formatter = DateTimeFormatter.ofPattern(" YYYY/MM/dd HH:mm")
        }
        return time.format(formatter)

    }
    fun createPerson(chatId : Long): Person {
        val stream: InputStream = FileInputStream(File(lastSenderIcon))
        val icon = BitmapFactory.decodeStream(BufferedInputStream(stream))
        var person =Person.Builder()
            .setName(lastSenderName)
            .setIcon(IconCompat.createWithBitmap(icon))
            return person.build()
    }
    fun pushShortcut(person : Person, context: Context){
        var shortcut = ShortcutInfoCompat.Builder(context, chatId)
            .setLocusId(LocusIdCompat(chatId))
            .setActivity(ComponentName(context, MainActivity::class.java))
            .setShortLabel(chatName)
            .setIcon(person.icon)
            .setLongLived(true)
            .setIntent(
                Intent(context, ChatActivity::class.java)
                    .setData(
                        Uri.parse(
                            "bubbledline://catcher.ryuxing.com/chat/${chatId}"
                        )
                    ).putExtra("roomId",chatId)
                    .setAction(Intent.ACTION_VIEW)
            )
        if(isGroup){
            shortcut.setPersons(arrayOf(person))
        }else{
            shortcut.setPerson(person)
        }
        ShortcutManagerCompat.pushDynamicShortcut(context,shortcut.build())
    }

}

