package com.example.signify

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Request(
    val requestID: String,
    val clientID: DocumentReference,
    val clientName: String,
    val date: Timestamp,
    val orderID: DocumentReference,
    val payoutDifference: Double,
    val requestedChanges: Map<String, Any>,
    val responsableManagerID: String,
    val status: String,
    val type: String
)