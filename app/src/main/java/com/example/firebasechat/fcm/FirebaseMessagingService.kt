package com.example.firebasechat.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.firebasechat.ui.activity.ChatActivity
import com.example.firebasechat.utils.setGroupNotification
import com.example.firebasechat.utils.setNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        const val channelId = "notification_channel"
        const val TAG = "tag"

        const val CHANNEL_NAME = "Test Notification"
        const val GROUP_NAME = "Test Group Notification"
        const val GROUP_ID = "test.notification"

        const val PATH_EXTRA = "path"
        const val DATA_EXTRA = "data"

        var token:String? = null
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        handleNewToken(token)
    }

    private fun handleNewToken(token: String) {
        Log.d("TAG", "handleNewToken: $token")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")

        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            remoteMessage.data.let {
                sendNotification(it["title"], it["body"])
            }
        }else{
            remoteMessage.data.let {
                sendNotification(it["title"], it["body"])
            }
        }*/

        remoteMessage.data.let {
            sendNotification(it["title"], it["body"],it["image"],it["bigPicture"])
            //createNotification(it["title"], it["body"], it["image"])
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
        }

    }

    //private fun sendNotificationFor13(title: String?, body: String?) {}

    @RequiresApi(Build.VERSION_CODES.S)
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        url: String?,
        bigPicture:String?
    ) {

        /* var  title: String? = null
         var messageBody: String? = null*/

        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        /*title = data["title"]
        messageBody = data["body"]*/


        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_MUTABLE
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
            url!!,
            bigPicture!!,
            defaultSoundUri,
            GROUP_ID,
            pendingIntent,

        )

        val groupNotification = setGroupNotification(
            channelId,
            GROUP_ID,
            true,
            "$title $messageBody",
            "New Notifications",
            "Notifications Grouped"
        )

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntent(intent)

        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_MUTABLE
        )
        notification.setContentIntent(resultPendingIntent)

        //ID of notification
        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
        notificationManager.notify(0, groupNotification)
    }
}
