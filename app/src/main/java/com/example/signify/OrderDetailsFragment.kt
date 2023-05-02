
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
        binding.changeOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            val fragment = ChangeBookingFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }
}
