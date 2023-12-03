package com.ryuxing.bubblelinecatcher.viewControl

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ryuxing.bubblelinecatcher.R

class ImageListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.image_list_item_image)
    val date  = itemView.findViewById<ImageView>(R.id.image_list_item_date)
}