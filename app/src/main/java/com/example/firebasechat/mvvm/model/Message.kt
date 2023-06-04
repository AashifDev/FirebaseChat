package com.example.firebasechat.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message (
    var message:String? = null,
    @PrimaryKey
    var senderId:String? = null
    )