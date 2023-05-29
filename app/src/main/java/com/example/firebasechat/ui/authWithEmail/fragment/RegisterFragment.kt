package com.example.firebasechat.ui.authWithEmail.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentRegisterBinding
import com.example.firebasechat.model.User
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.utils.ApplicationContext
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage

    val REQUEST_CODE = 101
    var email = ""
    var pass = ""
    var conPass = ""
    var name = ""
    var uid = ""
    var mobileNumber = ""
    var profile: Uri? = null
    var profileImage:Boolean = false
    var registerWith = "Register with email"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            addProfileImage()
        }

        binding.textViewLoginWithEmail.setOnClickListener { findNavController().navigate(R.id.loginFragment) }

        binding.btnRegister.setOnClickListener {
            if (validCredential()){
                setRegister()
                binding.progressCircular.visibility = View.VISIBLE
                binding.btnRegister.alpha = .7f
            }
        }
    }

    private fun addProfileImage() {
        if (ActivityCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),Array(1){android.Manifest.permission.CAMERA},101)
        }else{
            camera()
        }
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE){
            try {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                profile =  Utils.getUriFromFile(requireContext(),bitmap)
                binding.profile.setImageURI(profile)
                profileImage = true
            }
            catch (e: Exception){
                e.printStackTrace()
            }

        }
    }

    private fun setRegister() {
        firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context, "Successfully Registered", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                    addUserToFirebaseDatabase(name, email,profile,mobileNumber)
                    //uploadProfilePicture(profile)
                    PrefManager.saveUserWithEmail(email)
                    binding.btnRegister.alpha = 1f
                }else{
                    Toast.makeText(context, "Registration Failed! try again", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                }
            }
    }

    private fun uploadProfilePicture(profile: Uri?) {
        if (profile != null){
            val ref = firebaseStorage.reference.child("profileImageEmailUser/"+firebaseAuth.currentUser?.email)
            ref.putFile(profile)
        }
    }

    private fun addUserToFirebaseDatabase(
        name: String,
        email: String,
        profile: Uri?,
        mobileNumber: String?
    ) {

        if (profile != null){
            uid = firebaseAuth.currentUser?.uid.toString()
            val ref = firebaseStorage.reference.child("profileImageEmailUser/"+firebaseAuth.currentUser?.email)
            ref.putFile(profile)
            val user = User(name,email, uid,mobileNumber,profile.toString())
            firebaseDb.child("user").child(this.uid).setValue(user)
        }


    }

    private fun validCredential(): Boolean {
        name = binding.etName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        pass = binding.etPass.text.toString().trim()
        conPass = binding.etConPass.text.toString().trim()
        if (TextUtils.isEmpty(name) && name==""){
            binding.llName.helperText = "Name is blank"
            return false
        }else if (TextUtils.isEmpty(email) && email ==""){
            binding.llEmail.helperText = "Email address is blank"
            return false
        }else if (TextUtils.isEmpty(pass) && pass == "" ){
            binding.llPass.helperText = "Password is blank"
            return false
        }else if (pass.length < 6){
            Utils.createToast(ApplicationContext.context(),"Minimum 6 character required")
            return false
        }else if (conPass != pass) {
            binding.llConPass.helperText = "Password does not matched"
            return false
        }else if (!profileImage){
            Utils.createToast(ApplicationContext.context(),"Please upload image")
            return false
        }
        return true
    }
}