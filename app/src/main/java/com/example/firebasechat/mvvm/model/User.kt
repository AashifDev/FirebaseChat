package com.example.firebasechat.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
@Entity(tableName = "user_table")
data class User (
    var name:String? = null,
    var email:String? = null,
    @PrimaryKey
    var uid:String? = null,
    var mobileNumber:String? = null,
    var pic:String? = null
    )

