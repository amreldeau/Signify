package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signify.databinding.FragmentChangeBookingBinding
import com.example.signify.databinding.FragmentOrderDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChangeBookingFragment : Fragment() {

    private lateinit var binding: FragmentChangeBookingBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeBookingBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        val db = Firebase.firestore
        auth = FirebaseAuth.getInstance()
        val orderId = arguments?.getString("orderId")!!
        binding.continueBtn.setOnClickListener {
            val orderRef = db.collection("orders").document(orderId)

            val newStatus = "2023-04-23"
            orderRef.update("order_status.$newStatus", "Changes pending")
                .addOnSuccessListener {

                }
                .addOnFailureListener { e ->

                }
        }

        return binding.root
    }
}