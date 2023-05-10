package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore

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
    fun getManagerName(currentManagerUid: String): LiveData<String> {
        return repository.getManagerName(currentManagerUid)
    }
}