package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.firestore.FieldPath
import java.util.*

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val clientId = auth.currentUser?.uid
        if (clientId != null) {
            firestore.collection("authorization")
                .document(clientId)
                .get()
                .addOnSuccessListener { document ->
                    val orderIds = document["orders"] as List<String>
                    getOrderDetails(orderIds)
                }
                .addOnFailureListener { exception ->
                    // handle error
                }
        }

        return binding.root
    }
    private fun getOrderDetails(orderIds: List<String>) {
        val ordersRef = firestore.collection("orders")
        val query = ordersRef.whereIn(FieldPath.documentId(), orderIds)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val orders = mutableListOf<Order>()
                for (document in querySnapshot.documents) {
                    val billboardId = document.getString("billboard_id") ?: ""
                    val orderStatus = document.getString("order_status") ?: ""
                    val date = document.getString("date") ?: ""
                    val order = Order(billboardId, orderStatus, date)
                    orders.add(order)
                }
                displayOrders(orders)
            }
            .addOnFailureListener { exception ->
                // handle error
            }
    }
    private fun displayOrders(orders: List<Order>) {
        val adapter = OrdersAdapter(orders)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }
}