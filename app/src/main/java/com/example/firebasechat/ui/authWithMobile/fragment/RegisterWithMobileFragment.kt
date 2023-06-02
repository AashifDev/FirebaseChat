package com.example.firebasechat.ui.authWithMobile.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentRegisterWithMobileBinding
import com.example.firebasechat.ui.authWithEmail.AuthEmailActivity
import com.example.firebasechat.utils.App
import com.example.firebasechat.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class RegisterWithMobileFragment : Fragment() {
    lateinit var binding: FragmentRegisterWithMobileBinding
    lateinit var editTextMobileNumber: EditText
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDb: DatabaseReference

    var countryCode = ""
    var mobileNumber = ""
    var verificationId = ""
    var registerWith = "Register with phone"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterWithMobileBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = Firebase.database.reference

        binding.inputLayoutOtp.visibility = View.GONE
        binding.textViewSubmit.visibility = View.GONE
        binding.progressBarNext.visibility = View.GONE
        binding.progressBarSubmit.visibility = View.GONE

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //showKeyboardOnFragmentStart()

        binding.textViewNext.setOnClickListener {
            if (validMobileNumber()){
                binding.inputLayoutOtp.visibility = View.VISIBLE
                binding.textViewSubmit.visibility = View.VISIBLE
                binding.progressBarNext.visibility = View.VISIBLE
                binding.inputLayoutMobileNumber.isEnabled = false
                binding.textViewNext.alpha = .5f
                countryCode = "+1"+binding.editTextMobileNumber.text.toString()
                sendVerificationOtp(countryCode)
            }
        }

        binding.textViewSubmit.setOnClickListener {
            if (validOtp()){
                binding.progressBarSubmit.visibility = View.VISIBLE
                binding.textViewSubmit.alpha = .5f
                verifyOtp(code = binding.editTextOtp.text.toString())
            }
        }

        binding.textViewRegisterWithEmail.setOnClickListener {
            startActivity(Intent(App.context(), AuthEmailActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun validOtp(): Boolean {
        val otp = binding.editTextOtp.text.toString()
        if (TextUtils.isEmpty(otp)){
            binding.inputLayoutOtp.helperText = "Invalid OTP"
            return false
        }
        return true
    }

    private fun sendVerificationOtp(countryCode: String) {
        val callback = object  : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
                binding.inputLayoutOtp.visibility = View.VISIBLE
                binding.progressBarNext.visibility = View.GONE
                binding.textViewNext.visibility = View.GONE
                Utils.createToast(App.context(),"Otp sent")
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code = credential.smsCode
                if (code != null){
                    binding.editTextOtp.setText(code)
                    verifyOtp(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Utils.createToast(App.context(),"Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    Utils.createToast(App.context(),"The SMS quota for the project has been exceeded")
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    Utils.createToast(App.context(),"reCAPTCHA verification attempted with null Activity")
                }
            }
        }

        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(countryCode)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun verifyOtp(code: String) {
        val otp = binding.editTextOtp.text.toString()

        if (code == otp){
            val credential = PhoneAuthProvider.getCredential(verificationId,code)
            signInWithCredential(credential)
        }else{
            binding.progressBarSubmit.visibility = View.GONE
            binding.textViewSubmit.alpha = 1f
            Utils.createToast(App.context(),"Entered otp is invalid")
        }

    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val bundle = Bundle().apply {
                        putString("uid",firebaseAuth.currentUser?.uid.toString())
                        putString("verificationId", verificationId)
                    }
                    findNavController().navigate(R.id.addProfileFragment,bundle)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun validMobileNumber(): Boolean {
        mobileNumber = binding.editTextMobileNumber.text.toString()
        if (TextUtils.isEmpty(mobileNumber) && mobileNumber == ""){
            binding.inputLayoutMobileNumber.helperText = "Invalid Mobile Number"
            return false
        }
        return true
    }

    private fun showKeyboardOnFragmentStart() {
        editTextMobileNumber = requireView().findViewById(R.id.editTextMobileNumber)
        binding.editTextMobileNumber.requestFocus()
        val showKeyboard = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        showKeyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
