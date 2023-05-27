package com.example.firebasechat.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebasechat.R

class AuthenticationActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        toolbar = findViewById(R.id.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.authenticationNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        toolbar.setupWithNavController(navController)

        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.email){
                navController.navigate(R.id.registerFragment)
            }
            true
        }

    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}