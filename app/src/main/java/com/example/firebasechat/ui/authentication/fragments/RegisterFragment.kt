package com.example.firebasechat.ui.authentication.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentRegisterBinding
import com.example.firebasechat.model.User
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.mainUi.MainActivity
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
    lateinit var prefManager: PrefManager
    lateinit var firebaseStorage: FirebaseStorage

    var email = ""
    var pass = ""
    var conPass = ""
    var name = ""
    var uid = ""
    val profileImage:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage
        prefManager = PrefManager(requireContext())

        binding.profile.setOnClickListener {
            addProfileImage()
        }
        return binding.root
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
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_OK){
            val data = data!!.data as Bitmap
            binding.profile.setImageBitmap(data)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener {
            if (validCredential()){
                setRegister()
                binding.progressCircular.visibility = View.VISIBLE
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
                    addUserToFirebaseDatabase(name, email)
                    prefManager.saveUser(email)
                }else{
                    Toast.makeText(context, "Registration Failed! try again", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                }
            }
    }

    private fun addUserToFirebaseDatabase(name: String, email: String) {
        uid = firebaseAuth.currentUser?.uid.toString()
        val user = User(name,email,uid)
        firebaseDb.child("user").child(uid).setValue(user)

    }

    private fun validCredential(): Boolean {
        name = binding.etName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        pass = binding.etPass.text.toString().trim()
        conPass = binding.etConPass.text.toString().trim()
        if (name.isEmpty() && name==""){
            binding.etName.error = "Name is blank"
            binding.etName.requestFocus()
            return false
        }else if (email.isEmpty() && email ==""){
            binding.etEmail.error = "Email address is blank"
            binding.etEmail.requestFocus()
            return false
        }else if (pass.isEmpty() && pass == "" ){
            binding.etPass.error = "Password is blank"
            binding.etPass.requestFocus()
            return false
        }else if (pass.length < 6){
            Toast.makeText(context, "Minimum 6 character required", Toast.LENGTH_SHORT).show()
            return false
        }else if (conPass != pass) {
            binding.etConPass.error = "Password does not matched"
            binding.etConPass.requestFocus()
            return false
        }
        return true
    }


}