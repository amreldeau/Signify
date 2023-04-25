package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentChangeBookingBinding
import com.example.signify.databinding.FragmentManageOrderBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ManageOrderFragment : Fragment() {

    private lateinit var binding: FragmentManageOrderBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    // Define a list to hold the pending orders
    private val pendingOrders = mutableListOf<PendingOrder>()

    // Define a Firestore database reference
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageOrderBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val orderId = arguments?.getString("orderId")!!
        binding.accept.setOnClickListener {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val db = FirebaseFirestore.getInstance()
            val orderStatus = hashMapOf<String, Any>(
                currentDate to "Confirmed"
            )

            db.collection("orders")
                .document(orderId)
                .update("order_status", orderStatus)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
            binding.status.text = "Respond Confirmed"
        }
        binding.decline.setOnClickListener {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val db = FirebaseFirestore.getInstance()
            val orderStatus = hashMapOf<String, Any>(
                currentDate to "Declined"
            )

            db.collection("orders")
                .document(orderId)
                .update("order_status", orderStatus)
                .addOnSuccessListener {
                    // Update successful
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
            binding.status.text = "Respond Declined"
        }
        return binding.root
    }

}
