package com.example.firebasechat.mvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.Message
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class MessageViewModel(application: Application): AndroidViewModel(application) {
    var senderRoom:String? = null
    var receiverRoom:String? = null

    val _messageList = MutableLiveData<Response<ArrayList<Message>>>()
    val _messageLiveData : MutableLiveData<Response<ArrayList<Message>>>
        get() = _messageList

    fun addMessageToFirebaseDb(senderUid: String,receiverUid:String) = viewModelScope.launch{

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val tempList = arrayListOf<Message>()
                        tempList.clear()
                        for (postSnapshot in snapshot.children){
                            val message = postSnapshot.getValue(Message::class.java)
                            tempList.add(message!!)
                        }
                        _messageList.value = Response.Success(tempList)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("err",error.message)
                }
            })
    }

    fun sendMessage(message:String,senderUid:String) = viewModelScope.launch{
        val msg = Message(message,senderUid)
        firebaseDb.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(msg)
            .addOnSuccessListener {
                firebaseDb.child("chats").child(receiverRoom!!).child("messages").push().setValue(msg)
            }
    }
}