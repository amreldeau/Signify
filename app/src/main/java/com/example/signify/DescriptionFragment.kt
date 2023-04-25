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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
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
    var orderId =""
    var clientName = ""

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


        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

// Check if the current user is authenticated
        if (currentUserUid != null) {

            // Get a reference to the document with the user's uid
            val authorizationDocRef = db.collection("authorization").document(currentUserUid)

            // Retrieve the data from the document
            authorizationDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Get the value of the "name" field
                        clientName = document.getString("name")!!
                        if (clientName != null) {
                            // The value of the "name" field was retrieved successfully
                            // Use the clientName variable as needed
                        } else {
                            // The "name" field doesn't exist or its value is null
                        }
                    } else {
                        // The document doesn't exist
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the exception if the data couldn't be retrieved
                }
        } else {
            // The current user is not authenticated
        }

        binding.order.setOnClickListener {
            val db = Firebase.firestore
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val orderStatus = HashMap<String, String>()
            orderStatus[currentDate] = "Pending"

// Define the document data for the "orders" collection
            val ordersData = hashMapOf(
                "client_id" to id.toString(),  // replace with code to get current user ID
                "billboard_id" to billboard_id,
                "client_name" to clientName,
                "ordered_days" to textViewIds.keys.toList(),
                "order_status" to orderStatus
            )

// Add the new document to the "orders" collection
            db.collection("orders")
                .add(ordersData)
                .addOnSuccessListener { documentReference ->
                    // Get the ID of the newly created document
                    orderId = documentReference.id

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
