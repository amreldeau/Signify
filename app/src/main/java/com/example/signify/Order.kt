package com.example.signify

import java.util.*

data class Order(
    val orderId: String,
    val billboardId: String,
    val clientId: String,
    val occupied: Map<String, Boolean>,
    val orderDate: Date,
    val status: String,
    val totalCost: Double
)
