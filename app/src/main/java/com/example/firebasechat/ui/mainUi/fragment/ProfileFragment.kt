package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentProfileBinding
import com.example.firebasechat.model.User
import com.example.firebasechat.ui.mainUi.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var dataList: ArrayList<User>
    var currentId=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        currentId = arguments?.getString("currentId").toString()

        dataList = ArrayList()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = false
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.newMsg).isVisible = false
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.profile).isVisible = false
        (requireActivity() as MainActivity).hideToolbarItem()

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideToolbarItem()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.newMsg).isVisible = true
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.profile).isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.newMsg).isVisible = true
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.profile).isVisible = true
    }

}