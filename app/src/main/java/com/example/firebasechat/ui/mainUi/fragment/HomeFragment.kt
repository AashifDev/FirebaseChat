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
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
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
import com.example.firebasechat.ui.mainUi.adapter.StatusAdapter
import com.example.firebasechat.ui.mainUi.adapter.UserAdapter
import com.example.firebasechat.utils.Constant.CAMERA_REQ_CODE
import com.example.firebasechat.utils.Constant.GALLERY_REQ_CODE
import com.example.firebasechat.utils.Constant.VIDEO_REQ_CODE
import com.example.firebasechat.utils.FirebaseInstance.firebaseAuth
import com.example.firebasechat.utils.FirebaseInstance.firebaseDb
import com.example.firebasechat.utils.Utils
import com.example.firebasechat.utils.hide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import io.grpc.okhttp.internal.proxy.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: UserAdapter
    lateinit var userList: ArrayList<User>
    lateinit var statusList: ArrayList<Status>
    lateinit var statusAdapter: StatusAdapter
    private val userViewModel by viewModels<UserViewModel>()
    private val statusViewModel by viewModels<StatusViewModel>()

    var uid: String? = null
    var profile: Uri? = null
    var status: Uri? = null
    var path = ""
    var user: User? = null
    var file: Uri? = null
    var profileImage: Boolean = false
    var senderUid: String? = null
    var receiverUid = ""
    var senderRoom: String? = null
    var token:String? = null
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

        getFCMToken()

        //askNotificationPermission()

        Handler().postDelayed({
            if (firebaseAuth.currentUser != null){
                val uid = firebaseAuth.currentUser?.uid.toString()
                firebaseDb.child("user").child(uid).child("active").setValue(true)
            }
        }, 15000)


        binding.statusImage.setOnClickListener { viewMyStatus() }

        binding.addStatus.setOnClickListener {
            uploadStatus()
        }

        setUserToRecyclerView()
        setStatusRecyclerView()

        //setStatusToOnline()
        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        senderRoom = receiverUid + senderUid

        /*firebaseDb.child("chats").child(senderRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    // Handle the data that was added to the database.
                    val value = snapshot.getValue(Message::class.java)
                    // Trigger the notification here.
                    val senderId = value!!.senderId
                    if (!senderId!!.contains(firebaseAuth.currentUser!!.uid)) {
                        MyFirebaseMessagingService1()
                        //MyFirebaseMessagingService().createDefaultBuilder(value.message!!)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}

            })*/

        sendNotification()

    }

    private fun sendNotification() {
        /*val SERVER_KEY: String = getString(R.string.SERVER_KEY)
        val msg = "this is test message"
        val title = "my title"
        val token: String? = token

        var obj: JSONObject? = null
        var objData: JSONObject? = null
        var dataobjData: JSONObject? = null

        try {
            obj = JSONObject()
            objData = JSONObject()
            objData.put("body", msg)
            objData.put("title", title)
            objData.put("sound", "default")
            objData.put("icon", "icon_name") //   icon_name
            objData.put("tag", token)
            objData.put("priority", "high")
            dataobjData = JSONObject()
            dataobjData.put("text", msg)
            dataobjData.put("title", title)
            obj.put("to", token)
            //obj.put("priority", "high");
            obj.put("notification", objData)
            obj.put("data", dataobjData)
            Log.e("return here>>", obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsObjRequest: JsonObjectRequest =
            object : JsonObjectRequest(
                Request.Method.POST, Constants.FCM_PUSH_URL, obj,
                object : Listener<JSONObject?>() {
                    fun onResponse(response: JSONObject) {
                        Log.e("True", response.toString() + "")
                    }
                },
                object : ErrorListener() {
                    fun onErrorResponse(error: VolleyError) {
                        Log.e("False", error.toString() + "")
                    }
                }) {
                @get:Throws(AuthFailureError::class)
                val headers: Map<String, String>?
                    get() {
                        val params: MutableMap<String, String> = HashMap()
                        params["Authorization"] = "key=$SERVER_KEY"
                        params["Content-Type"] = "application/json"
                        return params
                    }
            }
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val socketTimeout = 1000 * 60 // 60 seconds

        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsObjRequest.setRetryPolicy(policy)
        requestQueue.add(jsObjRequest)*/
    }


    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("TAG", "Token is $token")
        })
    }

    private fun setStatusToOnline() {
        CoroutineScope(Dispatchers.IO).launch {
            val uid = firebaseAuth.currentUser?.uid.toString()
            var isActive: Boolean = false
            firebaseDb.child("user").child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val snapshot = snapshot.getValue(User::class.java)
                    isActive = snapshot!!.isActive

                    if (isActive == false) {
                        firebaseDb.child("user").child(uid).child("active").setValue(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }

            })
        }


    }

    private fun viewMyStatus() {
        findNavController().navigate(R.id.myStatusFragment)
    }

    private fun setStatusRecyclerView() {
        statusViewModel.getStatusFromFirebaseDb()
        statusViewModel._statusLiveData.observe(viewLifecycleOwner) { it ->
            if (!it.isNullOrEmpty()) {
                var url = ""
                statusList.addAll(it)
                binding.progressBarStatus.hide()
                it.forEach { url = it.statusUrl.toString() }
                Glide.with(requireContext()).load(url).into(binding.statusImage)
                statusAdapter = StatusAdapter(requireContext(), it, this)
                binding.recyclerViewStatus.adapter = statusAdapter
                statusAdapter.setData(it)
            }
        }
    }

    private fun setUserToRecyclerView() {
        userViewModel.getUserFromFirebaseDb()
        userViewModel._userLiveData.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.noChat.visibility = View.GONE
                userList.addAll(it)
                adapter = UserAdapter(requireContext(), it, this)
                binding.recyclerViewUser.adapter = adapter

            } else {
                binding.progressBar.visibility = View.GONE
                binding.noChat.visibility = View.VISIBLE
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

    fun getUid(uid: String?) {
        receiverUid = uid!!
    }
}