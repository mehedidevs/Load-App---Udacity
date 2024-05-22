package com.mehedi.loadapp.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mehedi.loadapp.R
import com.mehedi.loadapp.databinding.ActivityMainBinding
import com.mehedi.loadapp.utils.GLIDE_URL
import com.mehedi.loadapp.utils.LOAD_APP_URL
import com.mehedi.loadapp.utils.NO_URL
import com.mehedi.loadapp.utils.RETROFIT_URL
import com.mehedi.loadapp.utils.sendNotification
import com.mehedi.loadapp.utils.showToast


class MainActivity : AppCompatActivity() {
    
    private lateinit var fileName: String
    private lateinit var status: String
    private var downloadID: Long = 0
    private lateinit var pendingIntent: PendingIntent
    private lateinit var detailsIntent: Intent
    lateinit var binding: ActivityMainBinding
    private var downloadRadioValue = ""
    
    
    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
    }
    
    
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            downloadFromInternet()
        } else {
            showToast(getString(R.string.permission_is_needed))
        }
    }
    
    
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(
                receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
        
        
        
        binding.btnDownload.setOnClickListener {
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                
                downloadFromInternet()
            }
            
            
        }
    }
    
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            
            if (downloadID == id) {
                binding.btnDownload.isEnabled = true
                
                detailsIntent = Intent(applicationContext, DetailActivity::class.java)
                detailsIntent.putExtra(STATUS, status)
                detailsIntent.putExtra(FILE_NAME, fileName)
                
                pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    NOTIFICATION_ID,
                    detailsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                
                showToast(fileName)
                
                notificationManager.sendNotification(
                    applicationContext,
                    pendingIntent,
                    fileName,
                    applicationContext.getString(R.string.notif_title)
                )
            }
        }
    }
    
    private fun downloadFromInternet() {
        notificationManager.cancelAll()
        if (downloadRadioValue.isBlank()) {
            Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            return
        }
        binding.btnDownload.isEnabled = false
        val downloadRequest =
            DownloadManager.Request(Uri.parse(downloadRadioValue))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(downloadRequest)
    }
    
     fun selectUrlOptions(view: View) {
        if (view is RadioButton) {
            
            when (view.getId()) {
                R.id.glide_radio -> {
                    fileName = getString(R.string.glide_title)
                    downloadRadioValue = GLIDE_URL
                    status = getString(R.string.status_success)
                }
                
                R.id.load_app_radio -> {
                    downloadRadioValue = LOAD_APP_URL
                    fileName = getString(R.string.load_app_title)
                    status = getString(R.string.status_failed)
                }
                
                R.id.retrofit_radio -> {
                    downloadRadioValue = RETROFIT_URL
                    fileName = getString(R.string.retrofit_title)
                    status = getString(R.string.status_success)
                }
                
                else -> NO_URL
            }
        }
    }
    
    
    companion object {
        const val NOTIFICATION_ID = 101
        const val FILE_NAME = "file_name"
        const val STATUS = "status"
    }
}
