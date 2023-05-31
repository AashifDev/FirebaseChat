package com.example.firebasechat.session

import android.content.Context
import android.content.SharedPreferences
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils

class PrefManager(var context: Context) {

    val pref: SharedPreferences? = context.getSharedPreferences("ChitChatSharedPreferences", Context.MODE_PRIVATE)
    val edit: SharedPreferences.Editor? = pref?.edit()

    companion object{

        fun saveUserWithEmail(email:String){
            Utils.putString(App.context(),"email",email)
        }

        fun saveUserWithNumber(number:String){
           Utils.putInt(App.context(), number,number.toInt())
        }

        fun getUserEmail():String?{
            return Utils.getString(App.context(),"email","")
        }

        fun getUserNumber(): Int {
            return Utils.getInt(App.context(),"number",0)
        }

    }

}