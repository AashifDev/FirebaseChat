package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentNewMessageBinding
import com.example.firebasechat.ui.mainUi.MainActivity


class NewMessageFragment : Fragment() {
    lateinit var binding: FragmentNewMessageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewMessageBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}