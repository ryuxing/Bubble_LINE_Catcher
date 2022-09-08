package com.ryuxing.bubblelinecatcher.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ryuxing.bubblelinecatcher.R

/**
 * A simple [Fragment] subclass.
 * Use the [InitDescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InitDescriptionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_init_description, container, false)
    }
    override fun onStart() {
        Log.d("START","START")
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d("START","Resume")

    }

}