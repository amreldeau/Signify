package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentAddClientBinding
import com.example.signify.databinding.FragmentCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.util.*

class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        // In one fragment, allow the user to select a basic billboard product and save it to the Singleton
        val order = OrderSingleton.getInstance().order
        val items = listOf("Card", "PayPal")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.textField.setAdapter(adapter)
        binding.textField.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            // Respond to list popup window item click.
            if(position == 0){
                order?.setPaymentStrategy(CardPayment("", "", Date(345356)))
            }
            if(position == 1){
                order?.setPaymentStrategy(PayPal("", "", Date(345356)))
            }
        }
        binding.pay.setOnClickListener {
            order?.executePaymentStrategy()
        }
        binding.orderID.text = "Order ID " +order?.getID().toString()

        return binding.root
    }
}