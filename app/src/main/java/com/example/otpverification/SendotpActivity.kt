package com.example.otpverification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.otpverification.R
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import android.content.Intent
import android.view.View
import android.widget.Button
import com.example.otpverification.VerifyotpActivity
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class SendotpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sendotp)
        val inputMobile = findViewById<EditText>(R.id.inputMobile)
        val get_otp = findViewById<Button>(R.id.get_otp)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        get_otp.setOnClickListener(View.OnClickListener {
            if (inputMobile.text.toString().trim { it <= ' ' }.isEmpty()) {
                Toast.makeText(this@SendotpActivity, "enter mobile", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            progressBar.visibility = View.VISIBLE
            get_otp.visibility = View.VISIBLE
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + inputMobile.text.toString(),
                60,
                TimeUnit.SECONDS, this@SendotpActivity,
                object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                        progressBar.visibility = View.GONE
                        get_otp.visibility = View.VISIBLE
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        progressBar.visibility = View.GONE
                        get_otp.visibility = View.VISIBLE
                        Toast.makeText(this@SendotpActivity, e.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        forceResendingToken: ForceResendingToken
                    ) {
                        super.onCodeSent(verificationId, forceResendingToken)
                        progressBar.visibility = View.GONE
                        get_otp.visibility = View.VISIBLE
                        val intent = Intent(applicationContext, VerifyotpActivity::class.java)
                        intent.putExtra("mobile", inputMobile.text.toString())
                        intent.putExtra("verificationId", verificationId)
                        startActivity(intent)
                    }
                }
            )
        })
    }
}