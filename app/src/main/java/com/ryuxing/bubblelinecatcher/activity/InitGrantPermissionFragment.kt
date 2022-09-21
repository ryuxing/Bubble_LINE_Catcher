package com.ryuxing.bubblelinecatcher.activity

import android.app.NotificationManager
import android.content.Context
import android.content.Context.STORAGE_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R

class InitGrantPermissionFragment : Fragment() {
    val regFile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ res: ActivityResult?->
        val uri = res?.data?.data
        Log.d("uri",uri.toString())
        Log.d("uriApp",App.dataUri.toString())
        var result = (uri == App.treeUri)
        if(result){
            App.context.contentResolver.takePersistableUriPermission(uri!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resPermissionWithToast(result,R.id.init_grant_notification_file_access_status)
    }
    val regNotifyLog = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ _->
        val sets = NotificationManagerCompat.getEnabledListenerPackages(activity?:App.context)
        resPermissionWithToast((sets != null && sets.contains(activity?.packageName)),R.id.init_grant_notification_file_access_status)
    }
    val regBubbleLog = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        val manager = App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                manager.bubblePreference== NotificationManager.BUBBLE_PREFERENCE_ALL
            }else{
                manager.areBubblesAllowed()
            }
        resPermissionWithToast(permission,R.id.init_grant_bubble_notification_status)
    }
    fun resPermissionWithToast(isGrant:Boolean,id:Int){
        val view = view?.findViewById<TextView>(id)
        if(isGrant){
            Toast.makeText(context,R.string.init_permission_allowed,Toast.LENGTH_SHORT).show()
            view?.setText(R.string.init_permission_check_okay)
        } else{
            Toast.makeText(context,R.string.init_permission_denied,Toast.LENGTH_SHORT).show()
            view?.setText(R.string.init_permission_check_not_granted)

        }
    }
    fun resPermission(isGrant: Boolean,id:Int){
        val view = view?.findViewById<TextView>(id)
        if(isGrant){
            view?.setText(R.string.init_permission_check_okay)
            //view?.setTextColor(R.colo)
        }else{
            view?.setText(R.string.init_permission_check_not_granted)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_init_grant_permission, container, false)
    }

    override fun onStart() {
        Log.d("START","START")
        view?.findViewById<Button>(R.id.init_grant_file_access_button)?.setOnClickListener {
            val sm = App.context.getSystemService(STORAGE_SERVICE) as StorageManager
            val smpsv =  sm.primaryStorageVolume
            val uri = DocumentsContract.buildDocumentUri(
                "com.android.externalstorage.documents",
                "primary:Android/data/jp.naver.line.android")

            Toast.makeText(context,"「このフォルダを使用」を押してください。",Toast.LENGTH_LONG).show()
            regFile.launch(smpsv.createOpenDocumentTreeIntent().putExtra(DocumentsContract.EXTRA_INITIAL_URI,uri))
        }
        view?.findViewById<Button>(R.id.init_grant_notification_access_button)?.setOnClickListener{
            Toast.makeText(context,"Bubble Lineが通知を読み取ることを許可してください。",Toast.LENGTH_LONG).show()
            regNotifyLog.launch(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))

        }
        view?.findViewById<Button>(R.id.init_grant_bubble_button)?.setOnClickListener{
            Toast.makeText(context,"全ての通知をバブルで表示するを選択してください。",Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_BUBBLE_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE,App.context.packageName)
            }
            regBubbleLog.launch(intent)

        }

        super.onStart()
    }
    override fun onResume() {
        super.onResume()
        val sets = NotificationManagerCompat.getEnabledListenerPackages(activity?:App.context)
        val resNotifyLog = (sets != null && sets.contains(activity?.packageName))
        resPermission(resNotifyLog,R.id.init_grant_notification_access_status)
        val manager = App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resBubble = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            manager.bubblePreference== NotificationManager.BUBBLE_PREFERENCE_ALL
        }else{
            manager.areBubblesAllowed()
        }
        resPermission(resBubble,R.id.init_grant_bubble_notification_status)
        var resFile = false
        try {
            App.context.contentResolver.query(App.dataUri,null,null,null,null)
            resFile = true
        }catch (e:SecurityException){
            resFile = false
        }
        resPermission(resFile,R.id.init_grant_notification_file_access_status)
        Log.d("START","Resume")

    }

}


