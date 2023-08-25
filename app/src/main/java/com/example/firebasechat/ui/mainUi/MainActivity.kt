package com.example.firebasechat.ui.mainUi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityMainBinding
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authWithMobile.AuthMobileActivity
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var prefManager: PrefManager
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navController: NavController
    private lateinit var vibrator: Vibrator

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "ChitChat"

        setNavHostFragment()

        statusBarBackground()

        prefManager = PrefManager(this)

        binding.progressBar.visibility = View.GONE

        setClickOnDotMenu()

        setClickOnBottomMenu()


    }

    private fun setClickOnBottomMenu() {
        //Bottom NavigationView
        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment->{
                    if (!it.isChecked){
                        navController.navigate(R.id.homeFragment)
                    }

                    vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(20)
                }
                R.id.viewStatusFragment->{
                    if (!it.isChecked){
                        navController.navigate(R.id.viewStatusFragment)
                    }
                    vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(20)
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun setClickOnDotMenu() {
        //Set on click on menu time
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.contact->{
                    navController.navigate(R.id.newMessageFragment)
                }
                R.id.profile->{
                    val currentId = firebaseAuth.currentUser?.uid
                    val bundle = Bundle().apply {
                        putString("currentId",currentId)
                    }
                    navController.navigate(R.id.profileFragment,bundle)
                }
                R.id.account->{
                    firebaseAuth.signOut()
                    val intent = Intent(this, AuthMobileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }

    private fun statusBarBackground() {
        //Status BAr Background
        window.apply { decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
    }

    private fun setNavHostFragment() {
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        //Set NavHostFragment
        navController = findNavController(R.id.mainNavHostFragment)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.viewStatusFragment))
        bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainNavHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.more_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun deleteAccount() {
        val current = firebaseAuth.currentUser!!
        if (current != null){
            current.delete()
            firebaseDb.child("user").child(firebaseAuth.currentUser?.uid!!).removeValue()
            Utils.clear(this)
            startActivity(Intent(this, AuthMobileActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val current = firebaseAuth.currentUser?.uid
        if (current.isNullOrEmpty()){
            val intent = Intent(this, AuthMobileActivity::class.java)
            startActivity(intent)
            finish()
            }else{
            //Utils.createToast(this, "Welcome Back!")
        }
        Log.d("tag", current.toString())
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onResume() {
        super.onResume()
    }


}