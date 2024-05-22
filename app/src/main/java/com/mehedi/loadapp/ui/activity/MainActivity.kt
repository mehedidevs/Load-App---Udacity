package com.mehedi.loadapp.ui.activity

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
    private var downloadUrl = ""
    
    
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
            download()
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
                
                download()
            }
            
            
        }
    }
    
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            
            val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val downloadManager =
                context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadId!!)
            val cursor = downloadManager.query(query)
            
            if (cursor != null && cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                
                if (statusIndex >= 0) {
                    val downloadStatus = cursor.getInt(statusIndex)
                    when (downloadStatus) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            if (uriIndex >= 0) {
                                val uriString = cursor.getString(uriIndex)
                                // Do something with the file URI
                                status = getString(R.string.status_success)
                            }
                        }
                        
                        DownloadManager.STATUS_FAILED -> {
                            if (reasonIndex >= 0) {
                                val reason = cursor.getInt(reasonIndex)
                                // Handle the failure reason
                                status = getString(R.string.status_failed)
                            }
                        }
                        
                        DownloadManager.STATUS_RUNNING -> {
                            // Download is in progress
                        }
                        
                        DownloadManager.STATUS_PAUSED -> {
                            // Download is paused
                        }
                        
                        DownloadManager.STATUS_PENDING -> {
                            // Download is pending
                        }
                    }
                }
            }
            cursor?.close()
            
            
            if (downloadID == id) {
                binding.btnDownload.isEnabled = true
                
                detailsIntent = Intent(applicationContext, DetailActivity::class.java)
                detailsIntent.putExtra(DOWNLOAD_STATUS, status)
                detailsIntent.putExtra(DOWNLOAD_FILE_NAME, fileName)
                detailsIntent.putExtra(DOWNLOAD_FILE_NAME, fileName)
                
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
    
    
    private fun download() {
        notificationManager.cancelAll()
        
        if (downloadUrl.isBlank()) {
            Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnDownload.isEnabled = false
        val downloadRequest =
            DownloadManager.Request(Uri.parse(downloadUrl))
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
                    downloadUrl = GLIDE_URL
                }
                
                R.id.load_app_radio -> {
                    downloadUrl = LOAD_APP_URL
                    fileName = getString(R.string.load_app_title)
                }
                
                R.id.retrofit_radio -> {
                    downloadUrl = RETROFIT_URL
                    fileName = getString(R.string.retrofit_title)
                }
                
                else -> NO_URL
            }
        }
    }
    
    
    companion object {
        const val NOTIFICATION_ID = 101
        const val DOWNLOAD_FILE_NAME = "file_name"
        const val DOWNLOAD_FILE_URI = "file_uri"
        const val DOWNLOAD_STATUS = "status"
    }
}
