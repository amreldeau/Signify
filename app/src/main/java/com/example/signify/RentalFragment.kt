package com.example.signify

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.signify.databinding.FragmentDescriptionBinding
import com.example.signify.databinding.FragmentMapBinding
import com.example.signify.databinding.FragmentRentalBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RentalFragment : Fragment() {
    private lateinit var binding: FragmentRentalBinding
    private lateinit var billboard_id: String
    private val textViewIds = hashMapOf<String, Boolean>()
    private val id = Firebase.auth.uid
    private var notAvailableDays = hashMapOf<String, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRentalBinding.inflate(inflater, container, false)





        return binding.root
    }

}
