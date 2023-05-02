package com.example.signify


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentCancelBookingBinding
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import java.util.concurrent.TimeUnit


class CancelBookingFragment : Fragment() {

    private lateinit var binding: FragmentCancelBookingBinding
    private val repository = FirestoreRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelBookingBinding.inflate(inflater, container, false)

        val orderId = requireArguments().getString("orderId")!!
        binding.continueBtn.setOnClickListener {
            repository.updateOrderStatus(orderId, "Cancelled")
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}