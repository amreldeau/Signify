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
        query.get().addOnSuccessListener { querySnapshot ->
                val orders = mutableListOf<Order>()
                for (document in querySnapshot.documents) {
                    val billboardId = document.getString("billboard_id") ?: ""
                    val ordersStatusMap = document.get("order_status") as? HashMap<String, Any>
                    var newestDate: Date? = null
                    var newestStatus: String? = null

                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    for ((dateStr, status) in ordersStatusMap!!) {
                        val date = dateFormatter.parse(dateStr.toString())
                        if (newestDate == null || date.after(newestDate)) {
                            newestDate = date
                            newestStatus = status.toString()
                        }
                    }
                    // Get the location field from the billboards collection
                    val billboardRef = firestore.collection("billboards").document(billboardId)
                    billboardRef.get().addOnSuccessListener { billboardSnapshot ->
                        val location = billboardSnapshot.getString("location") ?: ""
                        val orderId = document.id
                        val order = Order(billboardId, newestStatus!!, location, orderId)
                        orders.add(order)
                        displayOrders(orders)
                    }
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