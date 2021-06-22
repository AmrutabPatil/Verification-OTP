package com.example.otpverification

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class VerifyotpActivity : AppCompatActivity() {
    private lateinit var inputCode1: EditText
    private lateinit var inputCode2: EditText
    private lateinit var inputCode3: EditText
    private lateinit var inputCode4: EditText
    private  lateinit var inputCode5: EditText
    private lateinit  var inputCode6: EditText
    private  lateinit var verificationId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verifyotp)
        val textMobile = findViewById<TextView>(R.id.textMobile)
        textMobile.text = String.format(
            "+91-%s", intent.getStringExtra("mobile")
        )
        inputCode1 = findViewById(R.id.inputCode1)
        inputCode2 = findViewById(R.id.inputCode2)
        inputCode3 = findViewById(R.id.inputCode3)
        inputCode4 = findViewById(R.id.inputCode4)
        inputCode5 = findViewById(R.id.inputCode5)
        inputCode6 = findViewById(R.id.inputCode6)
        setupOTPInputs()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btn_verify = findViewById<Button>(R.id.btn_verify)
        verificationId = intent.getStringExtra("verificationId").toString()
        btn_verify.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((!(!inputCode1.text.toString().trim { it <= ' ' }.isEmpty() && !inputCode2.text.toString().trim { it <= ' ' }.isEmpty() && !inputCode3.text.toString().trim { it <= ' ' }.isEmpty() && !inputCode4.text.toString().trim { it <= ' ' }.isEmpty() && !inputCode5.text.toString().trim { it <= ' ' }.isEmpty() && !inputCode6.text.toString().trim { it <= ' ' }.isEmpty()))
                ) {
                    Toast.makeText(
                        this@VerifyotpActivity,
                        "Please enter valid code",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                val code = (inputCode1.text.toString() +
                        inputCode2.text.toString() +
                        inputCode3.text.toString() +
                        inputCode4.text.toString() +
                        inputCode5.text.toString() +
                        inputCode6.text.toString())
                progressBar.visibility = View.VISIBLE
                btn_verify.visibility = View.INVISIBLE
                val phoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
                )
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener { task ->
                        progressBar.visibility = View.GONE
                        btn_verify.visibility = View.VISIBLE
                        if (task.isSuccessful) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@VerifyotpActivity,
                                "The verification code entered was invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        })
        findViewById<View>(R.id.textResendOTP).setOnClickListener {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + intent.getStringExtra("mobile"),
                60,
                TimeUnit.SECONDS, this@VerifyotpActivity,
                object : OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@VerifyotpActivity, e.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onCodeSent(
                        newVerificationId: String,
                        forceResendingToken: ForceResendingToken
                    ) {
                        super.onCodeSent((verificationId), forceResendingToken)
                        verificationId = newVerificationId
                        Toast.makeText(this@VerifyotpActivity, "OTP Sent", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }
    }

    private fun setupOTPInputs() {
        inputCode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.toString().trim { it <= ' ' }.isEmpty()) {
                    inputCode2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}