package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.databinding.FragmentViewSendMessageBinding
import com.example.firebasechat.model.Message
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewSendMessageFragment : Fragment() {
    lateinit var binding: FragmentViewSendMessageBinding
    lateinit var adapter: MessageAdapter
    lateinit var msgList: ArrayList<Message>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference

    var senderRoom:String? = null
    var receiverRoom:String? = null

    var userName = ""
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

        (requireActivity() as MainActivity).toolbar.title = userName
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        msgList = ArrayList()
        adapter = MessageAdapter(requireContext(),msgList)

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.send.setOnClickListener {
            sendMessage()
        }

        setDataToRecyclerView()

    }

    private fun setDataToRecyclerView() {
        firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    msgList.clear()
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        msgList.add(message!!)
                        adapter = MessageAdapter(requireContext(),msgList)
                        binding.recyclerViewMessage.adapter = adapter
                    }
                    adapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("err",error.message)
                }

            })
    }

    private fun sendMessage() {
        message = binding.editTextWriteMessage.text.toString()
        val msg = Message(message,senderUid)
        firebaseDb.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(msg)
            .addOnSuccessListener {
                firebaseDb.child("chats").child(receiverRoom!!).child("messages").push().setValue(msg)
            }
        binding.editTextWriteMessage.text?.clear()
    }



}