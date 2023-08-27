package com.example.firebasechat.mvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.firebasechat.model.User
import com.example.firebasechat.utils.FirebaseInstance
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.Response
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    val _userList = MutableLiveData<ArrayList<User>>()
    val _userLiveData: MutableLiveData<ArrayList<User>>
        get() = _userList

    fun getUserFromFirebaseDb() = viewModelScope.launch{
        FirebaseInstance.firebaseDb.child("user")
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
                            _userList.value = tempList
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


}