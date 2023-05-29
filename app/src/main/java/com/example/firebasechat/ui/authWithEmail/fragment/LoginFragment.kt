package com.example.firebasechat.ui.authWithEmail.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentLoginBinding
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.mainUi.MainActivity
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    private var email = ""
    var password = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.progressCircular.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.register.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
        binding.btnLogin.setOnClickListener {
            if (validCredential()){
                setLogin()
                binding.progressCircular.visibility = View.VISIBLE
                binding.btnLogin.alpha = .5f
            }
        }

    }

    private fun validCredential(): Boolean {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPass.text.toString().trim()

        if (email.isEmpty() && email==""){
            binding.etEmail.error = "Enter email"
            binding.etEmail.requestFocus()
            return false
        } else if(password.isEmpty() && password==""){
            binding.etPass.error = "Enter password"
            binding.etPass.requestFocus()
            return false
        }
        return true
    }

    private fun setLogin() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    PrefManager.saveUserWithEmail(email)
                    Toast.makeText(context, "Successfully Login", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.btnLogin.alpha = 1f
                    startActivity(Intent(context,MainActivity::class.java))
                    requireActivity().finish()
                }else{
                    Toast.makeText(context, "Login Failed! try again", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                }
            }

    }


}