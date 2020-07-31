package com.example.chatty.RegistrationandLogin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatty.DashboardScreen.Dashboardactivity
import com.example.chatty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ///////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance()
        ///////////////////////////////////////////////////////
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        /////////////////////////////////////////////////////////
        val signinButton = findViewById<Button>(R.id.signin_btn)
        signinButton.setOnClickListener{
            dologin()
        }
        val backbutton = findViewById<ImageView>(R.id.signin_back_btn)
        backbutton.setOnClickListener{
            finish()
        }

        val forgetPasswordButton = findViewById<Button>(R.id.forgetPassword)
        forgetPasswordButton.setOnClickListener {
            sendMailtoResetPassword()
        }
    }

    private fun sendMailtoResetPassword() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email_et.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"Email For Password Reset is Sent Successfully",Toast.LENGTH_LONG).show()
                }
            }
    }

    ///////////////////////////////////////////////////////////
    private fun dologin() {
        if (email_et.text.toString().isEmpty()) {
            email_et.error = "Please enter email"
            email_et.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_et.text.toString()).matches()) {
            email_et.error = "Please enter valid email"
            email_et.requestFocus()
            return
        }

        if (password_et.text.toString().isEmpty()) {
            password_et.error = "Please enter password"
            password_et.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(email_et.text.toString(), password_et.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {

                    updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if(currentUser.isEmailVerified) {
                intent = Intent(this, Dashboardactivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(
                    baseContext, "Please verify your email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                baseContext, "Login failed.",
                Toast.LENGTH_SHORT
            ).show()
            password_et.text.clear()
        }
    }

}
