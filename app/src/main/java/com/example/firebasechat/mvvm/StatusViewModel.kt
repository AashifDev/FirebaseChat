package com.example.firebasechat.mvvm

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.MyStatus
import com.example.firebasechat.model.Status
import com.example.firebasechat.model.User
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.FirebaseInstance
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.FirebaseInstance.firebaseStorage
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.Calendar

class StatusViewModel(application: Application):AndroidViewModel(application) {
    val status: Uri? = null

    val _myStatusList = MutableLiveData<Response<ArrayList<MyStatus>>>()
    val _myStatusLiveData: LiveData<Response<ArrayList<MyStatus>>>
        get() = _myStatusList

    val _statusList = MutableLiveData<Response<ArrayList<Status>>>()
    val _statusLiveData: MutableLiveData<Response<ArrayList<Status>>>
        get() = _statusList

    fun getStatusFromFirebaseDb() = viewModelScope.launch {
        firebaseDb.child("status").child("data").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val temList = arrayListOf<Status>()
                    temList.clear()
                    for (postSnapshot in snapshot.children) {
                        val statusObj = postSnapshot.getValue(Status::class.java)
                        if (firebaseAuth.currentUser!!.uid != statusObj!!.id) {
                            temList.add(statusObj)
                        }
                        _statusList.value = Response.Success(temList)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err", error.message)
            }
        })
    }

    fun addStatusToFirebaseDb(status: Uri, userlist : ArrayList<User> ) = viewModelScope.launch{
        val old= ArrayList<Status>()
        var new:Status? = null
        val email = firebaseAuth.currentUser?.email.toString()
        val dateTime = Utils.dateTime(Calendar.getInstance())

        val ref = firebaseStorage.child("status/").child(email).child(dateTime)
        ref.putFile(status)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        try {
                            val path = it.toString()
                            userlist.forEach {
                                new = Status(
                                    id = it.uid,
                                    statusUrl = path,
                                    userName = it.name,
                                    userProfile = it.pic,
                                    dateTime = dateTime
                                )
                            }
                            old.add(new!!)
                            _statusList.value = Response.Success(old)
                            firebaseDb.child("status").child("data").push().setValue(new)
                            Utils.createToast(App.context(), "Uploaded")
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }
                    .addOnFailureListener {
                        Utils.createToast(App.context(), "Try after some time!!")
                    }
            }
            .addOnFailureListener {
                Utils.createToast(App.context(), "Try after some time!!")
            }
    }

}