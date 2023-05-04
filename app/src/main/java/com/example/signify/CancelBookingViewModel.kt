package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class CancelBookingViewModel: ViewModel() {
    private val repository = FirestoreRepository()

    fun getOrderDetails(orderId: String): LiveData<OrderDetails> {
        return repository.getOrderDetails(orderId)
    }

}