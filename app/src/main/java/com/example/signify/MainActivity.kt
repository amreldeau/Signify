package com.example.signify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.signify.databinding.ActivityMainBinding

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
                    val ordersFragment = OrdersFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ordersFragment)
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