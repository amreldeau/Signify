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
import com.example.signify.databinding.FragmentChangeBookingBinding
import com.example.signify.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ChangeBookingFragment : Fragment() {

    private lateinit var binding: FragmentChangeBookingBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var repository: FirestoreRepository
    private var selected: MutableMap<String, Boolean> = mutableMapOf()
    private var occupied: Map<String, Boolean> = mutableMapOf()
    private lateinit var viewModel: ChangeBookingViewModel
    var newpayout = 0.0
    var originalpayout = 0.0
    var pricePerMonth = 0.0
    var penalty = 1.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeBookingBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        repository = FirestoreRepository()
        val orderId = requireArguments().getString("orderId")!!
        val currentUser = FirebaseAuth.getInstance().currentUser
        val clientId = currentUser?.uid ?: ""
        var billboardId = ""
        var orderDate: Date
        var year = 2023
        lateinit var map: HashMap<String, Boolean>
        val gridLayout = binding.gridLayout
        viewModel = ViewModelProvider(this).get(ChangeBookingViewModel::class.java)
        viewModel.getOrderById(orderId).observe(viewLifecycleOwner) { order ->
            if (order != null) {
                // Use the order object here
                billboardId = order.billboardId
                occupied = order.occupied
                orderDate = order.orderDate
                selected = occupied as MutableMap<String, Boolean>
                originalpayout = order.totalCost
                newpayout = originalpayout
                calculatePenalty(orderDate)
            }

            viewModel.getBillboardAvailability(billboardId)
                .observe(viewLifecycleOwner) { availabilityMap ->
                    map = availabilityMap
                }
            viewModel.getPrice(billboardId).observe(viewLifecycleOwner) { price ->
                pricePerMonth = price
                setAvailabilityForYear(year, map, gridLayout)
            }
            binding.originalPayout.text = getString(R.string.price, originalpayout)
            binding.newPayout.text = getString(R.string.price, newpayout)
            binding.originalDates.text =
                getString(R.string.original_dates, getSelectedMonthsText(occupied))
        }
        binding.right.setOnClickListener {
            year++
            binding.year.text = year.toString()
            setAvailabilityForYear(year, map, gridLayout)
        }
        binding.left.setOnClickListener {
            if (year > 2023) {
                year--
                binding.year.text = year.toString()
                setAvailabilityForYear(year, map, gridLayout)
            }
        }
        binding.continueBtn.setOnClickListener {
            viewModel.placeRequest(
                clientId,
                orderId,
                (newpayout - originalpayout),
                selected,
                "ScheduleChange"
            )
            showSuccessDialog()
        }
        binding.backButtonChange.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun showSuccessDialog() {
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Success")
            .setMessage("Request sent.")
            .setPositiveButton("OK") { _, _ ->
                requireActivity().onBackPressed()
            }
            .create()

        dialog.show()
    }

    fun getSelectedMonthsText(selected: Map<String, Boolean>): String {
        val monthNames = arrayOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
        )

        val selectedByYear = selected.keys.groupBy { it.substring(0, 4) }

        return selectedByYear.map { (year, monthKeys) ->
            val months = monthKeys.map { monthNames[it.substring(9).toInt() - 1] }
            "$year: ${months.joinToString(", ")}"
        }.joinToString(", ")
    }

    fun calculatePenalty(orderDate: Date) {
        val currentDate = Date()
        val diffInMs = currentDate.time - orderDate.time
        val diffInHours = diffInMs / (1000 * 60 * 60)

        penalty = when {
            diffInHours < 24 -> 1.0
            diffInHours < 48 -> 0.5
            else -> 1.0
        }
    }

    private fun setAvailabilityForYear(
        year: Int,
        availabilityMap: HashMap<String, Boolean>,
        gridLayout: GridLayout
    ) {
        val color = ContextCompat.getColor(requireContext(), R.color.gray)
        val whitecolor = ContextCompat.getColor(requireContext(), R.color.white)
        val blackcolor = ContextCompat.getColor(requireContext(), R.color.black)

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is TextView) {
                val textViewId = child.resources.getResourceEntryName(child.id)
                val e = "$year$textViewId"
                val reserved = availabilityMap[e] ?: false

                if (reserved) {
                    if (selected[e] == true) {
                        child.setBackgroundResource(R.drawable.background_selected_month)
                        child.setTextColor(whitecolor)
                    } else {
                        child.setOnClickListener(null)
                        child.paintFlags = child.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        child.setTextColor(color)
                        child.setBackgroundResource(0)
                    }
                } else {
                    child.paintFlags = child.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    if (selected["$year$textViewId"] == true) {
                        child.setBackgroundResource(R.drawable.background_selected_month)
                        child.setTextColor(whitecolor)

                        child.setOnClickListener {
                            val e = "$year$textViewId"
                            val a = selected[e] ?: false
                            if (!a) {
                                if (occupied["$year$textViewId"] == true) {
                                    newpayout += (pricePerMonth * penalty)
                                } else {
                                    newpayout += pricePerMonth
                                }
                                selected["$year$textViewId"] = true
                                child.setBackgroundResource(R.drawable.background_selected_month)
                                child.setTextColor(whitecolor)

                            }
                            if (a) {
                                if (occupied["$year$textViewId"] == true) {
                                    newpayout -= (pricePerMonth * penalty)

                                } else {
                                    newpayout -= pricePerMonth

                                }
                                selected.remove("$year$textViewId")
                                child.setBackgroundResource(0)
                                child.setTextColor(blackcolor)
                            }
                            binding.newPayout.text = getString(R.string.price, newpayout)
                            binding.payoutDifference.text =
                                getString(R.string.price, (newpayout - originalpayout))
                            binding.newDates.text = getSelectedMonthsText(selected)
                            binding.info.text =
                                getString(R.string.change_payout_info, (newpayout - originalpayout))
                        }
                    } else {
                        child.setTextColor(blackcolor)
                        child.setBackgroundResource(0)
                        child.setOnClickListener {
                            val e = "$year$textViewId"
                            val a = selected[e] ?: false
                            if (!a) {
                                if (occupied["$year$textViewId"] == true) {
                                    newpayout += (pricePerMonth * penalty)

                                } else {
                                    newpayout += pricePerMonth

                                }
                                selected["$year$textViewId"] = true
                                child.setBackgroundResource(R.drawable.background_selected_month)
                                child.setTextColor(whitecolor)

                            }
                            if (a) {
                                if (occupied["$year$textViewId"] == true) {
                                    newpayout -= (pricePerMonth * penalty)

                                } else {
                                    newpayout -= pricePerMonth

                                }
                                selected.remove("$year$textViewId")
                                child.setBackgroundResource(0)
                                child.setTextColor(blackcolor)

                            }
                            binding.newPayout.text = getString(R.string.price, newpayout)
                            binding.payoutDifference.text =
                                getString(R.string.price, (newpayout - originalpayout))
                            binding.newDates.text = getSelectedMonthsText(selected)
                            binding.info.text =
                                getString(R.string.change_payout_info, (newpayout - originalpayout))
                        }
                    }
                }
            }
        }
    }
}