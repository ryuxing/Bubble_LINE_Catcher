package com.ryuxing.bubblelinecatcher.viewControl

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.activity.ChatImageListActivity
import com.ryuxing.bubblelinecatcher.data.ChatPhoto
import com.ryuxing.bubblelinecatcher.data.MyDate
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class ImageListRecyclerAdapter(chatId:String, val context: Context): ListAdapter<ChatPhoto,ImageListViewHolder>(DIFF_UTIL) {
    companion object{
        val DIFF_UTIL = object : DiffUtil.ItemCallback<ChatPhoto>(){
            override fun areItemsTheSame(oldItem: ChatPhoto, newItem: ChatPhoto): Boolean {
                Log.d("DIFF_UTIL","areItemSame")
                return oldItem.getName() == newItem.getName()
            }

            override fun areContentsTheSame(oldItem: ChatPhoto, newItem: ChatPhoto): Boolean {
                Log.d("DIFF_UTIL","areContentSame")
                return oldItem.equals(newItem)
            }

            override fun getChangePayload(oldItem: ChatPhoto, newItem: ChatPhoto): Any? {
                return super.getChangePayload(oldItem, newItem)
            }
        }
    }
    val chatId = chatId
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image_list, parent, false)
        return ImageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        var imageItem = getItem(position)
        holder.date.text = MyDate.toTextDate(imageItem.lastModified)
        if(imageItem.type == ChatPhoto.TYPE_IMAGE){
        imageItem?.let {
            Picasso.get().load(it.getMain())
                .fit()
                .centerCrop()
                .into(holder.image)
        }}
        //TODO("アイテムのイベントリスナーを追加")
    }

}

