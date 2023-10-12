package com.example.firebasechat.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityMainBinding
import com.example.firebasechat.fcm.CallNotification
import com.example.firebasechat.fcm.FirebaseMessagingService.Companion.DATA_EXTRA
import com.example.firebasechat.fcm.FirebaseMessagingService.Companion.PATH_EXTRA
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authWithMobile.AuthMobileActivity
import com.example.firebasechat.utils.CommonPermissionUtils.requestCommonPermission
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Utils
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var prefManager: PrefManager
    lateinit var navController: NavController
    private lateinit var vibrator: Vibrator

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //If you using setSupportActionBar then u have to manually inflate menu option
        setSupportActionBar(binding.toolbar.toolbar)
        //supportActionBar?.title = "ChitChat"

        CallNotification().createDefaultBuilder(null,null,null)

        setNavHostFragment()

        statusBarBackground()

        prefManager = PrefManager(this)

        binding.progressBar.visibility = View.GONE

        setClickOnDotMenu()

        setClickOnBottomMenu()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = ArrayList<String>()
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            requestCommonPermission(
                activity = this,
                permissionList = permissions,
                allPermissionApprovedListener = {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                },
                anyPermissionDenyListener = {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            )
        }

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleIntentData(intent?.extras)
    }

    private fun handleIntentData(extras: Bundle?) {
        if (extras != null) {
            val data = extras.getString(DATA_EXTRA)

            when(extras.getString(PATH_EXTRA)) {
                "home_fragment" -> {
                    navController.navigate(R.id.homeFragment, bundleOf(
                        DATA_EXTRA to data
                    ))
                }
            }
        }
    }

    private fun setClickOnDotMenu() {
        //Set on click on menu time
        binding.toolbar.toolbar.setOnMenuItemClickListener {
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
                    PrefManager.clear()
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
        //Set NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        //setupActionBarWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)
        //binding.toolbar.toolbar.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
            //Utils.createToast(this, current)
        }
        Log.d("tag", current.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        val uid = firebaseAuth.currentUser?.uid.toString()
        val lastSeen = Utils.dateTime(Calendar.getInstance())

        firebaseDb.child("user").child(uid).child("active").setValue(false)
        firebaseDb.child("user").child(uid).child("lastSeen").setValue(lastSeen)
    }
}