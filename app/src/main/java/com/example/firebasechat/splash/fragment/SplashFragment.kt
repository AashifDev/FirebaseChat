package com.example.firebasechat.splash.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.authentication.AuthenticationActivity
import com.example.firebasechat.ui.mainUi.MainActivity

class SplashFragment : Fragment() {
    lateinit var prefManager: PrefManager
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        prefManager = PrefManager(requireContext())
        val currentUser = prefManager.getUser()

        /*if (!currentUser.isNullOrEmpty()){
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }else{
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finish()
        }*/

        Handler(Looper.getMainLooper()).postDelayed(Runnable{
            findNavController().navigate(R.id.action_splashFragment_to_agreeContinueFragment)
        },2000)

        return view
    }

}