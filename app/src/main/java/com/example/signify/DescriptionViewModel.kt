package com.example.signify

import android.graphics.Paint
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestore

class DescriptionViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    var billboardId: String = ""
    var price: Double = 0.0
    val orderId = MutableLiveData("")

    fun createOrder(clientId: String, billboardId: String, price: Double, selected: MutableMap<String, Boolean>, orderId: String) {
        repository.createOrder(clientId, billboardId, price, selected, orderId)
    }
    fun generateID(): LiveData<String>{
        return repository.generateID()
    }
    fun getUserName(uid: String) : LiveData<String>{
        return repository.getUserName(uid)
    }
    fun createPayment(orderId: String, amount: Double){
        repository.createPayment(orderId, amount)
    }
    fun updateOrderStatus(orderId: String, status: String){
        repository.updateOrderStatus(orderId, status)
    }
    fun appendSelectedToNotAvailable(billboardId: String, selected: MutableMap<String, Boolean>){
        repository.appendSelectedToNotAvailable(billboardId, selected)
    }
    fun getBillboardAvailability(billboardId: String): LiveData<HashMap<String, Boolean>> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val availability = MutableLiveData<HashMap<String, Boolean>>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            val billboardData = billboardSnapshot.data ?: return@addOnSuccessListener
            availability.value = ((billboardData["NotAvailable"] as? HashMap<String, Boolean> ?: emptyMap()) as HashMap<String, Boolean>?)
        }

        return availability
    }
    fun getDescription(billboardId: String): LiveData<Description> {
        return repository.getDescription(billboardId)
    }

    fun getPrice(billboardId: String): LiveData<Double> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val price = MutableLiveData<Double>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            price.value = billboardSnapshot.getDouble("price")
        }

        return price
    }

}
