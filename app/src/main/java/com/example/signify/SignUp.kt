package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //START:[Enter Sign Up page] This code sets an OnClickListener on the TextView, so when it is clicked, it will start the SignUp
        val logInEntry = findViewById<TextView>(R.id.log_in_entry)
        logInEntry.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        //END: Enter MainActivity
    }
}