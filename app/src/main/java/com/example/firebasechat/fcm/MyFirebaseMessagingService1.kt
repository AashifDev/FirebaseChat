package com.example.firebasechat.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.firebasechat.R
import com.example.firebasechat.ui.activity.ChatActivity
import com.example.firebasechat.ui.activity.MainActivity
import com.example.firebasechat.utils.setGroupNotification
import com.example.firebasechat.utils.setNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService1: FirebaseMessagingService() {

    /*fun generateMessage(message: String?){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.chat)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(message!!))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(channelId, CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(0,builder.build())
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(message: String): RemoteViews? {
        val remoteViews = RemoteViews("com.example.firebasechat", R.layout.firebase_push_notification)
        remoteViews.setTextViewText(R.id.message,message)
        remoteViews.setImageViewResource(R.id.image,R.drawable.chat,)

        return remoteViews
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null){
            generateMessage(message.notification!!.body!!)
        }


        // Check if message contains a notification payload.
        message.notification?.let {
            generateMessage(message.notification!!.body!!)
            Log.d("TAG", "Message Notification Body: ${it.body}")
        }

    }*/

    companion object {

        const val channelId = "notification_channel"
        const val TAG = "tag"

        const val CHANNEL_NAME = "Test Notification"
        const val GROUP_NAME = "Test Group Notification"
        const val GROUP_ID = "test.notification"

        const val PATH_EXTRA = "path"
        const val DATA_EXTRA = "data"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        handleNewToken(token)
    }

    private fun handleNewToken(token: String) {
        Log.d("TAG", "handleNewToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("TAG", "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body, remoteMessage.data)
        }
    }

    private fun sendNotification(
        title: String?,
        messageBody: String?,
        data: Map<String, String>?
    ) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        if (data != null) {
            for (i in 0 until data.size) {
                val key = data.keys.toList()[i]
                val value = data.values.toList()[i]
                intent.putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        //val channelId = getString(R.string.notification_channel_id)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(mChannel)
        }

       val notification = setNotification(
            channelId,
            title,
            messageBody,
            defaultSoundUri,
            GROUP_ID,
            pendingIntent
        )

        val groupNotification = setGroupNotification(
            channelId,
            GROUP_ID,
            true,
            "$title $messageBody",
            "New Notifications",
            "Notifications Grouped"
        )

        /*val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.chat)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setContentTitle(getString(R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH*/


        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)

        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_MUTABLE
        )
        notification.setContentIntent(resultPendingIntent)
        //notificationManager.notify(1234, notificationBuilder.build())

        //ID of notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
        notificationManager.notify(0, groupNotification)
    }
}