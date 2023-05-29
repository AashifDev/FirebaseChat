package com.example.firebasechat.ui.mainUi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityMainBinding
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authWithMobile.AuthMobileActivity
import com.example.firebasechat.utils.ApplicationContext
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var progressBar: ProgressBar
    lateinit var prefManager: PrefManager
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDb = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()

        progressBar = findViewById(R.id.progressBar)
        prefManager = PrefManager(this)

        progressBar.visibility = View.GONE

       // binding.toolbar.userName.text = "ChitChat"




       /* binding.toolbar.back.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.logout->{
                    firebaseAuth.signOut()
                    Utils.clear(this)
                    progressBar.visibility = View.VISIBLE
                    startActivity(Intent(this, AuthMobileActivity::class.java))
                    finish()
                }
                R.id.deleteAccound->{
                    deleteAccount()
                }
                else ->{
                    progressBar.visibility = View.GONE
                }
            }
            true
        }*/

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
    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    fun hideToolbarItem(){
        binding.toolbar.back.visibility = View.GONE
        binding.toolbar.profileImage.visibility = View.GONE
        binding.toolbar.userName.text = "ChitChat"
    }

    fun showToolbarItem(){
        binding.toolbar.back.visibility = View.VISIBLE
        binding.toolbar.profileImage.visibility = View.VISIBLE
        binding.toolbar.userName.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        hideToolbarItem()
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