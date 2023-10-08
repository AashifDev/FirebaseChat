package com.example.firebasechat.model

data class Message (
    var name:String? = null,
    var message:String? = null,
    var senderId:String? = null,
    var senderProfileUrl:String? = null
    )
