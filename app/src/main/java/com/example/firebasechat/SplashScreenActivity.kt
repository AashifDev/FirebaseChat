package com.example.firebasechat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authentication.AuthenticationActivity
import com.example.firebasechat.ui.mainUi.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        prefManager = PrefManager(this)

        val currentUser = prefManager.getUser()
        if (!currentUser.isNullOrEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }

    }
}