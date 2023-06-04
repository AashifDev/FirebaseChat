package com.example.firebasechat.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_table")
data class Status(
    @PrimaryKey
    val id:String? = null,
    val statusUrl:String? = null,
    val userName:String? = null,
    val userProfile:String? = null,
    val dateTime:String? = null
)
