package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentMyStatusBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.mvvm.StatusViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.utils.FirebaseInstance
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.hide
import com.example.firebasechat.utils.show
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyStatusFragment : Fragment() {
    lateinit var binding: FragmentMyStatusBinding
    lateinit var statusList: ArrayList<Status>
    lateinit var statusAdapter: StatusAdapter
    private val statusViewModel by viewModels<StatusViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyStatusBinding.inflate(layoutInflater, container, false)

        statusList = ArrayList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        statusAdapter = StatusAdapter(requireContext(),statusList,this)
        setStatusRecyclerView()
    }

    private fun setStatusRecyclerView() {
        statusViewModel.getStatusFromFirebaseDb()
        statusViewModel._statusLiveData.observe(viewLifecycleOwner, Observer { it ->
            if (!it.isNullOrEmpty()){
                var url = ""
                statusList.addAll(it)
                //binding.progressBarStatus.hide()
                //Glide.with(requireContext()).load(url).into(binding.statusImage)
                statusAdapter = StatusAdapter(requireContext(),it, this)
                binding.recyclerViewStatus.adapter = statusAdapter
                statusAdapter.setData(it)
            }
        })
        FirebaseInstance.firebaseDb.child("status").child("data").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    statusList.clear()
                    for (postSnapshot in snapshot.children){
                        val status = postSnapshot.getValue(Status::class.java)
                        if (firebaseAuth.currentUser!!.uid != status!!.id){
                            statusList.add(status)
                        }
                        statusList.add(status!!)
                        val resourceId = status.statusUrl
                        binding.recyclerViewStatus.adapter = statusAdapter
                        statusAdapter.setData(statusList)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("err",error.message)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showToolbarItem()
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