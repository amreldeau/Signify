package com.example.signify

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import java.text.SimpleDateFormat
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
        val clientId = auth.currentUser!!.uid
        if (clientId != null) {
            getOrderIds(clientId, { orderIds ->
                getOrderDetails(orderIds)
            }, {
                Log.d(TAG, "No orders found for client $clientId")
            })
        }
        return binding.root
    }
    private fun getOrderIds(clientId: String, onSuccess: (List<String>) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val authorizationRef = db.collection("authorization").document(clientId)

        authorizationRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val authorizationData = documentSnapshot.data
                val orderIds = authorizationData?.get("orders") as? List<String>

                if (orderIds != null) {
                    onSuccess(orderIds)
                } else {
                    onFailure()
                }
            } else {
                onFailure()
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting orderIds", e)
            onFailure()
        }
    }

    private fun getOrderDetails(orderIds: List<String>) {
        val db = FirebaseFirestore.getInstance()

        val orders = mutableListOf<Order>()

        // Loop through each order ID and retrieve the order details
        orderIds.forEach { orderId ->
            val orderRef = db.collection("orders").document(orderId)
            orderRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val orderData = documentSnapshot.data
                    val billboardId = orderData?.get("billboard_id").toString()
                    val location = orderData?.get("location").toString()

                    // Get the order status by sorting the order_status map by the timestamp values
                    val orderStatus = orderData?.get("order_status") as? Map<String, Timestamp>
                    val latestTimestamp = orderStatus?.values?.sortedDescending()?.firstOrNull()
                    val status = orderStatus?.entries?.firstOrNull { it.value == latestTimestamp }?.key ?: ""

                    val order = Order(
                        billboardId = billboardId ?: "",
                        status = status,
                        location = location ?: "",
                        orderId = orderId
                    )

                    // Add the order to the list
                    orders.add(order)

                    // Display the orders after retrieving all the order details
                    if (orders.size == orderIds.size) {
                        displayOrders(orders)
                    }

                } else {
                    Log.d(TAG, "Order not found")
                }
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error getting order information", e)
            }
        }
    }



    private fun displayOrders(orders: List<Order>) {
        val adapter = OrdersAdapter(orders)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }
}