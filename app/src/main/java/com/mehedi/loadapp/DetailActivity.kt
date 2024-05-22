package com.mehedi.loadapp

import com.mehedi.loadapp.MainActivity.Companion.FILE_NAME
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mehedi.loadapp.MainActivity.Companion.STATUS
import com.mehedi.loadapp.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDetailBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
       
        
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
        
        val fileName = intent.getStringExtra(FILE_NAME)
        val status = intent.getStringExtra(STATUS)
        
        binding.fileNameValue.text = fileName
        binding.statusValue.text = status
        
        binding.detailButton.setOnClickListener {
            finish()
        }
    }
    
}
