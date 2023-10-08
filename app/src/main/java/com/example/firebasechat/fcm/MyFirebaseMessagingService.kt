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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.ui.activity.ChatActivity
import com.example.firebasechat.utils.App


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
            .setSmallIcon(com.example.firebasechat.R.drawable.chat)
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
        remoteViews.setImageViewBitmap(R.id.profileImage, image)

    } catch (e: Exception) {
        e.printStackTrace()
    }


    setCustomContentView(remoteViews)


    return this
}

private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = Color.RED
    val paint = Paint()
    val rect = Rect(0, 0,bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    bitmap.recycle()
    return output
}

fun getCircleBitmap1(bitmap: Bitmap): Bitmap? {
    val output: Bitmap
    val srcRect: Rect
    val dstRect: Rect
    val r: Float
    val width = bitmap.width
    val height = bitmap.height
    if (width > height) {
        output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
        val left = (width - height) / 2
        val right = left + height
        srcRect = Rect(left, 0, right, height)
        dstRect = Rect(0, 0, height, height)
        r = (height / 2).toFloat()
    } else {
        output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        val top = (height - width) / 2
        val bottom = top + width
        srcRect = Rect(0, top, width, bottom)
        dstRect = Rect(0, 0, width, width)
        r = (width / 2).toFloat()
    }
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawCircle(r, r, r, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
    bitmap.recycle()
    return output
}

private fun getCircleBitmap2(bitmap: Bitmap): Bitmap? {
    val output = Bitmap.createBitmap(
        bitmap.width,
        bitmap.height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)
    val color = Color.RED
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawOval(rectF, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    bitmap.recycle()
    return output
}

private fun getCircleBitmap3(bitmap: Bitmap): Bitmap

{

    var srcRect: Rect

    var dstRect: Rect

    var r: Float

    var paint = Paint();

    var width: Int = bitmap.getWidth()

    var height: Int = bitmap.getHeight()

    var widthToGenerate = 100F

    var heightToGenerate = 100F

    var borderWidth: Float = 1.toFloat()

    var output: Bitmap

    var canvas: Canvas

    if (width > height) {

        output = Bitmap.createBitmap(widthToGenerate.toInt(), heightToGenerate.toInt(), Bitmap.Config.ARGB_8888);

        canvas = Canvas(output);
        val scale: Float = (widthToGenerate / width)


        var xTranslation = 0.0f
        var yTranslation: Float = (heightToGenerate - height * scale) / 2.0f;


        var transformation = Matrix();
        transformation.postTranslate(xTranslation, yTranslation)
        transformation.preScale(scale, scale)

        var color: Int = Color.WHITE
        paint.setAntiAlias(true)
        paint.setColor(color)

        canvas.drawBitmap(bitmap, transformation, paint)

    } else {
        output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        canvas = Canvas(output);
        var top: Int = (height - width) / 2
        var bottom: Int = top + width
        srcRect = Rect(0, top, width, bottom)
        dstRect = Rect(0, 0, width, width);
        r = (width / 2).toFloat()


        var color: Int = Color.GRAY
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)


        canvas.drawCircle(r + borderWidth, r + borderWidth, r + borderWidth, paint)
        canvas.drawCircle(r, r, r, paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)

        bitmap.recycle()
    }

    return output

}