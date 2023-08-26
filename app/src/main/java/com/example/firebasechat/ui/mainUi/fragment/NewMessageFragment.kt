package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentNewMessageBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.model.User
import com.example.firebasechat.mvvm.UserViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.ui.mainUi.adapter.UserAdapter
import com.example.firebasechat.utils.hide


class NewMessageFragment : Fragment() {
    lateinit var binding: FragmentNewMessageBinding
    lateinit var userList: ArrayList<User>
    lateinit var adapter: UserAdapter
    private val userViewModel by viewModels<UserViewModel>()
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

        userList = ArrayList()

        binding.progressBar.visibility = View.VISIBLE
        //binding.noChat.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        setUserToRecyclerView()

    }

    private fun setUserToRecyclerView() {
        userViewModel.getUserFromFirebaseDb()
        userViewModel._userLiveData.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding.progressBar.visibility = View.GONE
                //binding.noChat.visibility = View.GONE
                userList.addAll(it)
                adapter = UserAdapter(requireContext(), it, this@NewMessageFragment)
                binding.contactList.adapter = adapter

            } else {
                binding.progressBar.visibility = View.GONE
                //binding.noChat.visibility = View.VISIBLE
            }
        })
    }

}