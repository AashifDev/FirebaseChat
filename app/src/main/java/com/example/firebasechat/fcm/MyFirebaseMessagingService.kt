import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.firebasechat.R
import com.example.firebasechat.model.Message
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.utils.App


class MyFirebaseMessagingService {
    lateinit var builder: Notification.Builder
    var msg: String? = null
    var channelId: String = "my_channel_id"

    //Default fcm
    fun createDefaultBuilder(message: String?) {

        val notificationManager =
            App.context()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Create a unique notification channel for your app (required for Android 8.0 and higher).

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
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            App.context(),
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build the notification.
        val notificationBuilder = NotificationCompat.Builder(App.context()!!, channelId)
            .setSmallIcon(com.example.firebasechat.R.drawable.chat)
            .setContentTitle("New Message")
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        // Show the notification.
        val notificationId = 0 // You can use a unique ID for each notification.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}