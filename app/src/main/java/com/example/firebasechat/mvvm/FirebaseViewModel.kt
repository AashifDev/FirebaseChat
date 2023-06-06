package com.example.firebasechat.mvvm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasechat.model.MyStatus
import com.example.firebasechat.model.Status
import com.example.firebasechat.model.User
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.FirebaseInstance.firebaseStorage
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class FirebaseViewModel : ViewModel() {
    val status: Uri? = null

    val statusList = MutableLiveData<Response<ArrayList<Status>>>()
    val statusLiveData: MutableLiveData<Response<ArrayList<Status>>>
        get() = statusList

    val myStatusList = MutableLiveData<Response<ArrayList<MyStatus>>>()
    val myStatusLiveData: LiveData<Response<ArrayList<MyStatus>>>
        get() = myStatusList

    val userList = MutableLiveData<Response<ArrayList<User>>>()
    val userLiveData: MutableLiveData<Response<ArrayList<User>>>
        get() = userList


    fun getUserFromFirebaseDb() {
        firebaseDb.child("user")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val tempList = arrayListOf<User>()
                        tempList.clear()
                        for (postSnapShot in snapshot.children) {
                            val userObj = postSnapShot.getValue(User::class.java)
                            //current user will not show in list
                            if (firebaseAuth.currentUser?.uid != userObj?.uid) {
                                tempList.add(userObj!!)
                            }
                            userList.value = Response.Success(tempList)
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


    fun getStatusFromFirebaseDb() {
        firebaseDb.child("status").child("uid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val temList = arrayListOf<Status>()
                    temList.clear()
                    for (postSnapshot in snapshot.children) {
                        val statusObj = postSnapshot.getValue(Status::class.java)
                        if (firebaseAuth.currentUser!!.uid != statusObj!!.id) {
                            temList.add(statusObj)
                        }
                        statusList.value = Response.Success(temList)
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

    fun addStatusToFirebaseDb(status: Uri, statusList: ArrayList<Status>) {
        val email = firebaseAuth.currentUser?.email.toString()
        val dateTime = Utils.dateTime(Calendar.getInstance())
        val id = firebaseAuth.currentUser!!.uid
        val userName = firebaseAuth.currentUser!!.displayName.toString()

        val ref = firebaseStorage.child("status/").child(email).child(dateTime)
        ref.putFile(status)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener {
                        val path = it.toString()
                        val temList = arrayListOf<Status>()
                        Response.Success(userList).data!!.value!!.data!!.forEach {
                            temList.add(
                                Status(
                                    id = it.uid,
                                    statusUrl = path,
                                    userName = it.name,
                                    userProfile = it.pic,
                                    dateTime = dateTime
                                )
                            )
                        }
                      /*  userList.value!!.forEach {
                            temList.add(
                                Status(
                                    id = it.uid,
                                    statusUrl = path,
                                    userName = it.name,
                                    userProfile = it.pic,
                                    dateTime = dateTime
                                )
                            )
                        }*/

                        statusList.addAll(temList)
                        firebaseDb.child("status").child("uid").push().setValue(statusList)
                        Utils.createToast(App.context(),"Uploaded")
                    }
                    .addOnFailureListener {
                        Utils.createToast(App.context(),"Failed")
                    }
            }
            .addOnFailureListener {
                Utils.createToast(App.context(),"Failed")
            }
    }
}