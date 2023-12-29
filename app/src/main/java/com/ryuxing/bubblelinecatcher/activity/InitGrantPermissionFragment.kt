package com.ryuxing.bubblelinecatcher.activity

import android.app.NotificationManager
import android.app.NotificationManager.BUBBLE_PREFERENCE_ALL
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.core.app.NotificationManagerCompat
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import java.io.File
import java.lang.Exception

class InitGrantPermissionFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_init_grant_permission, container, false)
        //通知リスナーの許可設定
        val notificationButton : Button = view.findViewById<Button>(R.id.init_permission_notification_acccess_button)
        notificationButton.setOnClickListener(View.OnClickListener { _ ->
            startNotificationForResult.launch(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        })
        //バブル設定の許可設定
        val bubbleButton : Button = view.findViewById(R.id.init_permission_notification_bubble_setting_button)
        bubbleButton.setOnClickListener(View.OnClickListener { _->
            //ToDo アプリの通知許可設定
            val intent = Intent().also {
                it.action = Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS
                it.putExtra(Settings.EXTRA_APP_PACKAGE,requireContext().packageName)
            }
            startBubbleForResult.launch(intent)
        })
        //ストレージアクセス
        val storagePermissionButton : Button = view.findViewById(R.id.init_permission_notification_storage_permission_button)
        storagePermissionButton.setOnClickListener(View.OnClickListener { _->
            val intent =Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI,"Android/data/jp.naver.line.android")
            }
            startDocumentForResult.launch(intent)
        })
        //ファイル読み込み
        val restoreButton :Button = view.findViewById(R.id.init_permission_notification_restore_button)
        restoreButton.setOnClickListener(View.OnClickListener { _->
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                setType("*/*")
            }
        startRestoreForResult.launch(intent)
        })
        return view
    }

    override fun onStart() {
        Log.d("START","START")
        super.onStart()

    }
    override fun onResume() {
        super.onResume()
        Log.d("START","Resume")

    }
    //通知リスナー許可時の動作
    val startNotificationForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult? ->
            val sets = NotificationManagerCompat.getEnabledListenerPackages(requireContext())
            if (sets != null && sets.contains(requireContext().packageName)) {
                Toast.makeText(requireContext(),"Notification Access Granted.",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Notification Access DENIED!!!!",Toast.LENGTH_SHORT).show()
            }
        }
    //バブル設定時の動作
    @RequiresApi(Build.VERSION_CODES.S)
    val startBubbleForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult? ->
            val manager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if(manager.bubblePreference == BUBBLE_PREFERENCE_ALL){
                Toast.makeText(requireContext(),"Bubble Access Granted.",Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(requireContext(),"Bubble Access DENIED!!!!",Toast.LENGTH_SHORT).show()
            }
        }
    //DocumentTree取得時の動作
    val startDocumentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult? ->
            val uri = result?.data?.data
            if(uri == null || !uri.toString().equals("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata%2Fjp.naver.line.android")){
                Toast.makeText(requireContext(),"Document access FAILED!!!!",Toast.LENGTH_SHORT).show()
            }else {
                val contentResolver =
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                Log.d("ContentResolverReturnCode", contentResolver.toString())
                Toast.makeText(requireContext(), "Document access Granted.", Toast.LENGTH_SHORT).show()
            }
        }
    val startRestoreForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult? ->
        val dbUri = result?.data?.data ?: null
        if (dbUri == null) {
            Log.d("FilePick", "Failed.")
            Toast.makeText(requireContext(),"File access FAILED!!!!",Toast.LENGTH_SHORT).show()
        }else {
            try{
                var file  = File(requireActivity().externalCacheDir,"old_db.db")
                val input = requireActivity().contentResolver.openInputStream(dbUri)?.readBytes()?: byteArrayOf()
                file.writeBytes(input)
                App.dataManager.insertOldMessages(file)
                Toast.makeText(requireContext(),"File access success.",Toast.LENGTH_SHORT).show()

            } catch (e:Exception){
                Toast.makeText(requireContext(),"DB Restore FAILED!!!!",Toast.LENGTH_SHORT).show()
                Log.w("EXCEPTION", e.stackTraceToString())
            }
        }

    }
}