package com.example.firebasechat.ui.mainUi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityChatBinding
import com.example.firebasechat.model.Message
import com.example.firebasechat.mvvm.MessageViewModel
import com.example.firebasechat.ui.mainUi.adapter.MessageAdapter
import com.example.firebasechat.utils.Response
import com.google.firebase.auth.FirebaseAuth

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var adapter: MessageAdapter
    lateinit var msgList: ArrayList<Message>

    var senderRoom:String? = null
    var receiverRoom:String? = null

    private val viewModel by viewModels<MessageViewModel>()

    var userName = ""
    var picUrl = ""
    var lastSeen = ""
    var isActive: Boolean = false
    var receiverUid = ""
    var message = ""
    var senderUid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statusBarBackground()

        getDataFromIntent()

        setNameAndProfilePicOnToolBar()

        initArrListAndAdapter()

        senderAndReceiverRoom()

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        sendMessage()

        setDataToRecyclerView()


    }

    private fun statusBarBackground() {
        //Status BAr Background
        window.apply { decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
    }

    private fun sendMessage() {
        binding.send.setOnClickListener {
            if (validMessage()){
                viewModel.sendMessage(message, senderUid)
                binding.editTextWriteMessage.text?.clear()
            }
        }
    }

    private fun senderAndReceiverRoom() {
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
    }

    private fun initArrListAndAdapter() {
        msgList = ArrayList()
        adapter = MessageAdapter(this,msgList)
    }

    private fun getDataFromIntent() {
        userName = intent.extras?.getString("userName").toString()
        receiverUid = intent.extras?.getString("uid").toString()
        picUrl = intent.extras?.getString("pic").toString()
        lastSeen = intent.extras?.getString("lastSeen").toString()
        isActive = intent.extras?.getBoolean("isActive") == true
    }

    private fun setNameAndProfilePicOnToolBar() {
        binding.toolbar.userName.text = userName
        val img = binding.toolbar.profileImage
        Glide.with(this).load(picUrl).into(img)
        if (isActive) binding.toolbar.isActive.text = "online" else binding.toolbar.isActive.text = "offline"

        binding.toolbar.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

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
        viewModel._messageLiveData.observe(this, Observer { it ->
            if (!it.isNullOrEmpty()){
                msgList.addAll(it)
                adapter = MessageAdapter(this,it)
                binding.recyclerViewMessage.adapter = adapter
                adapter.notifyItemInserted(msgList.size)
                binding.recyclerViewMessage.scrollToPosition(adapter.msgList.size - 1)
            }
        })
    }

}