package com.example.firebasechat.Services

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.firebasechat.R

class FcmService : Service(){
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                ""
            }
        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(false)
            .setSmallIcon(R.drawable.chat)
            .setPriority(Notification.PRIORITY_MIN)
            .setContentTitle("Chitchat")
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(101, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val service = PendingIntent.getService(
            applicationContext, 1001,
            Intent(applicationContext, FcmService::class.java),
            PendingIntent.FLAG_IMMUTABLE)
        val alarmManager =
            getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000] = service
        super.onTaskRemoved(rootIntent)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}

