package com.example.firebasechat.utils

import android.app.Application

class ApplicationContext:Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: ApplicationContext? = null

        fun context() : ApplicationContext? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext!!
    }
}