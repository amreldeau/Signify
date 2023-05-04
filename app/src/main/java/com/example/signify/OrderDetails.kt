package com.example.signify

import java.util.*

data class OrderDetails(
    val billboardId: String,
    val price: Double,
    val location: String,
    val occupied: Map<String, Boolean>,
    val status: String,
    val orderDate: Date
)
