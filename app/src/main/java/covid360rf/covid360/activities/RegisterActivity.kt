package covid360rf.covid360.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import covid360rf.covid360.R
import covid360rf.covid360.databinding.ActivityRegisterBinding
import covid360rf.covid360.firebase.FireStoreClass
import covid360rf.covid360.model.User
import covid360rf.covid360.utils.start
import covid360rf.covid360.utils.toast
import java.util.concurrent.TimeUnit


class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeDuration: Long = 60000
    private var pauseOffset: Long = 0
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar(this, binding.toolbarSignUpActivity, getString(R.string.register))

        binding.tvResendCodeTimer.text = "00 : " + (timeDuration / 1000).toString()

        binding.cvSignUp.visibility = View.VISIBLE
        binding.cvVerify.visibility = View.GONE

        firebaseAuth = FirebaseAuth.getInstance()

        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneNumberAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                toast(e.message.toString())
                Log.e("Error", e.message.toString())
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)

                Log.e("Code", "OnCode Sent :: $verificationId")
                Log.e("Code", "OnCode Sent :: $token")
                mVerificationId = verificationId
                forceResendingToken = token

                //dismiss
                //hide phone layout and show code layout
                binding.cvSignUp.visibility = View.GONE
                binding.cvVerify.visibility = View.VISIBLE

                toast("Verification code sent successfully")
                //setUpActionBar(this@RegisterActivity, findViewById(R.id.toolbar), "Verification code")
            }

        }
        binding.btnRegister.setOnClickListener {
            if (validateForm(
                    binding.etName.text.toString().trim(),
                    binding.etPhoneNumber.text.toString().trim()
                )
            ) {
                showProgressDialog(getString(R.string.please_wait))
                val phone: String = "+91" + binding.etPhoneNumber.text.toString().trim()
                startPhoneNumberVerification(phone)
                binding.cvSignUp.visibility = View.GONE
                binding.cvVerify.visibility = View.VISIBLE
                binding.btnResendOtp.visibility = View.GONE

                startTimer(pauseOffset)
            }
        }
        binding.btnVerify.setOnClickListener {
            //input verification code
            val code = binding.etOtpCode.text.toString().trim()
            if (TextUtils.isEmpty(code)) {
                toast(getString(R.string.please_enter_code))
            } else {
                showProgressDialog(getString(R.string.please_wait))
                verifyPhoneNumberWithCode(mVerificationId!!, code)
            }
        }

        binding.btnResendOtp.setOnClickListener {
            if (validateForm(
                    binding.etName.text.toString().trim(),
                    binding.etPhoneNumber.text.toString().trim()
                )
            ) {
                val phone: String = "+91" + binding.etPhoneNumber.text.toString().trim()
                showProgressDialog(getString(R.string.please_wait))
                resendVerificationCode(phone, forceResendingToken)

                resetTimer()
                startTimer(pauseOffset)
            }
        }
    }

    private fun startPhoneNumberVerification(phone: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        hideProgressDialog()
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneNumberAuthCredential(credential)
        hideProgressDialog()
    }

    private fun signInWithPhoneNumberAuthCredential(credential: PhoneAuthCredential) {

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
//                val phone = firebaseAuth.currentUser!!.phoneNumber

                val phone = firebaseAuth.currentUser!!.phoneNumber

                val user  = User(
                    firebaseAuth.currentUser!!.uid,
                    binding.etName.text.toString().trim{ it<= ' '},
                    phone.toString()
                )
                FireStoreClass().registerUser(this,user)

                toast("Thank you for registering with us")
                start(MainActivity::class.java)
                finish()
            }.addOnFailureListener { e ->
                hideProgressDialog()
                toast(e.message.toString())
                Log.e("Error", e.message.toString())

            }
    }

    private fun resendVerificationCode(
        phone: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback!!)
            .setForceResendingToken(token!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        hideProgressDialog()
    }

    private fun validateForm(name: String, phone: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar(getString(R.string.please_enter_a_name))
                false
            }
            TextUtils.isEmpty(phone) || phone.length != 10 -> {
                showErrorSnackBar(getString(R.string.enter_10_digit_phone_number))
                false
            }
            else -> {
                true
            }
        }
    }

    private fun startTimer(pauseOffsetL: Long) {

        countDownTimer = object : CountDownTimer(
            timeDuration - pauseOffsetL, 1000
        ) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                pauseOffset = timeDuration - millisUntilFinished
                binding.tvResendCodeTimer.text = "00 : " +
                        if ((millisUntilFinished / 1000) < 10) "0" + (millisUntilFinished / 1000).toString() else (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                binding.btnResendOtp.visibility = View.VISIBLE
            }
        }.start()

    }

    private fun resetTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            binding.tvResendCodeTimer.text = (timeDuration / 1000).toString()
            countDownTimer = null
            pauseOffset = 0
        }
    }

    fun userRegisteredSuccess(){
        hideProgressDialog()
    }

}