/*
package com.example.firebasechat.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.sampleapplication.mainUi.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = ""

class MyFirebaseMessagingService: FirebaseMessagingService() {

    fun generateMessage(title: String,description: String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,description))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, description: String): RemoteViews? {
        val remoteViews = RemoteViews("com.example.sampleapplication", R.layout.firebase_push_notification)
        remoteViews.setTextViewText(R.id.text,title)
        remoteViews.setTextViewText(R.id.description,description)
        remoteViews.setImageViewResource(R.id.image,R.drawable.ic_logo,)

        return remoteViews
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null){
            generateMessage(message.notification!!.title!!,message.notification!!.body!!)
        }
    }



}*/
