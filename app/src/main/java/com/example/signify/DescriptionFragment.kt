package com.example.signify

import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentDescriptionBinding
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class DescriptionFragment : Fragment() {
    private lateinit var binding: FragmentDescriptionBinding
    private lateinit var viewModel: DescriptionViewModel
    private val selected: MutableMap<String, Boolean> = mutableMapOf()
    private var price: Double = 0.0
    private var billboardId: String? = null
    private lateinit var repository: FirestoreRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DescriptionViewModel::class.java)
        repository = FirestoreRepository()

        var year = 2023
        lateinit var map: HashMap<String, Boolean>
        val gridLayout = binding.gridLayout

        val currentUser = FirebaseAuth.getInstance().currentUser
        val clientId = currentUser?.uid ?: ""
        billboardId = arguments?.getString("key")!!
        viewModel.billboardId = billboardId as String
        viewModel.getPrice(viewModel.billboardId).observe(viewLifecycleOwner) { p ->
            price=p
        }
        viewModel.getDescription(billboardId!!).observe(viewLifecycleOwner){
            binding.billboardName.text = getString(R.string.billboard_id, it.billboardId)
            binding.billboardPrice.text = "Price: ${it.price}$"
            binding.location.text = it.location
            binding.desSurface.text = it.surface
            binding.desSize.text = it.size
            binding.desType.text = it.type
        }
        binding.backButtonDescription.setOnClickListener {
            // get the FragmentManager and remove the current fragment from the back stack
            val fragmentManager = requireActivity().supportFragmentManager

            fragmentManager.popBackStack()

        }
        viewModel.getBillboardAvailability(viewModel.billboardId).observe(viewLifecycleOwner) { availabilityMap ->
            year = 2023
            map = availabilityMap
            setAvailabilityForYear(year, availabilityMap, gridLayout)
        }

        binding.order.setOnClickListener {
            repository.createOrder(clientId, billboardId!!, price, selected)
            repository.appendSelectedToNotAvailable(billboardId!!, selected)
            showSuccessDialog()
        }
        binding.right.setOnClickListener {
            year++
            binding.year.text = year.toString()
            setAvailabilityForYear(year, map, gridLayout)
        }
        binding.left.setOnClickListener {
            if(year >2023) {
                year--
                binding.year.text = year.toString()
                setAvailabilityForYear(year, map, gridLayout)
            }
        }
        return binding.root
    }
    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Success")
            .setMessage("Order placed.")
            .setPositiveButton("OK") { _, _ ->
                requireActivity().onBackPressed()
            }
            .create()

        dialog.show()
    }

    private fun setAvailabilityForYear(year: Int, availabilityMap: HashMap<String, Boolean>, gridLayout: GridLayout) {
        val color = ContextCompat.getColor(requireContext(), R.color.gray)
        val whitecolor = ContextCompat.getColor(requireContext(), R.color.white)
        val blackcolor = ContextCompat.getColor(requireContext(), R.color.black)

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is TextView) {
                val textViewId = child.resources.getResourceEntryName(child.id)
                val e = "$year$textViewId"
                val available = availabilityMap[e] ?: false

                if (available) {
                    child.setOnClickListener(null)
                    child.paintFlags = child.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    child.setTextColor(color)
                    child.setBackgroundResource(0)
                }
                else {
                    child.setPaintFlags(child.getPaintFlags() and Paint.STRIKE_THRU_TEXT_FLAG.inv())
                    if(selected["$year$textViewId"] == true){
                        child.setBackgroundResource(R.drawable.background_selected_month)
                        child.setTextColor(whitecolor)

                        child.setOnClickListener {
                            val e = "$year$textViewId"
                            val a = selected[e] ?: false
                            if(!a){
                                selected["$year$textViewId"] = true
                                child.setBackgroundResource(R.drawable.background_selected_month)
                                child.setTextColor(whitecolor)
                                binding.dates.text = selected.size.toString() + " months selected"
                                binding.billboardPrice.text = "$" + selected.size * price
                            }
                            if(a){
                                selected.remove("$year$textViewId")
                                child.setBackgroundResource(0)
                                child.setTextColor(blackcolor)
                                binding.dates.text = selected.size.toString() + " months selected"
                                binding.billboardPrice.text = "$" + selected.size * price
                            }
                        }
                    }
                    else {
                        child.setTextColor(blackcolor)
                        child.setBackgroundResource(0)
                        child.setOnClickListener {
                            val e = "$year$textViewId"
                            val a = selected[e] ?: false
                            if(!a){
                                selected["$year$textViewId"] = true
                                child.setBackgroundResource(R.drawable.background_selected_month)
                                child.setTextColor(whitecolor)
                                binding.dates.text = selected.size.toString() + " months selected"
                                binding.billboardPrice.text = "$" + selected.size * price
                            }
                            if(a){
                                selected.remove("$year$textViewId")
                                child.setBackgroundResource(0)
                                child.setTextColor(blackcolor)
                                binding.dates.text = selected.size.toString() + " months selected"
                                binding.billboardPrice.text = "$" + selected.size * price
                            }
                        }
                    }

                }
            }
        }
    }

}
