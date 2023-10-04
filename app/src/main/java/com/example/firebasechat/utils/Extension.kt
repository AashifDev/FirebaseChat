package com.example.firebasechat.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.firebasechat.R

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
    soundUri: Uri?,
    groupId: String?,
    //remoteViews: RemoteViews?,
    //remoteViewsExpanded: RemoteViews?,
    pendingIntent: PendingIntent,
): NotificationCompat.Builder {

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


    if (groupId != null)
        notification.setGroup(groupId)

    notification.setContentIntent(pendingIntent)

    return notification
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