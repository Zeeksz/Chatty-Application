package com.example.chatty.RegistrationandLogin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatty.ModelClasses.User
import com.example.chatty.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        ///////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance()
        ////////////////////////////////////////////////////////
        signup_btn.setOnClickListener{
            signUpUser()
        }
        ////////////////////////////////////////////////////////
        val backbutton = findViewById<ImageView>(R.id.signup_back_btn)
        backbutton.setOnClickListener{
            finish()
        }

    }
 private fun signUpUser() {
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
    auth.createUserWithEmailAndPassword(email_et.text.toString(), password_et.text.toString())
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserToFirebaseDatabase()
                            intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
            }
            else {
                Toast.makeText(applicationContext,"Failed to sign-up", Toast.LENGTH_SHORT).show()
            }
        }
}
    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            username_et.text.toString(),
            email_et.text.toString(),
            "Hi! I'm Using Chatty",
            "Default",
            "Default"
        )
        ref.setValue(user)
    }
}