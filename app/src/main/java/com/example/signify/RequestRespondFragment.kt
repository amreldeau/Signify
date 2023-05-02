package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentRequestRespondBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class RequestRespondFragment : Fragment() {
    private lateinit var binding: FragmentRequestRespondBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: RequestRespondViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestRespondBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val requestId = arguments?.getString("requestId")!!
        viewModel = ViewModelProvider(this)[RequestRespondViewModel::class.java]
        viewModel.getRequestById(requestId).observe(viewLifecycleOwner) { request ->
            binding.name.text = request.clientName
            binding.payFull.text = getString(R.string.pay_full, request.payoutDifference)

        }

        return binding.root
    }
}
