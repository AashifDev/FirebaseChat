package com.example.firebasechat.ui.authentication.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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


class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var prefManager: PrefManager

    var email = ""
    var pass = ""
    var conPass = ""
    var name = ""
    var uid = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        prefManager = PrefManager(requireContext())
        return binding.root
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
        }else if (conPass != pass){
            binding.etConPass.error = "Password does not matched"
            binding.etConPass.requestFocus()
            return false
        }
        return true
    }


}