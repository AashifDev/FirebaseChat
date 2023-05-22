package com.example.firebasechat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.databinding.FragmentViewSendMessageBinding
import com.example.firebasechat.ui.mainUi.MainActivity

class ViewSendMessageFragment : Fragment() {
    lateinit var binding: FragmentViewSendMessageBinding
    var userName = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewSendMessageBinding.inflate(layoutInflater, container, false)
        userName = arguments?.getString("userName").toString()
        (requireActivity() as MainActivity).toolbar.title = userName
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }


}