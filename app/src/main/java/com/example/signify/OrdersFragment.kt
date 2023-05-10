package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.auth.FirebaseAuth

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val viewModel: OrdersViewModel by viewModels()
    private val adapter = OrdersAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        // Set the clientId to the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val clientId = currentUser?.uid ?: ""
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        viewModel.getOrdersForClient(clientId).observe(viewLifecycleOwner) { orders ->
            adapter.orders = orders
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }
}


