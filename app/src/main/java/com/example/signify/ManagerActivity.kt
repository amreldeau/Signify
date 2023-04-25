package com.example.signify

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.example.signify.databinding.ActivityManagerBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ManagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerBinding
    private val auth = Firebase.auth
    private val invoker = Invoker()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signout.setOnClickListener {
            val command = SignOutCommand(auth)
            invoker.executeCommand(command)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.orders.setOnClickListener {
            val ordersFragment = ManagerOrdersFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ordersFragment)
                .commit()
        }
    }

    // ...
}
