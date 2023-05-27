package com.example.firebasechat.splash.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.ui.authentication.AuthenticationActivity

class AgreeContinueFragment : Fragment() {
    lateinit var agreeAndContinue : TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agree_continue, container, false)
        agreeAndContinue = view.findViewById(R.id.textViewAgreeAndContinue)

        agreeAndContinue.setOnClickListener {
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finish()
        }
        return view
    }

}