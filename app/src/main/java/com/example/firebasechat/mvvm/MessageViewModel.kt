package com.example.firebasechat.mvvm

import MyFirebaseMessagingService
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.Message
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

    val _messageList = MutableLiveData<ArrayList<Message>>()
    val _messageLiveData: MutableLiveData<ArrayList<Message>>
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
                        _messageList.value = tempList

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("err", error.message)
                }
            })

        /*firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle the data that was added to the database.
                    val value = snapshot.getValue(Message::class.java)
                    // Trigger the notification here.
                    val senderId = value!!.senderId
                    if (!senderId!!.contains(firebaseAuth.currentUser!!.uid)){
                        MyFirebaseMessagingService().creteCustomNotificationBuilder(value.message)
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

            })*/
    }


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