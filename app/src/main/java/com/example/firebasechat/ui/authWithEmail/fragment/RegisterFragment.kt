package com.example.firebasechat.ui.authWithEmail.fragment

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentRegisterBinding
import com.example.firebasechat.mvvm.model.User
import com.example.firebasechat.session.PrefManager
import com.example.firebasechat.ui.mainUi.MainActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage

    val CAMERA_REQ_CODE = 101
    val GALLERY_REQ_CODE = 102
    var email = ""
    var pass = ""
    var conPass = ""
    var name = ""
    var uid = ""
    var ref: StorageReference? = null
    var mobileNumber = ""
    var profile: Uri? = null
    var file: Uri? = null
    var profileImage: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference
        firebaseStorage = Firebase.storage

        binding.progressCircular.visibility = View.GONE

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            uploadImageDialog()
        }

        binding.textViewLoginWithEmail.setOnClickListener { findNavController().navigate(R.id.loginFragment) }

        binding.register.setOnClickListener {
            if (validCredential()) {
                setRegister()
                binding.progressCircular.visibility = View.VISIBLE
                binding.register.alpha = .5f
            }
        }
    }

    private fun setRegister() {
        firebaseAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Successfully Registered", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                    addUserToFirebaseDatabase(name, email, profile, mobileNumber)
                    PrefManager.saveUserWithEmail(email)
                    binding.register.alpha = 1f
                } else {
                    binding.progressCircular.visibility = View.GONE
                    binding.register.alpha = 1f
                }
            }
            .addOnFailureListener {
                Utils.createToast(App.context(), it.message.toString())
            }
    }

    private fun addUserToFirebaseDatabase(
        name: String,
        email: String,
        profile: Uri?,
        mobileNumber: String?
    ) {
        uid = firebaseAuth.currentUser?.uid.toString()
        if (profile != null){
            val ref = firebaseStorage.reference.child("profileImageEmailUser/" + firebaseAuth.currentUser?.email)
            ref.putFile(profile)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        val path = it.toString()
                        val user = User(name, email, uid, mobileNumber, path)
                        firebaseDb.child("user").child(uid).setValue(user)
                    }
                }
        }

    }

    private fun validCredential(): Boolean {
        name = binding.etName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        pass = binding.etPass.text.toString().trim()
        conPass = binding.etConPass.text.toString().trim()
        if (TextUtils.isEmpty(name) && name == "") {
            binding.llName.helperText = "Name is blank"
            return false
        } else if (TextUtils.isEmpty(email) && email == "") {
            binding.llEmail.helperText = "Email address is blank"
            return false
        } else if (TextUtils.isEmpty(pass) && pass == "") {
            binding.llPass.helperText = "Password is blank"
            return false
        } else if (pass.length < 6) {
            Utils.createToast(App.context(), "Minimum 6 character required")
            return false
        } else if (conPass != pass) {
            binding.llConPass.helperText = "Password does not matched"
            return false
        } else if (!profileImage) {
            Utils.createToast(App.context(), "Please upload image")
            return false
        }
        return true
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
}