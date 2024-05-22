package com.mehedi.loadapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class DownloadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createChannel(
            getString(R.string.notif_channel_id),
            getString(R.string.notif_channel_name)
        )
    }
    
    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.description = getString(R.string.notif_title)
        
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
    
}