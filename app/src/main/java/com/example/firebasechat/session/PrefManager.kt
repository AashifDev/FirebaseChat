package com.example.firebasechat.session

import android.content.Context
import android.content.SharedPreferences

class PrefManager( context: Context) {

    val pref: SharedPreferences? = context.getSharedPreferences("ChitChatSharedPreferences", Context.MODE_PRIVATE)
    val edit: SharedPreferences.Editor? = pref?.edit()

    fun saveUser(email:String){
        edit?.putString("email",email)
        edit?.commit()
    }

    fun getUser():String?{
        return pref?.getString("email","")
    }

    fun clear(){
        edit?.clear()
        edit?.commit()
    }
}