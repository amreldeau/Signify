package com.example.signify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentAccountBinding
import com.example.signify.databinding.FragmentAddClientBinding
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        val auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid
        viewModel = ViewModelProvider(this)[AccountViewModel::class.java]
        binding.signOutButton.setOnClickListener {
            viewModel.signOut()
            // Redirect user to login screen
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        viewModel.getUserName(currentUserUid!!).observe(viewLifecycleOwner) { name ->
            binding.userName.text = name
        }
        return binding.root
    }
}