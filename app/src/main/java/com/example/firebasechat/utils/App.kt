package com.example.firebasechat.utils

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class App:Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun context() : App? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext!!
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}