package com.example.signify


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentCancelBookingBinding
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

import java.util.concurrent.TimeUnit


class CancelBookingFragment : Fragment() {

    private lateinit var binding: FragmentCancelBookingBinding
    private val repository = FirestoreRepository()
    private lateinit var viewModel: CancelBookingViewModel
    var penalty = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCancelBookingBinding.inflate(inflater, container, false)

        val orderId = requireArguments().getString("orderId")!!
        binding.continueBtn.setOnClickListener {
            repository.updateOrderStatus(orderId, "Cancelled")

            showSuccessDialog()
        }
        viewModel = ViewModelProvider(this).get(CancelBookingViewModel::class.java)
        viewModel.getOrderDetails(orderId).observe(viewLifecycleOwner) { order ->
            binding.originalPrice.text = order.price.toString()
            calculatePenalty(order.orderDate)
            binding.refund.text = (order.price * penalty).toString()
        }
        binding.backButtonCancel.setOnClickListener {
            // get the FragmentManager and remove the current fragment from the back stack
            val fragmentManager = requireActivity().supportFragmentManager

            fragmentManager.popBackStack()

        }

        return binding.root
    }
    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Success")
            .setMessage("Order status updated successfully.")
            .setPositiveButton("OK") { _, _ ->
                requireActivity().onBackPressed()
            }
            .create()

        dialog.show()
    }
    fun calculatePenalty(orderDate: Date) {
        val currentDate = Date()
        val diffInMs = currentDate.time - orderDate.time
        val diffInHours = diffInMs / (1000 * 60 * 60)

        penalty = when {
            diffInHours < 24 -> 1.0
            diffInHours < 48 -> 0.5
            else -> 1.0
        }
    }
}