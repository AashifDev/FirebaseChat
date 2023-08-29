package com.example.firebasechat.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.ActivityCallBinding
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

class CallActivity : AppCompatActivity() {
    lateinit var binding: ActivityCallBinding
    private val uid = "0"
    private var isJoined = false
    // Agora engine instance
    private var agoraEngine: RtcEngine? = null
    private var mRtcEventHandler: IRtcEngineEventHandler? = null

    var userName = ""
    var picUrl = ""
    var isSpeakerOn:Boolean = false
    var isMute:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userName = intent.extras?.getString("name").toString()
        picUrl = intent.extras?.getString("pic").toString()

        setData()
        setUpAgoraEngine()

        rtcEventHandler()

        setUpClick()

    }

    private fun setUpClick() {
        binding.handUp.setOnClickListener {
            isJoined = false
            agoraEngine!!.leaveChannel()
        }

        binding.speaker.setOnClickListener {
            if (isSpeakerOn == false){
                agoraEngine?.setEnableSpeakerphone(true)
                isSpeakerOn = true
                binding.speaker.setBackgroundColor(getColor(R.color.grey))
            }else{
                agoraEngine?.setEnableSpeakerphone(false)
                isSpeakerOn = false
                binding.speaker.setBackgroundColor(0)
            }
        }

        binding.mute.setOnClickListener {
            if (isMute == false){
                agoraEngine?.muteLocalAudioStream(true)
                isMute = true
                binding.mute.setBackgroundColor(getColor(R.color.grey))
            }else{
                agoraEngine?.muteLocalAudioStream(false)
                isMute = false
                binding.mute.setBackgroundColor(0)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            joinLeaveChannel()
        },5000)
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

            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                // Successfully joined a channel
                isJoined = true
                showMessage("Joined Channel $channel")
                runOnUiThread { binding.connecting.text = "Calling.." }
            }

            override fun onUserOffline(uid: Int, reason: Int) {
                // Listen for remote users leaving the channel
                showMessage("Remote user offline $uid $reason")
                if (isJoined == false) runOnUiThread {
                    binding.connecting.text = "Reconnecting.."
                }
            }

            override fun onLeaveChannel(stats: RtcStats) {
                // Listen for the local user leaving the channel
                runOnUiThread { binding.connecting.text = "reconnectiong..." }
                finish()
                isJoined = false
            }
        }
    }

    private fun setUpAgoraEngine() {
        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO,
                101)){
            setupVoiceSDKEngine()
        }
    }

    private fun checkSelfPermission(recordAudio: String,permissionReqIdRecordAudio: Int): Boolean {

        if (ActivityCompat.checkSelfPermission(this, recordAudio) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, Array(1) { recordAudio }, permissionReqIdRecordAudio)
            return false
        }
        return true
    }



    private fun setupVoiceSDKEngine() {
        initializeAgoraEngine()
        joinVoiceChannel()
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

    private fun joinVoiceChannel() {
        voiceConfig()
    }

    private fun voiceConfig() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        agoraEngine?.setEnableSpeakerphone(false)
        agoraEngine?.setDefaultAudioRoutetoSpeakerphone(false)
        agoraEngine?.joinChannel(null, "Testing", 0, options)
    }

    fun joinLeaveChannel() {
        if (isJoined) {
            agoraEngine!!.leaveChannel()
            //binding.text.text = "Join"
            Toast.makeText(this, "Connected.", Toast.LENGTH_SHORT).show()
        } else {
            joinVoiceChannel()
            //binding.text.text = "Leave"
            Toast.makeText(this, "Disconnected..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMessage(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.leaveChannel()
    }
}