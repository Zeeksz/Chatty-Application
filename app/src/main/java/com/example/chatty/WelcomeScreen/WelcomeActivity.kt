package com.example.chatty.WelcomeScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.chatty.R
import com.example.chatty.RegistrationandLogin.LoginActivity
import com.example.chatty.RegistrationandLogin.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val signinbutton = findViewById<Button>(R.id.signin_btn_welcomescreen)
        signinbutton.setOnClickListener{
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)}

        val signupbutton = findViewById<Button>(R.id.signup_btn_welcome)
        signupbutton.setOnClickListener{
            intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)}

    }
}
