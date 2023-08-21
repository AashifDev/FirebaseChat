package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentViewStatusBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.mvvm.StatusViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.hide
import com.example.firebasechat.utils.show

class ViewStatusFragment : Fragment() {
    lateinit var binding: FragmentViewStatusBinding
    lateinit var statusList: ArrayList<Status>
    lateinit var adapter: StatusAdapter
    private val viewModel by viewModels<StatusViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewStatusBinding.inflate(layoutInflater, container, false)

        statusList = ArrayList()
        binding.progressBar.show()
        binding.noStatus.hide()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userProfile.setOnClickListener{

        }


        setStatusToRecyclerView()
    }

    private fun setStatusToRecyclerView() {
        viewModel.getStatusFromFirebaseDb()
        viewModel._statusLiveData.observe(viewLifecycleOwner){
            when(it){
                is Response.Success->{
                    if (it.data.isNullOrEmpty()){
                        binding.noStatus.show()
                    }else{
                        val id = it.data.forEach { it.id }
                        statusList.filter {
                            if (!it.equals(id)){
                                statusList.add(it)
                            }
                            true
                        }
                        binding.progressBar.hide()
                        adapter = StatusAdapter(requireContext(), statusList,this)
                        binding.recyclerViewStatus.adapter = adapter
                        //adapter.setData(it.data)
                    }
                }
                is Response.Error->{
                    Log.e("err",it.errorMessage.toString())
                }
                is Response.Loading->{
                    binding.progressBar.show()
                    binding.noStatus.hide()
                }
            }

        }
        /*firebaseDb.child("status").child("data").addValueEventListener(object : ValueEventListener {
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

        })*/
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