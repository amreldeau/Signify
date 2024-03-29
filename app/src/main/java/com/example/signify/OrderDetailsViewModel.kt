package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class OrderDetailsViewModel : ViewModel() {
    //var billboardId: String = ""
    private val repository = FirestoreRepository()
    fun getOrderDetails(orderId: String): LiveData<OrderDetails>  {
        return repository.getOrderDetails(orderId)
    }
//    fun getDescription(billboardId: String): LiveData<Description> {
//        return repository.getDescription(billboardId)
//    }
}