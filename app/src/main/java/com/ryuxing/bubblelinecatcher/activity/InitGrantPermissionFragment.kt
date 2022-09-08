package com.ryuxing.bubblelinecatcher.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.ryuxing.bubblelinecatcher.R

class InitGrantPermissionFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_init_grant_permission, container, false)
    }

    override fun onStart() {
        Log.d("START","START")
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        Log.d("START","Resume")

    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult? ->
            var permission=false
            if(permission){

            }
        }
}