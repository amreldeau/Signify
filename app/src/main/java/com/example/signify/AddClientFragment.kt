package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentAddClientBinding

class AddClientFragment : Fragment() {

    private lateinit var binding: FragmentAddClientBinding
    private lateinit var viewModel: AddClientViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddClientBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AddClientViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val managerUid = "8qj7Z0umSLdgY8vJBXqArEjyPZV2" // replace with the actual manager UID

            viewModel.addNewClient(email, password, managerUid)
        }

        return binding.root
    }
}
