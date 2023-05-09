package com.example.signify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentClientsBinding
import com.example.signify.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: DashboardViewModel
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
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        binding.signout.setOnClickListener {
            viewModel.signOut()
            // Redirect user to login screen
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.clients.setOnClickListener {
            val fragment = ClientsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        viewModel.getTotalSales(auth.currentUser!!.uid).observe(viewLifecycleOwner){
            binding.totalSalesValue.text = getString(R.string.price, it)
        }
        viewModel.getUserName(auth.currentUser!!.uid).observe(viewLifecycleOwner){
            binding.name.text = it
        }

        return binding.root
    }
}