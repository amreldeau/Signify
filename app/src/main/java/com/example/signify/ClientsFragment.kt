package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.signify.databinding.FragmentClientsBinding
import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ClientsFragment : Fragment() {

    private lateinit var binding: FragmentClientsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClientsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.add.setOnClickListener{
            val fragment = AddClientFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.backButtonClients.setOnClickListener {
            // get the FragmentManager and remove the current fragment from the back stack
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        return binding.root
    }
}