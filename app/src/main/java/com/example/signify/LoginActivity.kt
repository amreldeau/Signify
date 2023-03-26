package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        val authFacade = AuthFacade()

        binding.login.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            authFacade.signInWithEmailAndPassword(
                email,
                password,
                { userType ->
                    if (userType == "manager") {
                        startActivity(Intent(this, ManagerActivity::class.java))
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                },
                { Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show() }
            )
        }
    }
}