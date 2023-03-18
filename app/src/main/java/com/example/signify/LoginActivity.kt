package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.example.signify.R.layout.activity_log_in
import com.example.signify.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize data binding
        val binding: ActivityLogInBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        binding.lifecycleOwner = this // use this activity as the lifecycle owner for the binding

        //force app to always use the light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //START: [Firebase]
        auth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    binding.loginEmail.error = "Enter email address"
                }
                if(password.isEmpty()){
                    binding.loginPassword.error = "Enter password"
                }
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            }else if(!email.matches(emailPattern.toRegex())){
                binding.loginEmail.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                binding.loginPassword.error = "Enter password more than 6 characters"
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
    }
}