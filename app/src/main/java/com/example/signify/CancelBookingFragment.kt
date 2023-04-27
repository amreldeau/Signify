package com.example.signify


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentCancelBookingBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.util.concurrent.TimeUnit


class CancelBookingFragment : Fragment() {

    private lateinit var binding: FragmentCancelBookingBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelBookingBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val orderId = arguments?.getString("orderId")!!
        calculatePriceAfterPenalty(orderId)
        binding.continueBtn.setOnClickListener {
            val message = binding.message.editText?.text.toString()
            val refundPrice = binding.refund.text.toString().toDouble()
            createNewRequest(orderId, refundPrice, message)
        }

        return binding.root
    }
    private fun createNewRequest(orderId: String, refundPrice: Double, message: String) {
        val db = FirebaseFirestore.getInstance()
        val requestRef = db.collection("requests").document()

        val request = hashMapOf(
            "orderId" to orderId,
            "refund_price" to refundPrice,
            "message" to message,
            "type" to "Cancellation request"
        )

        requestRef.set(request)
            .addOnSuccessListener {
                // Success
            }
            .addOnFailureListener { e ->
                // Error
            }
    }

    private fun calculatePriceAfterPenalty(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document(orderId)

        orderRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val orderData = documentSnapshot.data
                val orderStatus = orderData?.get("order_status") as? Map<String, Timestamp>
                val lowestTimestamp = orderStatus?.values?.minOrNull()?.toDate()?.time ?: 0
                val originalPrice = orderData?.get("price") as? Double ?: 0.0
                binding.originalPrice.text = originalPrice.toString()
                val timePassedMillis = System.currentTimeMillis() - lowestTimestamp
                val hoursPassed = TimeUnit.MILLISECONDS.toHours(timePassedMillis)

                val refundPercentage = when {
                    hoursPassed < 24 -> 0.8
                    hoursPassed < 48 -> 0.5
                    else -> 0.1
                }

                val refundAmount = originalPrice * refundPercentage
                binding.refund.text = refundAmount.toString()
            } else {

            }
        }.addOnFailureListener { e ->

        }
    }
}