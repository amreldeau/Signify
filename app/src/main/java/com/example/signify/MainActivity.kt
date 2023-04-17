package com.example.signify

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.example.signify.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        val mapFragment = com.example.signify.MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mapFragment)
            .commit()


        /** end setup bottom sheet */
        binding.navbar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val mapFragment = com.example.signify.MapFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mapFragment)
                        .commit()
                }
                R.id.order -> {
                    val r = RentalFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, r)
                        .commit()
                }
                R.id.account -> {
                    val accountFragment = AccountFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, accountFragment)
                        .commit()
                }
                else -> {

                }
            }
            true
        }

    }



}