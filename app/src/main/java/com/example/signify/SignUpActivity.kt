package com.example.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    // Firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //START: [Firebase]
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val signUpEmail: EditText = findViewById(R.id.signup_email)
        val signUpPassword: EditText = findViewById(R.id.signup_password)
        val signUpButton: Button = findViewById(R.id.signup)

        signUpButton.setOnClickListener{
            val email = signUpEmail.text.toString()
            val password = signUpPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    signUpEmail.error = "Enter email address"
                }
                if(password.isEmpty()){
                    signUpPassword.error = "Enter password"
                }
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
            }else if(!email.matches(emailPattern.toRegex())){
                signUpEmail.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if(password.length < 6){
                signUpPassword.error = "Enter password more than 6 characters"
                Toast.makeText(this, "Enter password more than 6 characters", Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val databaseRef = database.reference.child("users").child(auth.currentUser!!.uid)
                        val users: Users = Users(email, auth.currentUser!!.uid)

                        databaseRef.setValue(users).addOnCompleteListener{
                            if(it.isSuccessful){
                                val intent = Intent(this, LogInActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        //END: [Firebase]


        //START:[Enter MainActivity] This code sets an OnClickListener on the TextView, so when it is clicked, it will start the SignUp
        val logInEntry = findViewById<TextView>(R.id.log_in_entry)
        logInEntry.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }
        //END:[Enter MainActivity]
    }
}