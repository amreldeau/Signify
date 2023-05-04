package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.signify.databinding.FragmentOrderDetailsBinding
import com.example.signify.databinding.FragmentRequestListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RequestListFragment : Fragment() {
    private lateinit var binding: FragmentRequestListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: RequestListViewModel
    private lateinit var adapter: RequestListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestListBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Initialize the adapter with an empty list
        adapter = RequestListAdapter(emptyList())

        // Set up RecyclerView with LinearLayoutManager and adapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        viewModel = ViewModelProvider(this).get(RequestListViewModel::class.java)
        viewModel.getRequestsByManagerId(auth.currentUser!!.uid).observe(viewLifecycleOwner) { requests ->
            displayOrders(requests)
        }

        return binding.root
    }

    private fun displayOrders(requests: List<Request>) {
        val adapter = RequestListAdapter(requests)
        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(context)
    }
}
