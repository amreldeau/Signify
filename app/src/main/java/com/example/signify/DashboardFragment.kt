package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentClientsBinding
import com.example.signify.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.requests.setOnClickListener {
            val fragment = RequestListFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.clients.setOnClickListener {
            val fragment = ClientsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }
}