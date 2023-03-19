package com.example.signify

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddClientViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun addNewClient(email: String, password: String, managerUid: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // The user account was created successfully
                    val userId = auth.currentUser?.uid
                    // Next, write the UID of the new user to Firestore
                    if (userId != null) {
                        val clientsCollection = db.collection("authentication").document(managerUid)
                            .collection("clients")
                        clientsCollection.document(userId).set(hashMapOf("uid" to userId))
                            .addOnSuccessListener {
                                // The UID document was created successfully
                                Log.d(TAG, "UID document created for new client with ID: $userId")
                            }
                            .addOnFailureListener { e ->
                                // There was an error creating the UID document
                                Log.w(TAG, "Error creating UID document for client", e)
                            }
                    }
                } else {
                    // There was an error creating the user account
                    Log.w(TAG, "Error creating user account", task.exception)
                }
            }
    }
}
