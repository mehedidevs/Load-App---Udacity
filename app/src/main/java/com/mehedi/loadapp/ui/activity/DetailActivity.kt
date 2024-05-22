package com.mehedi.loadapp.ui.activity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mehedi.loadapp.databinding.ActivityDetailBinding
import com.mehedi.loadapp.ui.activity.MainActivity.Companion.DOWNLOAD_FILE_NAME
import com.mehedi.loadapp.ui.activity.MainActivity.Companion.DOWNLOAD_FILE_URI
import com.mehedi.loadapp.ui.activity.MainActivity.Companion.DOWNLOAD_STATUS
import com.mehedi.loadapp.utils.cancelAllNotifications


class DetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDetailBinding
    
    private val notificationManager: NotificationManager by lazy {
        ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        
        notificationManager.cancelAllNotifications()
        
        
        binding.apply {
            txtFileNameValue.text = intent.getStringExtra(DOWNLOAD_FILE_NAME).toString()
            txtStatusValue.text = intent.getStringExtra(DOWNLOAD_STATUS).toString()
            txtUriValue.text = intent.getStringExtra(DOWNLOAD_FILE_URI).toString()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
}
