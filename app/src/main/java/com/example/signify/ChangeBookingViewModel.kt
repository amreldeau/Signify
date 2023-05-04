package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class ChangeBookingViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    fun getOrderById(orderId: String): LiveData<Order> {
        return repository.getOrderById(orderId)
    }
    fun getBillboardAvailability(billboardId: String): LiveData<HashMap<String, Boolean>> {
        return repository.getBillboardAvailability(billboardId)
    }
    fun getPrice(billboardId: String): LiveData<Double> {
        return repository.getPrice(billboardId)
    }
    fun placeRequest(clientId: String, orderId: String, payoutDifference: Double, selected: MutableMap<String, Boolean>, type: String) {
        repository.addRequest(clientId, orderId, payoutDifference, selected, type)
    }
}