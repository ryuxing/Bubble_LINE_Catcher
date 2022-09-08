package com.ryuxing.bubblelinecatcher.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.ryuxing.bubblelinecatcher.R
import com.ryuxing.bubblelinecatcher.viewControl.InitAdapter

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        val pager = findViewById<ViewPager2>(R.id.init_pager2)
        val adapter = InitAdapter(this)
        pager.adapter = adapter
    }
}