package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.example.firebasechat.databinding.FragmentViewStatusBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.mvvm.StatusViewModel
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
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

       /* statusList = ArrayList()
        binding.progressBar.show()
        binding.noStatus.hide()
*/
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {




        //setStatusToRecyclerView()
    }

    private fun setStatusToRecyclerView() {
        viewModel.getStatusFromFirebaseDb()
        viewModel._statusLiveData.observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                binding.noStatus.show()
            }else{
                val id = it.forEach { it.id }
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



}