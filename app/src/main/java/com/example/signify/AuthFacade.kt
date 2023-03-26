package com.example.signify

import com.google.firebase.auth.FirebaseAuth

class AuthFacade {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val checkStatus = CheckStatus()

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
                        checkStatus.checkUserStatus(user.uid,
                            { userType ->
                                onSuccess(userType)
                            },
                            {
                                onFailure()
                            })
                    }
                } else {
                    onFailure()
                }
            }
    }
}