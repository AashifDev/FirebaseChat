package com.example.firebasechat.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityVideoCallBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VideoCallActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoCallBinding
    private val uid = "0"
    private var isJoined = false
    // Agora engine instance
    private var agoraEngine: RtcEngine? = null
    private var mRtcEventHandler: IRtcEngineEventHandler? = null

    var PERMISSION_REQ_ID_RECORD_AUDIO = 101
    var userName = ""
    var picUrl = ""
    var isSpeakerOn:Boolean = false
    var isMute:Boolean = true
    var isVisiblee:Boolean = false
    private var remoteView: SurfaceView? = null
    private var localView: SurfaceView? = null

    var bottomSheetDialog : BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.extras?.getString("name").toString()
        picUrl = intent.extras?.getString("pic").toString()

        bottomSheetDialog = BottomSheetDialog(this)
        setData()
        setUpAgoraEngine()

        rtcEventHandler()

        setUpClick()

        installBottomSheet()

        binding.fullScreenVidoFrame.setOnClickListener {
            bottomSheetDialog!!.show()
        }
    }

    private fun installBottomSheet() {
        bottomSheetDialog!!.setContentView(R.layout.bottom_sheet)
        val hangUp = bottomSheetDialog!!.findViewById<ImageView>(R.id.handUp)
        val speaker = bottomSheetDialog!!.findViewById<ImageView>(R.id.speaker)
        val mute = bottomSheetDialog!!.findViewById<ImageView>(R.id.mute)
        val switchCam = bottomSheetDialog!!.findViewById<ImageView>(R.id.switchCamera)

        hangUp!!.setOnClickListener {
            leaveChannel()
        }
        speaker!!.setOnClickListener {
            if (isSpeakerOn == false){
                agoraEngine?.setEnableSpeakerphone(true)
                isSpeakerOn = true
                speaker.setBackgroundColor(getColor(R.color.grey))
            }else{
                agoraEngine?.setEnableSpeakerphone(false)
                isSpeakerOn = false
                speaker.setBackgroundColor(0)
            }
        }

        mute!!.setOnClickListener {
            if (isMute == false){
                agoraEngine?.muteLocalAudioStream(true)
                isMute = true
                mute.setBackgroundColor(getColor(R.color.grey))
            }else{
                agoraEngine?.muteLocalAudioStream(false)
                isMute = false
                mute.setBackgroundColor(0)
            }
        }

        switchCam!!.setOnClickListener {

        }
        bottomSheetDialog!!.show()
    }

    private fun setUpClick() {
        Handler(Looper.getMainLooper()).postDelayed({
            joinVideoChannel()
        },5000)

        /*binding.linearLayoutButton.setOnClickListener {
            if (isVisiblee){
                isVisiblee = true
                binding.linearLayoutButton.visibility = View.VISIBLE
            }else{
                isVisiblee = false
                binding.linearLayoutButton.visibility = View.INVISIBLE
            }

        }*/
    }

    private fun setData() {
        Glide.with(this).load(picUrl).into(binding.profileImage)
        binding.profileName.text = userName
    }

    private fun rtcEventHandler() {
        mRtcEventHandler = object : IRtcEngineEventHandler() {
            // Listen for the remote user joining the channel.
            override fun onUserJoined(uid: Int, elapsed: Int) {
                runOnUiThread { binding.connecting.text = "Connected." }
            }
            override fun onUserOffline(uid: Int, reason: Int) {
                // Listen for remote users leaving the channel
                runOnUiThread {  onRemoteUserLeft() }

            }

            override fun onLeaveChannel(stats: RtcStats) {
                // Listen for the local user leaving the channel
                runOnUiThread { binding.connecting.text = "reconnectiong..." }
                finish()
                isJoined = false
            }


            override fun onFirstRemoteVideoFrame(uid: Int, width: Int, height: Int, elapsed: Int) {
                super.onFirstRemoteVideoFrame(uid, width, height, elapsed)
                setupRemoteVideoStream(uid)
            }

            // remote user has toggled their video
            override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
                // runOnUiThread { onRemoteUserVideoToggle(uid, state) }

                if (state === Constants.REMOTE_VIDEO_STATE_STARTING) {
                    runOnUiThread { setupRemoteVideoStream(uid) }
                }

            }
        }
    }

    private fun setupRemoteVideoStream(uid: Int) {
        if (binding.fullScreenVidoFrame.childCount > 1) {
            return
        }
        remoteView = RtcEngine.CreateRendererView(baseContext)
        binding.fullScreenVidoFrame.addView(remoteView)
        agoraEngine?.setupRemoteVideo(VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun setUpAgoraEngine() {
        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                101,102,103)){
            setupVoiceSDKEngine()
        }
    }

    private fun checkSelfPermission(recordAudio: String, camera: String,readPhoneState:String,permissionReqIdCamera: Int,permissionReqIdRecordAudio: Int, permissionReqPhoneState:Int): Boolean {

        if (ActivityCompat.checkSelfPermission(this, recordAudio) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1) { recordAudio }, permissionReqIdCamera)
            return false
        }else if (ActivityCompat.checkSelfPermission(this, camera) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1) { camera }, permissionReqIdRecordAudio)
            return false
        }else if (ActivityCompat.checkSelfPermission(this, readPhoneState) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1) { readPhoneState }, permissionReqPhoneState)
            return false
        }
        return true
    }

    private fun setupVoiceSDKEngine() {
        initializeAgoraEngine()
        joinVideoChannel()
        setupLocalVideoFeed()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initializeAgoraEngine() {
        GlobalScope.launch (Dispatchers.Main){
            val config = RtcEngineConfig()
            config.mContext = applicationContext
            config.mAppId = getString(R.string.app_id)
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
        }

    }

    private fun joinVideoChannel(){
        videoConfig()
    }
    private fun videoConfig() {
        val options = ChannelMediaOptions()
        agoraEngine?.enableLocalVideo(true)
        agoraEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        agoraEngine?.enableVideo()
        agoraEngine?.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
        agoraEngine?.joinChannel(null, "Testing", 0, options)
        binding.floatingVideoFrame.visibility = View.VISIBLE
        binding.linearLayout.visibility = View.GONE
        setupLocalVideoFeed()
    }

    private fun setupLocalVideoFeed() {
        localView = RtcEngine.CreateRendererView(baseContext)
        localView?.setZOrderMediaOverlay(true)
        //binding.floatingVideoFrame.addView(localView)
        agoraEngine?.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    override fun onDestroy() {
        super.onDestroy()
        leaveChannel()
        RtcEngine.destroy()
        agoraEngine = null
    }

    private fun onRemoteUserLeft() {
        removeVideo(R.id.fullScreenVidoFrame)
    }

    fun onLeaveChannelClicked(view: View?) {
        leaveChannel()
        removeVideo(R.id.floatingVideoFrame)
        removeVideo(R.id.fullScreenVidoFrame)
    }

    private fun leaveChannel() {
        agoraEngine?.leaveChannel()
    }

    private fun removeVideo(containerID: Int) {
        val videoContainer = findViewById<FrameLayout>(containerID)
        videoContainer.removeAllViews()
    }

    /*fun onVideoMuteClicked(view: View) {
        val btn = view as ImageView
        if (btn.isSelected) {
            btn.isSelected = false
            btn.setImageResource(R.drawable.baseline_cameraswitch_24)
        } else {
            btn.isSelected = true
            btn.setImageResource(R.drawable.baseline_cameraswitch_24)
        }
        agoraEngine?.muteLocalVideoStream(btn.isSelected)
        binding.floatingVideoFrame.visibility =
            if (btn.isSelected) View.GONE else View.VISIBLE
        val videoSurface = viewDataBinding.floatingVideoContainer.getChildAt(0) as SurfaceView
        videoSurface.setZOrderMediaOverlay(!btn.isSelected)
        videoSurface.visibility = if (btn.isSelected) View.GONE else View.VISIBLE
    }*/


}