package com.mehedi.loadapp.utils

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mehedi.loadapp.R
import com.mehedi.loadapp.ui.activity.MainActivity.Companion.NOTIFICATION_ID
import java.net.URL


fun Activity.showToast(message: String) {
    Toast.makeText(this@showToast, message, Toast.LENGTH_SHORT).show()
    
}

fun Float.dpToPx(view: View): Float {
    val density = view.resources.displayMetrics.density
    return this * density
}

fun String.isValidUrl(): Boolean {
    return try {
        URL(this)
        true
    } catch (e: Exception) {
        false
    }
}

fun NotificationManager.sendNotification(
    applicationContext: Context,
    statusPendingIntent: PendingIntent,
    msgBody: String,
    msgTitle: String,
) {
    val bigImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_launcher_foreground
    )
    
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigLargeIcon(bigImage)
        .bigPicture(bigImage)
    
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notif_channel_id)
    )
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(msgTitle)
        .setContentText(msgBody)
        .setAutoCancel(true)
        .setLargeIcon(bigImage)
        .setStyle(bigPicStyle)
        .setContentIntent(statusPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(
            R.drawable.ic_details,
            applicationContext.getString(R.string.notification_action),
            statusPendingIntent
        )
    
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelAllNotifications() {
    cancelAll()
}