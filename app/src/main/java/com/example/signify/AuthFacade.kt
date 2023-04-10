package com.example.signify

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthFacade {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        db.collection("authorization")
                            .document(user.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val status = document.getString("status")
                                if (status == "manager") {
                                    onSuccess("manager")
                                } else {
                                    onSuccess("user")
                                }
                            }
                            .addOnFailureListener {
                                onFailure()
                            }
                    }
                } else {
                    onFailure()
                }
            }
    }
}