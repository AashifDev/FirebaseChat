package com.example.firebasechat.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityChatBinding
import com.example.firebasechat.model.Message
import com.example.firebasechat.model.User
import com.example.firebasechat.mvvm.MessageViewModel
import com.example.firebasechat.ui.mainUi.adapter.MessageAdapter
import com.example.firebasechat.utils.Constant.GALLERY_REQ_CODE
import com.example.firebasechat.utils.FirebaseInstance
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class ChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    lateinit var adapter: MessageAdapter
    lateinit var msgList: ArrayList<Message>

    var senderRoom: String? = null
    var receiverRoom: String? = null

    private val viewModel by viewModels<MessageViewModel>()
    var PERMISSION_REQ_ID_RECORD_AUDIO = 101
    private val uid = "0"
    var navController: NavController? = null
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

        setClickOnDotMenu()

        changeImageToMicAndSendIcon()

        setChatBackground()

        setClickOnToolbarItem()
    }

    private fun setClickOnToolbarItem() {
        val agoraConfig = User.Agora(
            null,
            "Testing",
            "0"
            )
        binding.toolbar.audioCall.setOnClickListener {
           /* FirebaseInstance.firebaseDb
                .child("user")
                .child(receiverUid)
                .child("agora")
                .setValue(agoraConfig)*/
            val intent = Intent(this@ChatActivity, CallActivity::class.java).apply {
                putExtra("pic",picUrl)
                putExtra("name",userName)
            }
            startActivity(intent)
        }

        binding.toolbar.videoCall.setOnClickListener {
            val intent = Intent(this@ChatActivity, VideoCallActivity::class.java).apply {
                putExtra("pic",picUrl)
                putExtra("name",userName)
            }
            startActivity(intent)
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Location")
        dialog.setMessage("Are you sure want to add this location?")
        dialog.setPositiveButton("Yes"){_,_->
            finish()
        }
        dialog.setNegativeButton("No"){_,_->
            this.finish()
        }
        dialog.create()
        dialog.show()
    }

    private fun setChatBackground() {
        FirebaseInstance.firebaseDb.child("user").child(receiverUid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    val img = data!!.chatBackground
                    Glide.with(this@ChatActivity).load(img).into(binding.chatBackground)
                    println("ChatActivity.onDataChange::${img.toString()}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("error",error.message)
                }

            })
    }

    private fun changeImageToMicAndSendIcon() {
        binding.editTextWriteMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                val size = p0!!.length
                if (size > 0) {
                    binding.send.setImageResource(R.drawable.send1)
                } else {
                    binding.send.setImageResource(R.drawable.ic_mic)
                }
            }
        })
    }

    private fun showTypingPlaceHolder() {
        binding.editTextWriteMessage.setOnFocusChangeListener { view, b ->
            if (view.hasFocus()) {
                FirebaseInstance.firebaseDb
                    .child("user")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("typing").setValue(true)
            }
        }

        FirebaseInstance.firebaseDb.child("user").child(receiverUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    val isTyping = data?.isTyping!!
                    if (isTyping == true) {
                        binding.toolbar.isActive.text = "typing.."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("err", error.message)
                }

            })

    }

    private fun statusBarBackground() {
        //Status BAr Background
        window.apply { decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR }
        window.statusBarColor = ContextCompat.getColor(this, R.color.primary)
    }

    private fun sendMessage() {
        binding.send.setOnClickListener {
            if (validMessage()) {
                viewModel.sendMessage(message, senderUid)
                binding.editTextWriteMessage.text?.clear()
                FirebaseInstance.firebaseDb
                    .child("user")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("typing").setValue(false)
                binding.editTextWriteMessage.clearFocus()
            }
        }
    }

    private fun setClickOnDotMenu() {
        //Set on click on menu time
        binding.toolbar.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.block -> {
                    //navController.navigate(R.id.action_homeFragment_to_newMessageFragment)
                }

                R.id.profile -> {
                    val currentId = FirebaseInstance.firebaseAuth.currentUser?.uid
                    navController.let {
                        navController?.navigate(R.id.profileFragment)
                    }
                    val bundle = Bundle().apply {
                        putString("navConroller", currentId)
                    }

                    //navController.navigate(R.id.profileFragment,bundle)
                }

                R.id.changeBg -> {
                    changeBackground()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun changeBackground() {

        if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES, GALLERY_REQ_CODE)){
            openGallery()
        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1) { permission }, requestCode)
            ActivityCompat.requestPermissions(this, Array(1) { permission }, PERMISSION_REQ_ID_RECORD_AUDIO)
            return false
        }
        return true
    }

    private fun openGallery() {
        val galleryIntent = Intent()
        galleryIntent.type = "image/**"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQ_CODE -> {
                try {
                    //val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, profile)
                    val backgroundImage = data!!.data
                    setThisBackgroundImageToFirebaseStrorage(backgroundImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setThisBackgroundImageToFirebaseStrorage(backgroundImage: Uri?) {
        binding.chatBackground.setImageURI(backgroundImage)
        if (backgroundImage != null){
            val ref = FirebaseInstance.firebaseStorage.child("chatBackground/"+ FirebaseInstance.firebaseAuth.currentUser?.email)
                ref.putFile(backgroundImage)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        FirebaseInstance.firebaseDb.child("user").child(receiverUid)
                            .child("chatBackground").setValue(it.toString())
                    }
                }
        }
    }

    private fun senderAndReceiverRoom() {
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
    }

    private fun initArrListAndAdapter() {
        msgList = ArrayList()
        adapter = MessageAdapter(this, msgList)
    }

    private fun getDataFromIntent() {
        userName = intent.extras?.getString("userName").toString()
        receiverUid = intent.extras?.getString("uid").toString()
        picUrl = intent.extras?.getString("pic").toString()
        isActive = intent.extras?.getBoolean("isActive") == true
        lastSeen = intent.extras?.getString("lastSeen").toString() //2023-08-25 11:06:54 PM

    }

    private fun setNameAndProfilePicOnToolBar() {
        binding.toolbar.userName.text = userName
        val img = binding.toolbar.profileImage
        Glide.with(this).load(picUrl).into(img)
        if (isActive) {
            binding.toolbar.isActive.text = "online"
        } else {
            val date = Utils.getDateFromCalender(lastSeen) //2023-08-25
            val currentDatee = Utils.dateTime(Calendar.getInstance())
            val currentDateOutput = Utils.getDateFromCalender(currentDatee)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd")

            val lastDate = dateFormat.parse(date)//last seen 27
            val currentDate = dateFormat.parse(currentDateOutput)//current time 26

            // Compare dates
            if (lastDate!!.before(currentDate)) {
                binding.toolbar.isActive.text = "a day ago "
            } else if (lastDate.after(currentDate)) {
                binding.toolbar.isActive.text = Utils.getTimeFromCalender(lastSeen)
            } else {
                binding.toolbar.isActive.text = Utils.getTimeFromCalender(lastSeen)
            }

        }
        binding.toolbar.back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        showTypingPlaceHolder()
    }

    private fun validMessage(): Boolean {
        message = binding.editTextWriteMessage.text.toString()
        if (message.isEmpty() && message == "") {
            return false
        }
        return true
    }

    private fun setDataToRecyclerView() {
        viewModel.addMessageToFirebaseDb(senderUid, receiverUid)
        viewModel._messageLiveData.observe(this, Observer { it ->
            if (!it.isNullOrEmpty()) {
                msgList.addAll(it)
                adapter = MessageAdapter(this, it)
                binding.recyclerViewMessage.adapter = adapter
                adapter.notifyItemInserted(msgList.size)
                binding.recyclerViewMessage.scrollToPosition(adapter.msgList.size - 1)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseInstance.firebaseDb
            .child("user")
            .child(receiverUid)
            .child("typing").setValue(false)
    }

}