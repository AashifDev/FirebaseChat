package com.example.firebasechat.mvvm

import MyFirebaseMessagingService
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.R
import com.example.firebasechat.model.Message
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Response
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    var senderRoom: String? = null
    var receiverRoom: String? = null
    var CHANNEL_ID: String? = "channel1"
    var name: String? = "channel1"

    val _messageList = MutableLiveData<Response<ArrayList<Message>>>()
    val _messageLiveData: MutableLiveData<Response<ArrayList<Message>>>
        get() = _messageList

    fun addMessageToFirebaseDb(senderUid: String, receiverUid: String) = viewModelScope.launch {

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val tempList = arrayListOf<Message>()
                        tempList.clear()
                        for (postSnapshot in snapshot.children) {
                            val message = postSnapshot.getValue(Message::class.java)
                            tempList.add(message!!)
                        }
                        _messageList.value = Response.Success(tempList)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("err", error.message)
                }
            })

        firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle the data that was added to the database.
                    val value = snapshot.getValue(Message::class.java)
                    // Trigger the notification here.
                    val senderId = value!!.senderId
                    if (!senderId!!.contains(firebaseAuth.currentUser!!.uid)){
                        MyFirebaseMessagingService().createDefaultBuilder(value)
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

            })
    }
    /*// Function to send a notification.
    private fun sendNotification(message: Message?) {
        val notificationManager =App.context()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
    }*/

    fun sendMessage(message: String, senderUid: String) = viewModelScope.launch {
        val msg = Message(message, senderUid)
        firebaseDb.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(msg)
            .addOnSuccessListener {
                firebaseDb.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(msg)
            }
    }
}