package com.example.firebasechat.ui.mainUi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentHomeBinding
import com.example.firebasechat.model.User
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.UserAdapter
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: UserAdapter
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var userList: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        binding.progressBar.visibility = View.VISIBLE
        binding.noChat.visibility = View.GONE


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList = ArrayList()
        adapter = UserAdapter(requireContext(),userList)


        setUserToRecyclerView()
    }

    private fun setUserToRecyclerView() {

        firebaseDb.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapShot in snapshot.children) {
                    val user = postSnapShot.getValue(User::class.java)
                    //current user will not show in list
                    if (firebaseAuth.currentUser?.uid != user?.uid){
                        userList.add(user!!)
                    }
                    if (userList.isEmpty()){
                        binding.progressBar.visibility = View.GONE
                        binding.noChat.visibility = View.VISIBLE
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.noChat.visibility = View.GONE
                    }
                    binding.recyclerViewUser.adapter = adapter
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err",error.message)
                binding.progressBar.visibility = View.GONE
            }

        })
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showToolbarItem()

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideToolbarItem()

        //(requireActivity() as MainActivity).binding.toolbar.more.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).showMenuItem()
        //(requireActivity() as MainActivity).binding.toolbar.more.visibility = View.VISIBLE
    }

}