package com.example.firebasechat.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.firebasechat.R
import java.io.IOException
import java.net.URL

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun Context.setNotification(
    channelId: String,
    title: String?,
    body: String?,
    url:String,
    soundUri: Uri?,
    groupId: String?,
    //remoteViews: RemoteViews?,
    //remoteViewsExpanded: RemoteViews?,
    pendingIntent: PendingIntent,
): NotificationCompat.Builder {

    val notificationLayoutExpanded = notificationLayoutExpanded(
        packageName,
        title,
        body,
        url,
        R.layout.firebase_push_notification
    )

    var image: Bitmap? = null

    try {
        val urll = URL(url)
        image = BitmapFactory.decodeStream(urll.openConnection().getInputStream())
    } catch (e: IOException) {
        println(e)
    }

    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.chat)
        //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.chat))
        .setColor(ContextCompat.getColor(applicationContext, R.color.blue))
        .setContentTitle(title)
        .setContentText(body)/*
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(remoteViews)*/
        .setAutoCancel(true)
        .setSound(soundUri)
        .setGroupSummary(false)
        .setCustomBigContentView(notificationLayoutExpanded)
        //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
        //.setCustomNotification(title,body,url)
        //.setBigPictureStyle(title,body,url)


    if (groupId != null)
        notification.setGroup(groupId)

    notification.setContentIntent(pendingIntent)

    return notification
}

fun notificationLayoutExpanded(
    packageName: String?,
    title: String?,
    body: String?,
    url: String,
    firebasePushNotification: Int
): RemoteViews? {

    val remoteView = RemoteViews(packageName,firebasePushNotification)

    var image: Bitmap? = null

    try {
        val urll = URL(url)
        image = BitmapFactory.decodeStream(urll.openConnection().getInputStream())
    } catch (e: IOException) {
        println(e)
    }



    remoteView.setTextViewText(R.id.title, title)
    remoteView.setTextViewText(R.id.text, body)

    Glide.with(App.context()!!)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                remoteView.setImageViewBitmap(R.id.imageViewLogo, resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {}
        })


    return remoteView
}


fun NotificationCompat.Builder.setCustomNotification(
    title: String?,
    text: String?,
    url: String?
): NotificationCompat.Builder {
    // inflate the layout and set the values to our UI IDs
    var image: Bitmap? = null

    try {
        val urll = URL(url)
        image = BitmapFactory.decodeStream(urll.openConnection().getInputStream())
    } catch (e: IOException) {
        println(e)
    }

    val remoteViews = RemoteViews("com.example.firebasechat", R.layout.firebase_push_notification)

    remoteViews.setTextViewText(R.id.title, title)
    remoteViews.setTextViewText(R.id.text, text)

    remoteViews.setImageViewBitmap(R.id.image, image)

    setCustomContentView(remoteViews)

    return this
}

private fun NotificationCompat.Builder.setBigPictureStyle(
    title: String?,
    text: String?,
    url: String
): NotificationCompat.Builder {

    var image: android.graphics.Bitmap? = null

    try {
        val url = URL(url)
        image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
    } catch (e: IOException) {
        println(e)
    }

    setLargeIcon(image)
    setStyle(
        NotificationCompat.BigPictureStyle()
            .bigPicture(image)
            .setBigContentTitle(title)
            .setSummaryText(text)
    )
    return this
}


fun Context.setNotificationWithPayLoad(
    channelId: String,
    title: String?,
    body: String?,
    soundUri: Uri?,
    groupId: String?,
    pendingIntent: PendingIntent,
): NotificationCompat.Builder {
    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.chat)
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.chat))
        .setColor(ContextCompat.getColor(applicationContext, R.color.blue))
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setSound(soundUri)
        .setGroupSummary(false)

    if (groupId != null)
        notification.setGroup(groupId)

    notification.setContentIntent(pendingIntent)

    return notification
}

fun Context.setGroupNotification(
    channelId: String,
    groupId: String,
    groupSummary: Boolean,
    lineText: String,
    bigContentTitle: String,
    summaryText: String,
): Notification = NotificationCompat.Builder(this, channelId)
    .setSmallIcon(R.drawable.chat)
    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.chat))
    .setColor(ContextCompat.getColor(applicationContext, R.color.blue))
    .setStyle(
        NotificationCompat.InboxStyle()
            .addLine(lineText)
            .setBigContentTitle(bigContentTitle)
            .setSummaryText(summaryText)
    )
    .setGroup(groupId)
    .setGroupSummary(groupSummary)
    .setAutoCancel(true)
    .build()


