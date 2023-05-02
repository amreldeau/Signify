package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class OrdersViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    fun getOrdersForClient(clientId: String): LiveData<List<Order>> {
        return repository.getOrdersForClient(clientId)
    }
}