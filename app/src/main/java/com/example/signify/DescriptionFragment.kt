package com.example.signify

import android.content.ContentValues.TAG
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.signify.databinding.FragmentDescriptionBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.util.*

class DescriptionFragment : Fragment() {
    private lateinit var binding: FragmentDescriptionBinding
    private lateinit var viewModel: DescriptionViewModel
    private val selected: MutableMap<String, Boolean> = mutableMapOf()
    var price: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DescriptionViewModel::class.java)
        var year = 2023
        lateinit var map: HashMap<String, Boolean>
        val gridLayout = binding.gridLayout

        val billboard_id = arguments?.getString("key")!!
        viewModel.billboardId = billboard_id
        viewModel.getPrice(viewModel.billboardId).observe(viewLifecycleOwner) { p ->
            price=p
        }
        viewModel.getBillboardAvailability(viewModel.billboardId).observe(viewLifecycleOwner) { availabilityMap ->
            year = 2023
            map = availabilityMap
            setAvailabilityForYear(year, availabilityMap, gridLayout)
        }

        binding.order.setOnClickListener {

            placeOrder(viewModel.billboardId, selected.toMap(), price)
            updateBillboardAvailability(viewModel.billboardId, selected)
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

    private fun getBillboardLocation(billboardId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val billboardRef = db.collection("billboards").document(billboardId)

        billboardRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val billboardData = documentSnapshot.data
                val location = billboardData?.get("location").toString()

                // Call the onSuccess callback function and pass the location value
                onSuccess(location)
            } else {
                // Call the onFailure callback function if the document doesn't exist
                onFailure()
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting billboard information", e)
            // Call the onFailure callback function in case of an error
            onFailure()
        }
    }



    private fun placeOrder(billboardId: String, selectedMonths: Map<String, Boolean>, price: Double) {
        val db = FirebaseFirestore.getInstance()
        val auth = Firebase.auth
        val clientUid = "WyMejmzRgePHN6nd4BCU45GWDb42"

        // Get the location of the billboard
        getBillboardLocation(billboardId, { location ->
            // Create a new order with the location included
            val orderStatus = HashMap<String, Any>()
            val timeStamp = FieldValue.serverTimestamp()
            orderStatus["Pending"] = timeStamp

            val order = hashMapOf(
                "billboard_id" to billboardId,
                "client_uid" to clientUid,
                "ordered_months" to selectedMonths,
                "order_status" to orderStatus,
                "price" to price,
                "location" to location
            )

            // Add the order to the "orders" collection
            db.collection("orders")
                .add(order)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Order placed successfully with ID: ${documentReference.id}")
                    Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()

                    // Add the orderId to the authorization collection
                    val orderId = documentReference.id
                    val authorizationRef = db.collection("authorization").document(clientUid)
                    authorizationRef.get().addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val updates = hashMapOf<String, Any>(
                                "orders" to FieldValue.arrayUnion(orderId)
                            )
                            authorizationRef.set(updates, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Order ID added to authorization collection")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding order ID to authorization collection", e)
                                    Toast.makeText(requireContext(), "Error adding order ID to authorization collection", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Log.d(TAG, "Client not found in authorization collection")
                        }
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error getting authorization collection", e)
                        Toast.makeText(requireContext(), "Error getting authorization collection", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error placing order", e)
                    Toast.makeText(requireContext(), "Error placing order", Toast.LENGTH_SHORT).show()
                }
        }, {
            // Handle the case where the location couldn't be retrieved
            Log.w(TAG, "Error getting billboard location")
            Toast.makeText(requireContext(), "Error getting billboard location", Toast.LENGTH_SHORT).show()
        })
    }



    private fun updateBillboardAvailability(billboardId: String, selectedMonths: Map<String, Boolean>) {
        val db = FirebaseFirestore.getInstance()
        val billboardRef = db.collection("billboards").document(billboardId)
        billboardRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {

                val updates = hashMapOf<String, Any>(
                    "availability" to selectedMonths
                )
                billboardRef.set(updates, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Billboard availability updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating billboard availability", e)
                        Toast.makeText(requireContext(), "Error updating billboard availability", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Log.d(TAG, "Billboard not found")
            }
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error getting billboard availability", e)
            Toast.makeText(requireContext(), "Error getting billboard availability", Toast.LENGTH_SHORT).show()
        }
    }


}
