package com.example.signify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.FragmentOrderDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val db = Firebase.firestore
        val orderId = arguments?.getString("orderId")!!
        val orderRef = db.collection("orders").document(orderId)
        var newestStatus: String? = null
        orderRef.get().addOnSuccessListener { documentSnapshot ->

            val orderData = documentSnapshot.data ?: return@addOnSuccessListener
            val orderStatusMap =
                orderData["order_status"] as? Map<*, *> ?: return@addOnSuccessListener
            var newestDate: Date? = null
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for ((dateStr, status) in orderStatusMap) {
                val date = dateFormatter.parse(dateStr.toString())
                if (newestDate == null || date.after(newestDate)) {
                    newestDate = date
                    newestStatus = status.toString()
                }
            }

            val orderedDaysArray = orderData["ordered_days"] as? ArrayList<*>

            if (orderedDaysArray != null) {
                val sortedDates = orderedDaysArray
                    .mapNotNull { it as? String }
                    .map { it.padStart(4, '0') }
                    .sorted()
                val dateFormatter = SimpleDateFormat("E, MMM dd", Locale.US)
                val minDateStr = sortedDates.firstOrNull()?.let {
                    val month = it.substring(0, 2).toInt()
                    val dayOfMonth = it.substring(2).toInt()
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.MONTH, month - 1)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    dateFormatter.format(calendar.time)
                } ?: ""
                val maxDateStr = sortedDates.lastOrNull()?.let {
                    val month = it.substring(0, 2).toInt()
                    val dayOfMonth = it.substring(2).toInt()
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.MONTH, month - 1)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    dateFormatter.format(calendar.time)
                } ?: ""

                binding.date1.text = minDateStr
                binding.date2.text = maxDateStr
                // Get the billboard information and update the UI
                val billboardId = orderData["billboard_id"] as? String ?: ""
                val billboardsRef = db.collection("billboards")
                billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
                    val billboardData = billboardSnapshot.data ?: return@addOnSuccessListener
                    val location = billboardData["location"] as? String ?: ""
                    binding.billboardName.text = getString(R.string.billboard_id, billboardId)
                    binding.location.text = location
                    binding.status.text = newestStatus
                }
            }
            val textView = binding.status

            when (newestStatus) {
                "Created" ->

                    textView.text = "Created"

                "Confirmed" ->

                    textView.text = "Confirmed"

                "Active" ->

                    textView.text = "Active"

                "Completed" ->

                    textView.text = "Completed"


                "Changes pending" ->

                    textView.text = "Changes pending"

                "Changes declined" -> textView.text = "Changes declined"
                "Changes in process" -> textView.text = "Changes in process"

                "Declined" -> textView.text = "Declined"
            }
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
}
