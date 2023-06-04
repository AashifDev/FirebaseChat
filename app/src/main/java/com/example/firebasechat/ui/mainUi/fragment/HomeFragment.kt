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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentHomeBinding
import com.example.firebasechat.mvvm.model.Status
import com.example.firebasechat.mvvm.model.User
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.Calendar

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: UserAdapter
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var userList: ArrayList<User>
    lateinit var statusList: ArrayList<Status>
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var statusAdapter: StatusAdapter

    val CAMERA_REQ_CODE = 101
    val GALLERY_REQ_CODE = 102
    val VIDEO_REQ_CODE = 103

    var uid:String? = null
    var profile: Uri? = null
    var status: Uri? = null
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

        statusList = ArrayList()

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        binding.progressBar.visibility = View.VISIBLE
        binding.noChat.visibility = View.GONE
        binding.shimmerStatus.stopShimmer()
        binding.shimmerStatus.hideShimmer()


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userList = ArrayList()
        statusList = ArrayList()

        adapter = UserAdapter(requireContext(),userList)
        statusAdapter = StatusAdapter(requireContext(),statusList,this@HomeFragment)

        addStatus()


        setUserToRecyclerView()
        setStatusRecyclerView()


    }

    private fun setStatusRecyclerView() {
        /*val layoutManager = LinearLayoutManager(App.context())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        binding.recyclerViewStatus.layoutManager = layoutManager*/
        firebaseDb.child("status").child("uid").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                statusList.clear()
                for (postSnapshot in snapshot.children){
                    val status = postSnapshot.getValue(Status::class.java)
                    if (firebaseAuth.currentUser!!.uid != status!!.id){
                        statusList.add(status)
                    }
                    binding.recyclerViewStatus.adapter = statusAdapter
                    statusAdapter.setData(statusList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("err",error.message)
            }

        })
    }

    private fun setUserToRecyclerView() {
        firebaseDb.child("user").addValueEventListener(object : ValueEventListener{
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

        })
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
                    success()
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            VIDEO_REQ_CODE -> {
                try {
                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    status = Utils.getUriFromFile(requireContext(), bitmap)
                    success()
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            GALLERY_REQ_CODE -> {
                try {
                    status = data!!.data
                    success()
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun success() {

        binding.shimmerStatus.visibility = View.VISIBLE
        binding.shimmerStatus.startShimmer()

        /*val email = firebaseAuth.currentUser?.email.toString()
        val dtForm: DateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.")
        val date: String = dtForm.format(Calendar.getInstance().time)
        val fileName = date + FirebaseAuth.getInstance().currentUser!!.uid*/
        val email = firebaseAuth.currentUser?.email.toString()

        val dateTime = Utils.dateTime(Calendar.getInstance())
        val id = firebaseAuth.currentUser!!.uid
        if (status != null){
            val ref = firebaseStorage.reference.child("status/").child(email).child("status/$dateTime")
            ref.putFile(status!!)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener {
                            path = it.toString()
                            val usrName = user!!.name
                            val profileImage = user!!.pic
                            //val dateTime = Utils.dateTime(Calendar.getInstance())
                            val status = Status(id,path,usrName,profileImage,dateTime)
                            firebaseDb.child("status").child("uid").push().setValue(status)
                            binding.shimmerStatus.visibility = View.GONE
                            binding.shimmerStatus.stopShimmer()
                        }
                        .addOnFailureListener {
                            Log.e("err",it.message.toString())
                            Utils.createToast(App.context(), "Try again")
                        }
                }
        }
    }

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