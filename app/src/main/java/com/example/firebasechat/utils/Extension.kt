package com.example.firebasechat.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
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
    url: String,
    bigPicture: String,
    soundUri: Uri?,
    groupId: String?,
    pendingIntent: PendingIntent,
): NotificationCompat.Builder {

    val notificationLayoutExpanded = notificationLayoutExpanded(
        App.context()!!,
        packageName,
        title,
        body,
        url,
        bigPicture,
        R.layout.firebase_custom_notification
    )

    val notification = NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.chat)
        //.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.chat))
        .setColor(ContextCompat.getColor(applicationContext, com.example.firebasechat.R.color.blue))
        .setContentTitle(title)
        .setContentText(body)/*
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(remoteViews)*/
        .setAutoCancel(true)
        .setSound(soundUri)
        .setGroupSummary(false)
        .setShortcutId(generateShortcutId(channelId))
        .setBubbleMetadata(
            setBubbleNotification(
                channelId,
                title,
                body,
                url,
                bigPicture,
                soundUri,
                groupId,
                pendingIntent
            )
        )
        /*.setBubbleMetadata(bubbleData)
        .setShortcutId(shortcutId)*/
        .addPerson(chatPartner)
        .addPerson(chatPartner)
        .setBigPictureStyle(title, body, url)
        //.setCustomNotification(title,body,url,App.context()!!)
        .setCustomBigContentView(notificationLayoutExpanded)
    //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(image))
    //.setCustomNotification(title,body,url)
    //.setBigPictureStyle(title,body,url)


    if (groupId != null)
        notification.setGroup(groupId)

    notification.setContentIntent(pendingIntent)

    return notification
}

fun setBubbleNotification(
    channelId: String,
    title: String?,
    body: String?,
    url: String,
    bigPicture: String,
    soundUri: Uri?,
    groupId: String?,
    pendingIntent: PendingIntent
): NotificationCompat.BubbleMetadata? {

    val icon: Bitmap = Glide.with(App.context()!!)
        .asBitmap()
        .load(url)
        .submit(55, 55)
        .get()

    val builder = NotificationCompat.BubbleMetadata.Builder(pendingIntent,
        IconCompat.createWithResource(App.context()!!, R.drawable.chat))

    return builder.setDesiredHeight(600)
        .setIntent(pendingIntent)
        .setAutoExpandBubble(true)
        .setSuppressNotification(true)
        //.setIcon(IconCompat.createWithAdaptiveBitmap(icon))
        .setAutoExpandBubble(true)
        .setSuppressNotification(true)
        .setIcon(IconCompat.createWithResource(App.context()!!,R.drawable.chat))
        .build()

}

val chatPartner = Person.Builder()
    .setName("Chat partner")
    .setImportant(true)
    .build()

val category = "com.example.firebasechat"

fun generateShortcutId(channelId: String): String? {
    val shortcut =
        ShortcutInfo.Builder(App.context()!!, channelId)
            .setCategories(setOf(category))
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name!!)
            .build()

    return channelId
}




fun notificationLayoutExpanded(
    context: Context,
    packageName: String?,
    title: String?,
    body: String?,
    url: String,
    bigPicture: String,
    firebasePushNotification: Int
): RemoteViews? {

    val remoteView = RemoteViews(packageName, firebasePushNotification)

    remoteView.setTextViewText(R.id.txtTitle, title)
    remoteView.setTextViewText(R.id.txtBody, body)

    /*----------Working fine-----------------*/
    /*val awt: AppWidgetTarget =
        object : AppWidgetTarget(context.applicationContext, R.id.image, remoteView, R.id.image) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                super.onResourceReady(resource, transition)
            }
        }

    val options = RequestOptions().override(300, 300).placeholder(R.drawable.chat)
        .error(R.drawable.camera_icon)

    Glide.with(context.applicationContext).asBitmap().load(url).apply(options).into(awt)*/

    /*----------Working fine-----------------*/
    try {
        val image1: Bitmap = Glide.with(context)
            .asBitmap()
            .load(url)
            .submit(55, 55)
            .get()


        val image2: Bitmap = Glide.with(context)
            .asBitmap()
            .load(bigPicture)
            .submit(250, 250)
            .get()

        remoteView.setImageViewBitmap(R.id.image, image2)
        remoteView.setImageViewBitmap(R.id.bigPicture, image1)

    } catch (e: Exception) {
        e.printStackTrace()
    }
    return remoteView
}


fun NotificationCompat.Builder.setCustomNotification(
    title: String?,
    text: String?,
    url: String?,
    context: Context,
): NotificationCompat.Builder {
    // inflate the layout and set the values to our UI IDs

    val remoteViews = RemoteViews("com.example.firebasechat", R.layout.firebase_push_notification)

    remoteViews.setTextViewText(R.id.title, title)
    remoteViews.setTextViewText(R.id.text, text)

    try {
        val image: Bitmap = Glide.with(context)
            .asBitmap()
            .load(url)
            .submit(250, 250)
            .get()

        remoteViews.setImageViewBitmap(R.id.image, image)

    } catch (e: Exception) {
        e.printStackTrace()
    }

    setCustomContentView(remoteViews)

    return this
}

private fun NotificationCompat.Builder.setBigPictureStyle(
    title: String?,
    text: String?,
    url: String
): NotificationCompat.Builder {

    val remoteViews = RemoteViews("com.example.firebasechat", R.layout.firebase_push_notification)

    remoteViews.setTextViewText(R.id.title, title)
    remoteViews.setTextViewText(R.id.text, text)

    try {
        val image: Bitmap = Glide.with(App.context()!!)
            .asBitmap()
            .load(url)
            .submit(250, 250)
            .get()

        remoteViews.setImageViewBitmap(R.id.image, image)

    } catch (e: Exception) {
        e.printStackTrace()
    }

    setCustomContentView(remoteViews)
    return this
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