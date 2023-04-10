package com.example.signify

interface PaymentStrategy {
    fun pay(amount: Double): Boolean
}