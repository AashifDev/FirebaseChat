import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.firebasechat.R
import com.example.firebasechat.model.Message
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.utils.App
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseMessagingService: FirebaseMessagingService() {
    //Default fcm
    fun createDefaultBuilder(message: Message?) {
        val notificationManager =
            App.context()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create a unique notification channel for your app (required for Android 8.0 and higher).
        val channelId = "my_channel_id"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        // Create an intent to launch your app when the user taps the notification.
        val intent = Intent(App.context(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            App.context(),
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification.
        val notificationBuilder = NotificationCompat.Builder(App.context()!!, channelId)
            .setSmallIcon(R.drawable.chat)
            .setContentTitle("New Message")
            .setContentText(message.toString())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        // Show the notification.
        val notificationId = 0 // You can use a unique ID for each notification.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /*@SuppressLint("RemoteViewLayout")
    fun createCustomNotification() {

        val notificationID: Int = 100
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.data = Uri.parse("https://www.tutorialsbuzz.com/")

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        val channelId = "News"
        val channelName = "News"

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.chat)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(500, 500, 500))

        val smallContent = RemoteViews(App.context()!!.packageName, R.layout.fcm_lalyout)
        //val bigContent = RemoteViews(getPackageName(), R.layout.large_notification_layout)

        //bigContent.setTextViewText(R.id.notification_title, "Notification Custom Text")
        smallContent.setTextViewText(R.id.name, "Notification Custom Text")

        //bigContent.setImageViewResource(R.id.notification_image, R.mipmap.ic_launcher)
        smallContent.setImageViewResource(R.id.profile, R.mipmap.chat)

        builder.setContent(smallContent)
        //builder.setCustomBigContentView(bigContent)

        with(NotificationManagerCompat.from(App.context()!!)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                createNotificationChannel(channel)
                if (ActivityCompat.checkSelfPermission(App.context()!!, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) {
                    showSettingDialog()
                    return
                }
                notify(notificationID, builder.build())
            }
        }
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }*/

}