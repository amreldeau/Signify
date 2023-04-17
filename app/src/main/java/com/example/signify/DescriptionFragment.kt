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

class DescriptionFragment : Fragment() {
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


// Show the date picker when a button is clicked



// Set the date you want to make unselectable


        return binding.root
    }



// Disable the date by setting it to a different day

    }
