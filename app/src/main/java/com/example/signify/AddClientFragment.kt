package com.example.signify

import AddClientViewModel
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentAddClientBinding
import com.google.firebase.auth.FirebaseAuth

class AddClientFragment : Fragment() {

    private lateinit var binding: FragmentAddClientBinding
    private lateinit var viewModel: AddClientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddClientBinding.inflate(inflater, container, false)
        val auth = FirebaseAuth.getInstance()
        val currentUserUid = auth.currentUser?.uid
        viewModel = ViewModelProvider(this).get(AddClientViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val name = binding.etRegisterName.text.toString()
            viewModel.addNewClient(email, password, currentUserUid!!, name)
            showSuccessDialog()
        }
        binding.backButtonRegister.setOnClickListener {
            // get the FragmentManager and remove the current fragment from the back stack
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        return binding.root
    }
    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Success")
            .setMessage("Client registered.")
            .setPositiveButton("OK") { _, _ ->
                requireActivity().onBackPressed()
            }
            .create()

        dialog.show()
    }
}
