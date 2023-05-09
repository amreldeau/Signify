package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class DashboardViewModel: ViewModel() {
    private val repository = FirestoreRepository()
    fun signOut() {
        repository.signOut()
    }
    fun getUserName(currentUserUid: String): LiveData<String> {
        return repository.getUserName(currentUserUid)
    }
    fun getTotalSales(uid: String) : LiveData<Double>{
        return repository.getTotalSales(uid)
    }
}