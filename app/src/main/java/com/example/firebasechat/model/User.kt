package com.example.firebasechat.model

import java.io.Serializable

data class User(
    var name: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var mobileNumber: String? = null,
    var pic: String? = null,
    var isActive: Boolean = false,
    var lastSeen: String? = null,
    var joinDate: String? = null,
    var isTyping:Boolean? = false
)
