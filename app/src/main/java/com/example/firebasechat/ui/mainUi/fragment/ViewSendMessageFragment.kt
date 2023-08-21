package com.example.firebasechat.ui.mainUi.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentViewSendMessageBinding
import com.example.firebasechat.model.Message
import com.example.firebasechat.mvvm.MessageViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.MessageAdapter
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


class ViewSendMessageFragment : Fragment() {
    lateinit var binding: FragmentViewSendMessageBinding
    lateinit var adapter: MessageAdapter
    lateinit var msgList: ArrayList<Message>

    var senderRoom:String? = null
    var receiverRoom:String? = null


    private val viewModel by viewModels<MessageViewModel>()

    var userName = ""
    var picUrl = ""
    var receiverUid = ""
    var message = ""
    var senderUid = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewSendMessageBinding.inflate(layoutInflater, container, false)

        userName = arguments?.getString("userName").toString()
        receiverUid = arguments?.getString("uid").toString()
        picUrl = arguments?.getString("pic").toString()

        setNameAndProfilePicOnToolBar()

        /*firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference*/


        return binding.root
    }


    private fun setNameAndProfilePicOnToolBar() {
        (requireActivity() as MainActivity).binding.toolbar.userName.text = userName
        val img = (requireActivity() as MainActivity).binding.toolbar.profileImage
        Glide.with(requireContext()).load(picUrl).into(img)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        msgList = ArrayList()
        adapter = MessageAdapter(requireContext(),msgList)

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.send.setOnClickListener {
            if (validMessage()){
                viewModel.sendMessage(message, senderUid)
                binding.editTextWriteMessage.text?.clear()
            }
        }

        setDataToRecyclerView()



    }



    private fun validMessage(): Boolean {
        message = binding.editTextWriteMessage.text.toString()
        if (message.isEmpty() && message==""){
            return false
        }
        return true
    }

    private fun setDataToRecyclerView() {


        viewModel.addMessageToFirebaseDb(senderUid,receiverUid)
        viewModel._messageLiveData.observe(viewLifecycleOwner, Observer { it ->
            when(it){
                is Response.Success->{
                    if (!it.data.isNullOrEmpty()){
                        msgList.addAll(it.data)
                        adapter = MessageAdapter(requireContext(),it.data)
                        binding.recyclerViewMessage.adapter = adapter
                        adapter.notifyItemInserted(msgList.size)
                        binding.recyclerViewMessage.scrollToPosition(adapter.msgList.size - 1)
                    }
                }
                is Response.Error->{

                }
                is Response.Loading->{

                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showToolbarItem()
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.GONE
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = false

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.VISIBLE
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.VISIBLE
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.VISIBLE
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }


}