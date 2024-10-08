package com.example.firebasechat.ui.authWithMobile.fragment

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
import android.text.TextUtils
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentAddProfileBinding
import com.example.firebasechat.model.User
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.activity.MainActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class AddProfileFragment : Fragment() {
    lateinit var binding: FragmentAddProfileBinding
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference

    val CAMERA_REQ_CODE = 101
    val GALLERY_REQ_CODE = 102
    var uid = ""
    var verificationId = ""
    var userName = ""
    var number = ""
    var profile: Uri? = null
    var file: Uri? = null
    var profileImage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProfileBinding.inflate(layoutInflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        uid = arguments?.getString("uid").toString()
        verificationId = arguments?.getString("verificationId").toString()

        binding.progressBarSave.visibility = View.GONE

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.textViewSave.setOnClickListener {
            if (validCredential()) {
                addUserToDbWithMobileNumber(userName)
                binding.progressBarSave.visibility = View.VISIBLE
                binding.textViewSave.alpha = .5f
            }
        }

        binding.profile.setOnClickListener {
            uploadImageDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun uploadImageDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.upload_profile_layout)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)

        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM

        dialog.window?.attributes = lp
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog?.window?.attributes?.windowAnimations = R.style.CustomDialogAnimation

        val camera = dialog.findViewById(R.id.camera) as ImageView
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openGallery() {
        val galleryIntent = Intent()
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
                    profile = Utils.getUriFromFile(requireContext(), bitmap)
                    binding.profile.setImageURI(profile)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            GALLERY_REQ_CODE -> {
                try {
                    //val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, profile)
                    profile = data!!.data
                    binding.profile.setImageURI(profile)
                    profileImage = true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun validCredential(): Boolean {
        userName = binding.etName.text.toString()
        if (TextUtils.isEmpty(userName) && userName == "") {
            binding.inputLayoutUserName.helperText = "Enter Name"
            return false
        }
        return true
    }

    private fun addUserToDbWithMobileNumber(
        name: String
    ) {

        if (profileImage != null) {

            number = firebaseAuth.currentUser?.phoneNumber.toString()
            if (!number.isNullOrEmpty()) {
                PrefManager.saveUserWithNumber(number)
            }

            uid = firebaseAuth.currentUser?.uid.toString()
            if (profileImage != null) {
                val ref =
                    firebaseStorage.reference.child("profileImagePhoneUser/" + firebaseAuth.currentUser?.phoneNumber)
                ref.putFile(profile!!)
                    .addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            val path = it.toString()
                            val user = User(name, null, uid, number, path)
                            firebaseDb.child("user").child(uid).setValue(user)
                        }
                    }
            }

            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()

            Utils.createToast(App.context(), "Registration completed")
        } else {
            binding.progressBarSave.visibility = View.GONE
            binding.textViewSave.alpha = 0f
            Utils.createToast(App.context(), "Please upload profile image")
        }
    }

}