package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var viewModel: OrderDetailsViewModel
    //private var billboardId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(OrderDetailsViewModel::class.java)

        val orderId = arguments?.getString("orderId")!!
//        billboardId = arguments?.getString("key")!!
//        viewModel.billboardId = billboardId as String

        viewModel.getOrderDetails(orderId).observe(viewLifecycleOwner) { orderDetails ->
            // Update the UI with the order details
            binding.more.text = "Price: ${orderDetails.price}"
            binding.status.text = orderDetails.status
            binding.billboardName.text = getString(R.string.billboard_id, orderDetails.billboardId)
            binding.location.text = "Billboard location: ${orderDetails.location}"
            binding.date1.text = "Occupied: ${orderDetails.occupied}"
            checkStatus(orderDetails.status)
        }
//        viewModel.getDescription(billboardId!!).observe(viewLifecycleOwner){
//            binding.orderSurface.text = it.surface
//            binding.orderSize.text = it.size
//            binding.orderType.text = it.type
//        }
        binding.backButtonDetails.setOnClickListener {
            // get the FragmentManager and remove the current fragment from the back stack
            val fragmentManager = requireActivity().supportFragmentManager

            fragmentManager.popBackStack()

        }

        binding.cancelOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            val fragment = CancelBookingFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.changeOrder.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            val fragment = ChangeBookingFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }

    fun checkStatus(status: String) {
        if (status.equals("Cancelled")) binding.orderManager.visibility = View.GONE
    }
}
