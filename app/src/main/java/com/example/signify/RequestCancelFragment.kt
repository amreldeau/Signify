package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentRequestCancelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class RequestCancelFragment : Fragment() {
    private lateinit var binding: FragmentRequestCancelBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestCancelBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val requestId = arguments?.getString("requestId")!!
        getRequestDetails(requestId)
        binding.accept.setOnClickListener {
            val orderId = binding.orderId.text.toString()
            updateOrderStatus(orderId)
            deleteRequest(requestId)
        }
        binding.decline.setOnClickListener {
            deleteRequest(requestId)
        }

        return binding.root
    }
    private fun deleteRequest(requestId: String) {
        firestore.collection("requests").document(requestId).delete()
            .addOnSuccessListener {
                // Document was successfully deleted
                // TODO: Add any necessary UI updates or callbacks here
            }.addOnFailureListener { e ->
                // Deletion failed
                // TODO: Handle the failure here
            }
    }
    private fun getRequestDetails(requestId: String) {
        firestore.collection("requests").document(requestId).get()
            .addOnSuccessListener { document ->
                val message = document.getString("message") ?: ""
                val refundPrice = document.getDouble("refund_price") ?: 0.0
                val orderId = document.getString("orderId") ?: ""
                binding.message.text = message
                binding.refund.text = refundPrice.toString()
                binding.orderId.text = orderId
            }
    }
    private fun updateOrderStatus(orderId: String) {
        val orderRef = firestore.collection("orders").document(orderId)

        // Get the current order status map
        orderRef.get().addOnSuccessListener { documentSnapshot ->
            val orderStatus = documentSnapshot.get("order_status") as? Map<String, Any> ?: emptyMap()

            // Add the new cancelled field to the order status map with the current timestamp
            val updatedOrderStatus = orderStatus.toMutableMap()
            updatedOrderStatus["Cancelled"] = FieldValue.serverTimestamp()

            // Update the order document with the new order status map
            orderRef.update("order_status", updatedOrderStatus).addOnSuccessListener {
                // Update was successful
                // TODO: Add any necessary UI updates or callbacks here
            }.addOnFailureListener { e ->
                // Update failed
                // TODO: Handle the failure here
            }
        }
    }


}
