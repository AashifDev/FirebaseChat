package com.example.firebasechat.session

import android.content.Context
import android.content.SharedPreferences
import com.example.firebasechat.model.User
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils
import com.google.gson.Gson

class PrefManager(var context: Context) {

    val pref: SharedPreferences? = context.getSharedPreferences("ChitChatSharedPreferences", Context.MODE_PRIVATE)
    val edit: SharedPreferences.Editor? = pref?.edit()

    companion object{

        fun saveUserWithEmail(email:String){
            Utils.putString(App.context(),"email",email)
        }

        fun saveUserWithNumber(number:String){
           Utils.putString(App.context(), "number",number)
        }

        fun getUserEmail():String?{
            return Utils.getString(App.context(),"email","")
        }

        fun getUserNumber(): String?{
            return Utils.getString(App.context(),"number","")
        }

        fun saveUserDetail(user:User?){
            Utils.putString(App.context(),"userData", Gson().toJson(user))
        }

        fun getUserDetail():User?{
            return Gson().fromJson(Utils.getString(App.context(),"userData", ""),User::class.java)
        }



    }

}