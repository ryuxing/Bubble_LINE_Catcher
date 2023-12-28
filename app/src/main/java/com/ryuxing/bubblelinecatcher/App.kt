package com.ryuxing.bubblelinecatcher

import android.app.Application
import android.content.Context
import android.provider.DocumentsContract
import android.util.Log
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import com.ryuxing.bubblelinecatcher.data.Manager
import com.ryuxing.bubblelinecatcher.data.Database

class App :Application() {
    companion object{
        lateinit var db: Database
        lateinit var dataManager : Manager
        lateinit var context : Context
        val treeUri = DocumentsContract.buildTreeDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Android/data/jp.naver.line.android")
        val dataUri = DocumentsContract.buildDocumentUriUsingTree(treeUri,DocumentsContract.getTreeDocumentId(treeUri))
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            Database::class.java,
            "chat-database"
        )
            .allowMainThreadQueries()
            .build()
        dataManager = Manager(db)
        context = applicationContext
        //ダイナミックカラー
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}