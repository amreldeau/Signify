package com.example.signify


import com.google.firebase.auth.FirebaseAuth

class SignOutCommand(private val firebaseAuth: FirebaseAuth) : Command{
    override fun execute() {
        firebaseAuth.signOut()
    }
    override fun undo() {
    }
}