package com.example.signify


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.signify.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    //#1 Defining a BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    val almaty = LatLng(43.24, 76.88)
    var clicked = false
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }


    private lateinit var database: FirebaseDatabase
    private lateinit var billboardRef: DatabaseReference
    private lateinit var map: GoogleMap

    // Define the fragments
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var otherFragment: BillboardListFragment

    // Keep track of the current fragment
    private var isMapFragmentVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        billboardRef = database.getReference("billboards")
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        otherFragment = BillboardListFragment()
        mapFragment.getMapAsync  { googleMap ->
            onMapReady(googleMap)
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almaty, 12f))
            googleMap.setOnCameraMoveListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            setupMap()
        }

        binding.fabMain.setOnClickListener {
            onButtonClicked()
        }
        binding.swaper.setOnClickListener {
            // Switch between the fragments based on the current fragment
            if (isMapFragmentVisible) {
                supportFragmentManager.beginTransaction().replace(R.id.map_fragment, otherFragment).commit()
                binding.swaper.setImageResource(R.drawable.map_vector)
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.map_fragment, mapFragment).commit()
                binding.swaper.setImageResource(R.drawable.list_pointers_svgrepo_com)
                mapFragment.getMapAsync  { googleMap ->
                    onMapReady(googleMap)
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almaty, 12f))
                    googleMap.setOnCameraMoveListener {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                    setupMap()
                }
            }
            // Update the flag
            isMapFragmentVisible = !isMapFragmentVisible
        }
        binding.billboardDescription.selectMonth.setOnClickListener{
            binding.billboardSelectMonth.billboardName.text = binding.billboardDescription.billboardName.text
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardSelectMonth.bottomSheet)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.billboardSelectMonth.description.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardDescription.bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }



        setSupportActionBar(binding.myToolbar)


        supportActionBar?.title = "";
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.myToolbar, R.string.app_name, R.string.app_name)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //#2 Initializing the BottomSheetBehavior

        bottomSheetBehavior = BottomSheetBehavior.from(binding.test1.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        binding.billboardDescription.viewPager.adapter = PagerAdapter(supportFragmentManager)
        binding.billboardDescription.tabLayout.setupWithViewPager(binding.billboardDescription.viewPager)


        //#4 Changing the BottomSheet State on ButtonClick

    }
    private fun onButtonClicked() {
        setVisiblity(clicked)
        setAnimaton(clicked)
        clicked = !clicked
    }
    private fun setVisiblity(clicked: Boolean) {
        if(!clicked){
            binding.fab1.visibility = View.VISIBLE
            binding.fab2.visibility = View.VISIBLE
            binding.fab1text.visibility = View.VISIBLE
            binding.fab2text.visibility = View.VISIBLE
        }
        else{
            binding.fab1.visibility = View.INVISIBLE
            binding.fab2.visibility = View.INVISIBLE
            binding.fab1text.visibility = View.INVISIBLE
            binding.fab2text.visibility = View.INVISIBLE
        }
    }
    private fun setAnimaton(clicked: Boolean) {
        if(!clicked){
            binding.fab1.startAnimation(fromBottom)
            binding.fab2.startAnimation(fromBottom)
            binding.fab1text.startAnimation(fromBottom)
            binding.fab2text.startAnimation(fromBottom)
        }
        else{
            binding.fab1text.startAnimation(toBottom)
            binding.fab2text.startAnimation(toBottom)
        }
    }



    /**
     * Adds marker representations of the places list on the provided GoogleMap object
     */
    private fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMarkerClickListener { marker ->
            val billboard = marker.tag as Billboard
            Toast.makeText(this, billboard.name, Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showBillboardDetails(billboard)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            true
        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    private fun setupMap() {
        // Retrieve billboards from Firebase Realtime Database
        billboardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing markers
                map.clear()

                // Loop through each billboard in the snapshot
                for (billboardSnapshot in snapshot.children) {
                    // Get the latitude, longitude, and name of the billboard

                    val latitude = billboardSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = billboardSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                    val name = billboardSnapshot.child("name").getValue(String::class.java) ?: ""
                    val billboard = Billboard(latitude, longitude, name)
                    // Create a marker and add it to the map

                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(billboard.latitude, billboard.longitude))
                            .title(billboard.name)
                            .icon(BitmapFromVector(applicationContext, R.drawable.billboard_marker))
                    )
                    marker.tag = billboard
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })

        // Set up marker click listener

    }
    private fun showBillboardDetails(billboard: Billboard) {
        binding.billboardDescription.billboardName.text = billboard.name
        // Set the BottomSheetBehavior for the new bottom sheet
        // Get the root view of the activity
        bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardDescription.bottomSheet)


    }
}

class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragments = listOf(
        BillboardParametersFragment(),
        BillboardListFragment(),
        BillboardListFragment()
    )

    override fun getCount() = fragments.size

    override fun getItem(position: Int) = fragments[position]

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Parameters"
            1 -> "Images"
            2 -> "Reviews"
            else -> ""
        }
    }
}
