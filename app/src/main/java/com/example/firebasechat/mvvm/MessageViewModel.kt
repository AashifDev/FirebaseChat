package com.example.firebasechat.mvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.Message
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
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

    }


    fun sendMessage(name:String,message: String, senderUid: String ,profile:String) = viewModelScope.launch {
        val msg = Message(name,message, senderUid,profile)

        firebaseDb.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(msg)
            .addOnSuccessListener {
                firebaseDb.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(msg)

                PrefManager.saveRoomId(receiverRoom!!)
            }

       // MyFirebaseMessagingService().createDefaultBuilder(msg.message)
    }
}