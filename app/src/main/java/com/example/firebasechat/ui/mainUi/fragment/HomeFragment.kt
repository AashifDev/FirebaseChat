package com.example.firebasechat.ui.mainUi.fragment

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentHomeBinding
import com.example.firebasechat.model.Status
import com.example.firebasechat.model.User
import com.example.firebasechat.mvvm.FirebaseViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.ui.mainUi.adapter.UserAdapter
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.Utils


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: UserAdapter
    lateinit var userList: ArrayList<User>
    lateinit var statusList: ArrayList<Status>
    lateinit var statusAdapter: StatusAdapter
    private val viewModels by viewModels<FirebaseViewModel>()

    val CAMERA_REQ_CODE = 101
    val GALLERY_REQ_CODE = 102
    val VIDEO_REQ_CODE = 103

    var uid:String? = null
    var profile: Uri? = null
    var status: Uri? = null
    var statuss: Status? = null
    var path = ""
    var user: User? = null
    var file: Uri? = null
    var profileImage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        userList = ArrayList()
        statusList = ArrayList()

        binding.progressBar.visibility = View.VISIBLE
        binding.noChat.visibility = View.GONE
        binding.progressBarStatus.visibility = View.GONE

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.statusImage.setOnClickListener { viewMyStatus() }
        addStatus()

        setUserToRecyclerView()
        setStatusRecyclerView()

    }

    private fun viewMyStatus() {
        findNavController().navigate(R.id.myStatusFragment)
    }

    private fun setStatusRecyclerView() {
        /*val layoutManager = LinearLayoutManager(App.context())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        binding.recyclerViewStatus.layoutManager = layoutManager*/

        viewModels.getStatusFromFirebaseDb()
        viewModels.statusLiveData.observe(viewLifecycleOwner, Observer { it ->
            /*if (!it.isNullOrEmpty()){
                var url = ""
                statusList.addAll(it)
                it.forEach { url = it.statusUrl.toString() }
                Glide.with(requireContext()).load(url).into(binding.statusImage)
                statusAdapter = StatusAdapter(requireContext(),it,this@HomeFragment)
                binding.recyclerViewStatus.adapter = statusAdapter
                statusAdapter.setData(it)
            }*/
            when(it){
                is Response.Success->{
                    if (!it.data.isNullOrEmpty()){
                        var url = ""
                        statusList.addAll(it.data)
                        it.data.forEach { url = it.statusUrl.toString() }
                        Glide.with(requireContext()).load(url).into(binding.statusImage)
                        statusAdapter = StatusAdapter(requireContext(),it.data,this@HomeFragment)
                        binding.recyclerViewStatus.adapter = statusAdapter
                        statusAdapter.setData(it.data)
                    }
                }
                is Response.Error->{

                }
                is Response.Loading->{}
            }
        })

        /*firebaseDb.child("status").child("uid").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    statusList.clear()
                    for (postSnapshot in snapshot.children){
                        val status = postSnapshot.getValue(Status::class.java)
                        if (firebaseAuth.currentUser!!.uid != status!!.id){
                            statusList.add(status)
                        }
                        val resourceId = status.statusUrl
                        Glide.with(requireContext()).load(resourceId).into(binding.statusImage)
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
        })*/
    }

    private fun setUserToRecyclerView() {
        viewModels.getUserFromFirebaseDb()
        viewModels.userLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is Response.Loading->{
                    binding.progressBar.visibility = View.GONE
                    binding.noChat.visibility = View.VISIBLE
                }
                is Response.Error->{
                    binding.progressBar.visibility = View.GONE
                    binding.noChat.visibility = View.VISIBLE
                }
                is Response.Success->{
                    if (!it.data.isNullOrEmpty()){
                        binding.progressBar.visibility = View.GONE
                        binding.noChat.visibility = View.GONE
                        userList.addAll(it.data)
                        adapter = UserAdapter(requireContext(),it.data)
                        binding.recyclerViewUser.adapter = adapter

                    }
                }
            }
     /*       if (it.isNullOrEmpty()){
                binding.progressBar.visibility = View.GONE
                binding.noChat.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
                binding.noChat.visibility = View.GONE

                userList.addAll(it)
                adapter = UserAdapter(requireContext(),it)
                binding.recyclerViewUser.adapter = adapter
            }*/

        })

        /*firebaseDb.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapShot in snapshot.children) {
                    user = postSnapShot.getValue(User::class.java)
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

        })*/
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun addStatus() {
        binding.addStatus.setOnClickListener {
            uploadStatus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun uploadStatus() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.upload_status_dialog_layout)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)

        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM

        dialog.window?.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val camera = dialog.findViewById(R.id.camera) as ImageView
        val video = dialog.findViewById(R.id.video) as ImageView
        val gallery = dialog.findViewById(R.id.gallery) as ImageView

        camera.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), Array(1) { android.Manifest.permission.CAMERA },
                    CAMERA_REQ_CODE
                )
            } else {
                openCamera()
            }
            dialog.dismiss()
        }

        video.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    Array(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                    VIDEO_REQ_CODE
                )
            } else {
                openVideo()
            }
            dialog.dismiss()
        }

        gallery.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    Array(1) { android.Manifest.permission.READ_EXTERNAL_STORAGE },
                    GALLERY_REQ_CODE
                )
            } else {
                openGallery()
            }
            dialog.dismiss()
        }

        dialog.show()

        dialog.setCancelable(true)
    }
    private fun openVideo() {
        val videoIntent = Intent()
        videoIntent.type = "video/*"
        videoIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(videoIntent, GALLERY_REQ_CODE)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(galleryIntent, GALLERY_REQ_CODE)

    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQ_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_REQ_CODE -> {
                try {
                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    status = Utils.getUriFromFile(requireContext(), bitmap)
                    viewModels.addStatusToFirebaseDb(status!!,statusList)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            VIDEO_REQ_CODE -> {
                try {
                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    status = Utils.getUriFromFile(requireContext(), bitmap)
                    viewModels.addStatusToFirebaseDb(status!!,statusList)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            GALLERY_REQ_CODE -> {
                try {
                    status = data!!.data
                    viewModels.addStatusToFirebaseDb(status!!,statusList)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /*private fun success() {
        binding.progressBarStatus.visibility = View.VISIBLE
        val email = firebaseAuth.currentUser?.email.toString()
        val dateTime = Utils.dateTime(Calendar.getInstance())
        val id = firebaseAuth.currentUser!!.uid
        if (status != null){
            val ref = firebaseStorage.child("status/").child(email).child("status/$dateTime")
            ref.putFile(status!!)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener {
                            path = it.toString()
                            val usrName = user!!.name
                            val profileImage = user!!.pic
                            val status = Status(id,path,usrName,profileImage,dateTime)
                            firebaseDb.child("status").child("uid").push().setValue(status)
                            binding.progressBarStatus.visibility = View.GONE
                        }
                        .addOnFailureListener {
                            Log.e("err",it.message.toString())
                            Utils.createToast(App.context(), "Try again")
                            binding.progressBarStatus.visibility = View.GONE
                        }
                }
        }
    }*/

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).hideToolbarItem()

    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).hideToolbarItem()
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