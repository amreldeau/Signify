
package com.example.signify

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.FragmentOrderDetailsBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val orderId = arguments?.getString("orderId")!!
        setOrderInformation(orderId)

        binding.cancelOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            val fragment = CancelBookingFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }


    private fun setOrderInformation(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document(orderId)

        orderRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val orderData = documentSnapshot.data
                val billboardId = orderData?.get("billboard_id").toString()
                val orderStatus = orderData?.get("order_status") as Map<String, Any>
                val latestStatusKey = orderStatus.entries
                    .sortedByDescending { (_, value) -> value as Timestamp }
                    .firstOrNull()?.key
                val latestStatus = latestStatusKey ?: "Unknown"

                binding.status.text = latestStatus

                // Check if the latest status is "Cancelled"
                if (latestStatus != "Cancelled") {
                    // Hide the order manager view
                    binding.orderManager.visibility = View.VISIBLE
                }
                // Set the text of the billboard name
                binding.billboardName.text = billboardId

                // Get the billboard location using the billboardId
                getBillboardLocation(billboardId,
                    onSuccess = { location ->
                        // Set the text of the billboard location
                        binding.location.text = location
                    },
                    onFailure = {
                        Log.d(TAG, "Billboard not found")
                    }
                )

                // Do something with the billboardId
                Log.d(TAG, "Billboard ID: $billboardId")
            } else {
                Log.d(TAG, "Order not found")
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting order information", e)
        }
    }
    private fun getBillboardLocation(billboardId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val billboardRef = db.collection("billboards").document(billboardId)

        billboardRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val billboardData = documentSnapshot.data
                val location = billboardData?.get("location").toString()

                // Call the onSuccess callback function and pass the location value
                onSuccess(location)
            } else {
                // Call the onFailure callback function if the document doesn't exist
                onFailure()
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting billboard information", e)
            // Call the onFailure callback function in case of an error
            onFailure()
        }
    }

}
