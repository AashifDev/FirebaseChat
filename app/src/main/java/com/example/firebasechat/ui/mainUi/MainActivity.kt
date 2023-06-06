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
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityMainBinding
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authWithMobile.AuthMobileActivity
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Utils


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var progressBar: ProgressBar
    lateinit var prefManager: PrefManager
    private lateinit var vibrator: Vibrator

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "ChitChat"
        //Set NavHostFragment
        navController = findNavController(R.id.mainNavHostFragment)

        //Status BAr Background
        window.apply { decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)


        progressBar = findViewById(R.id.progressBar)
        prefManager = PrefManager(this)

        progressBar.visibility = View.GONE


        //Set on click on menu time
        binding.toolbar.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.newMsg->{
                    navController.navigate(R.id.newMessageFragment)
                }
                R.id.profile->{
                    val currentId = firebaseAuth.currentUser?.uid
                    val bundle = Bundle().apply {
                        putString("currentId",currentId)
                    }
                    navController.navigate(R.id.profileFragment,bundle)
                }
                R.id.signout->{
                    firebaseAuth.signOut()
                    val intent = Intent(this, AuthMobileActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        //Bottom NavigationView
       binding.bottomNavigation.setOnItemSelectedListener {
           when(it.itemId){
               R.id.home->{
                   if (!it.isChecked){
                       navController.navigate(R.id.homeFragment)
                   }

                   vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                   vibrator.vibrate(20)
               }
               R.id.status->{
                   if (!it.isChecked){
                       navController.navigate(R.id.viewStatusFragment)
                   }

                   vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                   vibrator.vibrate(20)
               }
           }
           return@setOnItemSelectedListener true
       }

        binding.bottomNavigation.setOnItemReselectedListener {
            navController.popBackStack(it.itemId, inclusive = false)
        }

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


    fun showToolbarItem(){
        binding.toolbar.profileImage.visibility = View.VISIBLE
        binding.toolbar.userName.visibility = View.VISIBLE
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.userName.textSize = 15f
    }

    fun hideToolbarItem(){
        binding.toolbar.profileImage.visibility = View.GONE
        binding.toolbar.back.visibility = View.GONE
        binding.toolbar.userName.setText(R.string.app_name)
        binding.toolbar.userName.textSize = 20f
    }

    override fun onStart() {
        super.onStart()
        hideToolbarItem()
        binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true

        val current = firebaseAuth.currentUser?.uid
        if (current.isNullOrEmpty()){
            val intent = Intent(this, AuthMobileActivity::class.java)
            startActivity(intent)
            finish()
            }else{
            //Utils.createToast(this, "Welcome Back!")
        }
        /*val current = firebaseAuth.currentUser!!.getIdToken(true)

        current.addOnSuccessListener {
            val intent = Intent(this, AuthMobileActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Log.e("err",it.message.toString())
        }*/
        Log.d("tag", current.toString())
    }

    override fun onRestart() {
        super.onRestart()
        hideToolbarItem()
    }

    override fun onResume() {
        super.onResume()
        hideToolbarItem()
    }


}