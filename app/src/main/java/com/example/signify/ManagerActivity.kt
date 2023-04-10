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
        binding.add.setOnClickListener {
// Create a new instance of the MyFragment fragment
            val myFragment = AddClientFragment()

            // Use the FragmentManager to begin a new transaction
            val transaction = supportFragmentManager.beginTransaction()

            // Add the new fragment to the back stack
            transaction.addToBackStack(null)

            // Add the new fragment to the container view
            transaction.add(R.id.fragment_container, myFragment)

            // Commit the transaction
            transaction.commit()
        }
    }

    // ...
}
