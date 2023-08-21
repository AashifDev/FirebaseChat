package com.example.firebasechat.model

data class UserStatus(
    val uid:String? = null,
    val name:String? = null,
    val pic:String? = null,
    val data: ArrayList<Status>
)
