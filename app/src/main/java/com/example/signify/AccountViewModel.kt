package com.example.signify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class AccountViewModel: ViewModel() {
    private val repository = FirestoreRepository()
    fun signOut() {
        repository.signOut()
    }
    fun getUserName(currentUserUid: String): LiveData<String> {
        return repository.getUserName(currentUserUid)
    }
}