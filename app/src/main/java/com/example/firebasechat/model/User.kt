package com.example.firebasechat.model

import com.google.firebase.database.IgnoreExtraProperties

class User {
    var name:String? = null
    var email:String? = null
    var uid:String? = null
    var mobileNumber:String? = null
    var pic:String? = null

    constructor(){}

    constructor(name:String?,email:String?,uid:String?,mobileNumber:String?,pic:String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.mobileNumber = mobileNumber
        this.pic = pic
    }
}

