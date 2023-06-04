package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentViewStatusBinding
import com.example.firebasechat.mvvm.model.Status
import com.example.firebasechat.ui.mainUi.MainActivity
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


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        statusList = ArrayList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userProfile.setOnClickListener{

        }
        adapter = StatusAdapter(requireContext(), statusList,this)

        setStatusToRecyclerView()
    }

    private fun setStatusToRecyclerView() {
        firebaseDb.child("status").child("uid").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                statusList.clear()
                for (postSnapshot in snapshot.children){
                    val status = postSnapshot.getValue(Status::class.java)
                    if (firebaseAuth.currentUser!!.uid != status!!.id){
                        statusList.add(status)
                    }
                    binding.recyclerViewStatus.adapter = adapter
                    adapter.setData(statusList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err",error.message)
            }

        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = false

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.menu.findItem(R.id.account).isVisible = true
    }

}