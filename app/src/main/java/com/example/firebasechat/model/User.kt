package com.example.firebasechat.model

import com.google.firebase.database.IgnoreExtraProperties

class User {
    var name:String? = null
    var email:String? = null
    var uid:String? = null

    constructor(){}

    constructor(name:String?,email:String?,uid:String?){
        this.name = name
        this.email = email
        this.uid = uid
    }
}

/*
@IgnoreExtraProperties
data class User(
    var name:String? = null,
    var email:String? = null,
    var uid:String? = null
)*/
