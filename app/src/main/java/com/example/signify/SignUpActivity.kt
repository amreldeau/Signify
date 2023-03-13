package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //START:[Enter MainActivity] This code sets an OnClickListener on the TextView, so when it is clicked, it will start the SignUp
        val logInEntry = findViewById<TextView>(R.id.log_in_entry)
        logInEntry.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        //END:[Enter MainActivity]
    }
}