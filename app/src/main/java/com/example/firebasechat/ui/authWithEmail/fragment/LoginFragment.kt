package com.example.firebasechat.ui.authWithEmail.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentLoginBinding
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authWithMobile.AuthMobileActivity
import com.example.firebasechat.ui.activity.MainActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
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

        binding.back.setOnClickListener {
            startActivity(Intent(App.context(),AuthMobileActivity::class.java))
            requireActivity().finish()
        }

        binding.textViewLoginWithMobile.setOnClickListener {
            startActivity(Intent(App.context(),AuthMobileActivity::class.java))
            requireActivity().finish()
        }

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
            binding.llEmail.helperText = "Email Required"
            return false
        } else if(password.isEmpty() && password==""){
            binding.llPass.helperText = "Password Required"
            return false
        }
        return true
    }

    private fun setLogin() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    PrefManager.saveUserWithEmail(email)
                    Utils.createToast(App.context(),"Successfully Login")
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.btnLogin.alpha = 1f
                    startActivity(Intent(context, MainActivity::class.java))
                    requireActivity().finish()
                }else{
                    binding.progressCircular.visibility = View.GONE
                    binding.btnLogin.alpha = 1f
                }
            }
            .addOnFailureListener {
                Utils.createToast(App.context(), it.message)
            }

    }


}