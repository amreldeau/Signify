package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    // Firebase variables
    private lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //force app to always use the light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        //START: [Firebase]
        auth = FirebaseAuth.getInstance()

        val logInEmail: EditText = findViewById(R.id.login_email)
        val logInPassword: EditText = findViewById(R.id.login_password)
        val logInButton: Button = findViewById(R.id.login)

        logInButton.setOnClickListener{
            val email = logInEmail.text.toString()
            val password = logInPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    logInEmail.error = "Enter email address"
                }
                if(password.isEmpty()){
                    logInPassword.error = "Enter password"
                }
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            }else if(!email.matches(emailPattern.toRegex())){
                logInEmail.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                logInPassword.error = "Enter password more than 6 characters"
                Toast.makeText(this, "Enter password more than 6 characters", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //END: [Firebase]


        //START:[Enter Sign Up page] This code sets an OnClickListener on the TextView, so when it is clicked, it will start the SignUp
        val signUpEntry = findViewById<TextView>(R.id.sign_up_entry)
        signUpEntry.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        //END:[Enter Sign Up page]
    }
}