package com.example.firebasechat.ui.mainUi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.firebasechat.R
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var toolbar: Toolbar
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var prefManager: PrefManager
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progressBar)
        prefManager = PrefManager(this)

        progressBar.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        toolbar.setupWithNavController(navController)


        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.logout){
                firebaseAuth.signOut()
                prefManager.clear()
                progressBar.visibility = View.VISIBLE
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }else{
                progressBar.visibility = View.GONE
            }
            true
        }
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }


}