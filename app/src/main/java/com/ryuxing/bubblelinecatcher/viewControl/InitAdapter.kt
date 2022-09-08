package com.ryuxing.bubblelinecatcher.viewControl

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ryuxing.bubblelinecatcher.activity.InitDescriptionFragment
import com.ryuxing.bubblelinecatcher.activity.InitGrantPermissionFragment

class InitAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int =3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> InitDescriptionFragment()
            1 -> InitGrantPermissionFragment()
            else ->InitDescriptionFragment()
        }
    }
}