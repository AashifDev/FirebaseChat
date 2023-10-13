package com.example.firebasechat.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.ui.activity.ChatActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.setNotification


class MyFirebaseMessagingService {

    var channelId: String = "my_channel_id"
    var myChannel: String = "my_Channel"

    //Default fcm
    fun createDefaultBuilder(name:String?,message: String?, profileUrl:String?) {

        val notificationManager = App.context()!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create a unique notification channel for your app (required for Android 8.0 and higher).

        // Create an intent to launch your app when the user taps the notification.
        val intent = Intent(App.context(), ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            App.context(),
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                myChannel,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification.
        val notificationBuilder = NotificationCompat.Builder(App.context()!!, channelId)
            .setSmallIcon(R.drawable.chat)
            .setContentTitle("New Message")
            .setContentText(message)
            .setAutoCancel(true)
            //.setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomNotification(name,message,profileUrl)
        // Show the notification.
        val notificationId = 0 // You can use a unique ID for each notification.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}

@SuppressLint("RemoteViewLayout")
fun NotificationCompat.Builder.setCustomNotification(
    name:String?,
    message: String?,
    profileUrl:String?
): NotificationCompat.Builder {
    // inflate the layout and set the values to our UI IDs
    val remoteViews = RemoteViews("com.example.firebasechat", R.layout.notification_layout)

    try {
        val image: Bitmap = Glide.with(App.context()!!)
            .asBitmap()
            .load(profileUrl)
            .submit(30, 30)
            .get()

        remoteViews.setTextViewText(R.id.senderName, name)
        remoteViews.setTextViewText(R.id.senderMessage, message)
        remoteViews.setImageViewBitmap(R.id.profileImage, getCircleBitmapImg(image))



    } catch (e: Exception) {
        e.printStackTrace()
    }


    setCustomContentView(remoteViews)


    return this
}

private fun getCircleBitmapImg(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(
        bitmap.width,
        bitmap.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)

    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color

    canvas.drawCircle(
        bitmap.width / 2f, bitmap.height / 2f,
        bitmap.width / 2f, paint
    )
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    return output
}