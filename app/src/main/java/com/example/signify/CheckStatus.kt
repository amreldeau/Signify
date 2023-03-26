package com.example.signify

import com.google.firebase.firestore.FirebaseFirestore

class CheckStatus {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun checkUserStatus(userId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        db.collection("authorization")
            .document(userId)
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
}