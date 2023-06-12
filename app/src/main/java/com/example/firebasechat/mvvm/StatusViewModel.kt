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
    var userObj = User()

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

    fun addStatusToFirebaseDb(status: Uri) = viewModelScope.launch{
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
                            firebaseDb.child("user")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        try {
                                            val tempList = arrayListOf<User>()
                                            tempList.clear()
                                            for (postSnapShot in snapshot.children) {
                                                val user = postSnapShot.getValue(User::class.java)!!
                                                //current user will not show in list
                                                if (firebaseAuth.currentUser?.uid != user?.uid) {
                                                    tempList.add(user)
                                                }
                                            }

                                            for (postSnapShot in snapshot.children) {
                                                userObj = postSnapShot.getValue(User::class.java)!!
                                                //current user will not show in list
                                                if (firebaseAuth.currentUser?.uid == userObj?.uid) {

                                                        val path = it.toString()
                                                        val id = firebaseAuth.currentUser!!.uid
                                                        val usrName = userObj.name
                                                        val profileImage = userObj.pic
                                                        val dateTime = Utils.dateTime(Calendar.getInstance())
                                                        val status = Status(
                                                            id = id,
                                                            statusUrl = path,
                                                            userName = usrName,
                                                            userProfile = profileImage,
                                                            dateTime = dateTime
                                                        )
                                                        firebaseDb.child("status").child("uid").push().setValue(status)

                                                }
                                            }



                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("err", error.message)
                                    }
                                })

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

        /*uid = firebaseAuth.currentUser!!.uid
        val email = firebaseAuth.currentUser?.email.toString()
        val dtForm: DateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.")
        val date: String = dtForm.format(Calendar.getInstance().time)
        val fileName = date + FirebaseAuth.getInstance().currentUser!!.uid
        if (status != null){
            val ref = firebaseStorage.reference.child("status/").child(uid+email).child("status/$fileName")
            ref.putFile(status!!)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener {
                            path = it.toString()
                            val usrName = user!!.name
                            val profileImage = user!!.pic
                            val status = Status(path,usrName,profileImage)
                            firebaseDb.child("status").child("uid").push().setValue(status)
                        }
                        .addOnFailureListener {
                            Utils.createToast(App.context(), "Try again")
                        }
                }
        }*/
    }

}