package com.example.firebasechat.mvvm.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.firebasechat.mvvm.model.Message
import com.example.firebasechat.mvvm.model.Status
import com.example.firebasechat.mvvm.model.User

@Dao
interface Dao {

    @Query("SELECT * FROM user_table")
    fun getUserList():LiveData<ArrayList<User>>

    @Query("SELECT * FROM status_table")
    fun getStatus(): LiveData<ArrayList<Status>>

    @Query("SELECT * FROM message_table")
    fun getMessage(): LiveData<ArrayList<Message>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStatus(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun sendMessage(user: User)

}