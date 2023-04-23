package com.example.signify

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.signify.databinding.FragmentDescriptionBinding
import com.example.signify.databinding.FragmentMapBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DayViewDecorator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class DescriptionFragment : Fragment(), MyPagerAdapter.OnTextViewClickListener {
    private lateinit var binding: FragmentDescriptionBinding
    private lateinit var billboard_id: String
    private val textViewIds = hashMapOf<String, Boolean>()
    private val id = Firebase.auth.uid
    private var notAvailableDays = hashMapOf<String, Boolean>()
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        billboard_id = arguments?.getString("key")!!
        val db = FirebaseFirestore.getInstance()

        val billboardsRef = db.collection("billboards")
        billboardsRef.document(billboard_id).get().addOnSuccessListener { billboardSnapshot ->
            val billboardData = billboardSnapshot.data ?: return@addOnSuccessListener
            val location = billboardData["location"] as? String ?: ""
            val price = billboardSnapshot.getDouble("price")
            binding.billboardName.text = getString(R.string.billboard_id, billboard_id)
            binding.location.text = location
            binding.billboardPrice.text = getString(R.string.billboard_price, price)
        }

        val availableDaysRef = db.collection("available_days").document(billboard_id)
        availableDaysRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                notAvailableDays = documentSnapshot.get("not_available_days") as HashMap<String, Boolean>
                val viewPager = binding.viewpager
                val adapter = MyPagerAdapter(requireContext(), this, notAvailableDays)
                viewPager.adapter = adapter
            } else {
                // handle case where B1 document doesn't exist
            }
        }.addOnFailureListener { exception ->
            // handle any exceptions thrown while fetching the document
        }


        binding.order.setOnClickListener {
            val db = Firebase.firestore

// Define the document data for the "orders" collection
            val ordersData = hashMapOf(
                "client_id" to id.toString(),  // replace with code to get current user ID
                "billboard_id" to billboard_id,
                "date" to "18.04.2023",
                "ordered_days" to textViewIds.keys.toList(),
                "order_status" to "Created"
            )

// Add the new document to the "orders" collection
            db.collection("orders")
                .add(ordersData)
                .addOnSuccessListener { documentReference ->
                    // Get the ID of the newly created document
                    val orderId = documentReference.id

                    // Define the data to add to the "available_month/B1" document
                    val availableMonthData = hashMapOf(
                        "not_available_days" to textViewIds
                    )

                    // Add the data to the "available_month/B1" document
                    db.collection("available_days")
                        .document(billboard_id)
                        .set(availableMonthData, SetOptions.merge()).addOnSuccessListener {
                            // Success! The data was added to both collections

                            // Add the orderId to the "orders" array in the authorization collection
                            db.collection("authorization")
                                .document(id.toString())  // replace with code to get current user ID
                                .update("orders", FieldValue.arrayUnion(orderId))
                        }
                }
        }
        return binding.root
    }
    override fun onTextViewClick(textViewId: Int) {
        textViewIds[textViewId.toString()] = true

    }

}
