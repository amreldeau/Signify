package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.signify.databinding.FragmentDescriptionBinding
import com.example.signify.databinding.FragmentOrderBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private val textViewIds = hashMapOf<String, Boolean>()
    private val id = Firebase.auth.uid
    private var notAvailableDays = hashMapOf<String, Boolean>()
    private lateinit var billboard_id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)

// Enable the "Up" button in the toolbar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)



        billboard_id = arguments?.getString("key")!!
        val db = FirebaseFirestore.getInstance()

        val availableDaysRef = db.collection("available_days").document(billboard_id)
        availableDaysRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                notAvailableDays =
                    documentSnapshot.get("not_available_days") as HashMap<String, Boolean>
                months()

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
                "ordered_days" to textViewIds.keys.toList(),
                "order_status" to "In process"
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

    fun months() {
        // Find the GridLayout inside the view
        val gridLayout = binding.grid
        val inflater = LayoutInflater.from(context)
        // Create 30 TextViews and add them to the GridLayout with different ids
        for (i in 1 until 13) {
            val textView = inflater.inflate(R.layout.textview_item, gridLayout, false) as TextView
            val textViewId = i
            textView.id = textViewId
            textView.text = "$textViewId"
            if (!notAvailableDays.getOrDefault(textViewId.toString(), false)) {
                textView.setOnClickListener {
                    // Change the background drawable of the TextView when it's clicked
                    textViewIds[textViewId.toString()] = true
                    binding.total.text = textViewIds.size.toString()
                    textView.setBackgroundResource(R.drawable.background_selected_month)
                }
            } else {
                textView.setBackgroundResource(R.drawable.background_not_available)
            }
            gridLayout.addView(textView)
        }
    }
}
