package com.example.firebasechat.ui.authentication.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.example.firebasechat.databinding.FragmentAddProfileBinding
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
import java.io.ByteArrayOutputStream


class AddProfileFragment : Fragment() {
    lateinit var binding: FragmentAddProfileBinding
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference

    var uid = ""
    var verificationId = ""
    var profileImage:Uri? = null
    var userName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProfileBinding.inflate(layoutInflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        uid = arguments?.getString("uid").toString()
        verificationId = arguments?.getString("verificationId").toString()

        binding.progressBarSave.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textViewSave.setOnClickListener {
            if (validCredential()) {
                addUserToDbWithMobileNumber()
                binding.progressBarSave.visibility = View.VISIBLE
                binding.textViewSave.alpha = .5f
            }
        }

        binding.profile.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), Array(1) {
                android.Manifest.permission.CAMERA
            }, 101)
        } else {
            camera()
        }
    }

    private fun camera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent,101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101){
            val byte = ByteArrayOutputStream()
            val bitmapImage = data?.extras?.get("data") as Bitmap
            bitmapImage.compress(Bitmap.CompressFormat.JPEG,100,byte)
            val uriImg = MediaStore.Images.Media.insertImage(context?.contentResolver,bitmapImage,"image",null)
            profileImage = Uri.parse(uriImg)
            binding.profile.setImageURI(profileImage)
        }
    }

    private fun validCredential(): Boolean {
        userName = binding.etName.text.toString()
        if (TextUtils.isEmpty(userName) && userName == ""){
            binding.inputLayoutUserName.helperText = "Enter Name"
            return false
        }
        return true
    }

    private fun addUserToDbWithMobileNumber() {

        if (profileImage != null){
            val ref = firebaseStorage.reference.child("profileImagePhoneUser/"+firebaseAuth.currentUser?.phoneNumber)
            ref.putFile(profileImage!!)

            val user = User(userName,null,uid,verificationId)
            firebaseDb.child("user").child(uid).setValue(user)

            val number = firebaseAuth.currentUser?.phoneNumber
            if (number != null) {
                PrefManager.saveUserWithNumber(number)
            }

            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()

            Utils.createToast(ApplicationContext.context(),"Registration completed")
        }else{
            binding.progressBarSave.visibility = View.GONE
            binding.textViewSave.alpha = 0f
            Utils.createToast(ApplicationContext.context(),"Please upload profile image")
        }
    }
}