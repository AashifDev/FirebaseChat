package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentViewStatusBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.utils.App
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ViewStatusFragment : Fragment() {
    lateinit var binding: FragmentViewStatusBinding
    lateinit var statusList: ArrayList<Status>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var adapter: StatusAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewStatusBinding.inflate(layoutInflater, container, false)
        statusList = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = StatusAdapter(requireContext(), statusList)
        setStatusToRecyclerView()
    }

    private fun setStatusToRecyclerView() {
        firebaseDb.child("status").child("uid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                statusList.clear()
                for (postSnapshot in snapshot.children){
                    val status = postSnapshot.getValue(Status::class.java)
                    statusList.add(status!!)
                    binding.recyclerViewStatus.adapter = adapter
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err",error.message)
            }

        })
    }

}