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
import androidx.activity.OnBackPressedCallback
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
import com.example.firebasechat.mvvm.StatusViewModel
import com.example.firebasechat.mvvm.UserViewModel
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.ui.mainUi.adapter.UserAdapter
import com.example.firebasechat.utils.Constant.CAMERA_REQ_CODE
import com.example.firebasechat.utils.Constant.GALLERY_REQ_CODE
import com.example.firebasechat.utils.Constant.VIDEO_REQ_CODE
import com.example.firebasechat.utils.Response
import com.example.firebasechat.utils.Utils
import com.example.firebasechat.utils.hide


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: UserAdapter
    lateinit var userList: ArrayList<User>
    lateinit var statusList: ArrayList<Status>
    lateinit var statusAdapter: StatusAdapter
    private val userViewModel by viewModels<UserViewModel>()
    private val statusViewModel by viewModels<StatusViewModel>()

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
        binding.progressBarStatus.hide()

        binding.statusImage.setOnClickListener { viewMyStatus() }

        binding.addStatus.setOnClickListener {
            uploadStatus()
        }

        setUserToRecyclerView()
        setStatusRecyclerView()

    }

    private fun viewMyStatus() {
        findNavController().navigate(R.id.myStatusFragment)
    }

    private fun setStatusRecyclerView() {
        statusViewModel.getStatusFromFirebaseDb()
        statusViewModel._statusLiveData.observe(viewLifecycleOwner){ it ->
            when(it){
                is Response.Success->{
                    if (!it.data.isNullOrEmpty()){
                        var url = ""
                        statusList.addAll(it.data)
                        binding.progressBarStatus.hide()
                        it.data.forEach { url = it.statusUrl.toString() }
                        Glide.with(requireContext()).load(url).into(binding.statusImage)
                        statusAdapter = StatusAdapter(requireContext(),it.data,this)
                        binding.recyclerViewStatus.adapter = statusAdapter
                        statusAdapter.setData(it.data)
                    }
                }
                is Response.Error->{
                    binding.progressBarStatus.hide()
                }
                is Response.Loading->{
                    binding.progressBarStatus.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUserToRecyclerView() {
        userViewModel.getUserFromFirebaseDb()
        userViewModel._userLiveData.observe(viewLifecycleOwner, Observer {
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
        })
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
        galleryIntent.type = "image/**"
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
                    //statusViewModel.addStatusToFirebaseDb(status!!,userList)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            VIDEO_REQ_CODE -> {
                try {
                    val bitmap = data!!.extras!!.get("data") as Bitmap
                    status = Utils.getUriFromFile(requireContext(), bitmap)
                    //statusViewModel.addStatusToFirebaseDb(status!!,userList)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            GALLERY_REQ_CODE -> {
                try {
                    status = data!!.data
                    statusViewModel.addStatusToFirebaseDb(status!!)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).hideToolbarItem()
        (requireActivity() as MainActivity).binding.toolbar.toolbar.title = null
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