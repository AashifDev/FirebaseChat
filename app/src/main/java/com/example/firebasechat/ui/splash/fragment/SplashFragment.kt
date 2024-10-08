package com.example.firebasechat.ui.splash.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.activity.MainActivity
import com.example.firebasechat.utils.App

class SplashFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserEmail = PrefManager.getUserEmail()
        val currentUserNumber = PrefManager.getUserNumber()

        if (!currentUserEmail.isNullOrEmpty()){
            Handler(Looper.getMainLooper()).postDelayed(Runnable{
                startActivity(Intent(App.context(), MainActivity::class.java))
                requireActivity().finish()
            },2000)
        }else if (!currentUserNumber.isNullOrEmpty()){
            Handler(Looper.getMainLooper()).postDelayed(Runnable{
                startActivity(Intent(App.context(), MainActivity::class.java))
                requireActivity().finish()
            },2000)
        }else{
            Handler(Looper.getMainLooper()).postDelayed(Runnable{
                findNavController().navigate(R.id.agreeContinueFragment)
            },2000)
        }

    }

}