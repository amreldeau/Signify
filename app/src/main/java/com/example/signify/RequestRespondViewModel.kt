package com.example.signify

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository

class RequestRespondViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    fun getRequestById(requestId: String): LiveData<Request> {
        return repository.getRequestById(requestId)
    }
}