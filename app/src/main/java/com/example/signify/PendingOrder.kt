package com.example.signify

import com.google.firebase.Timestamp

data class PendingOrder(
    val clientName: String,
    val billboardAddress: String,
    val orderId: String
)