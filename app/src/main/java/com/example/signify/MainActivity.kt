package com.example.signify


import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.signify.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    //#1 Defining a BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val almaty = LatLng(43.24, 76.88)
    private lateinit var map: GoogleMap

    // Define the fragments
    private lateinit var mapFragment: SupportMapFragment

    private val billboardDescriptionBottomSheetCreator = BillboardDescriptionBottomSheetCreator()
    private val billboardSelectMonthBottomSheetCreator = BillboardSelectMonthBottomSheetCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync  { googleMap ->
            onMapReady(googleMap)
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(applicationContext, R.raw.map_style))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almaty, 12f))
            googleMap.setOnCameraMoveListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            setupMap()
        }

        binding.billboardDescription.selectMonth.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val billboardSelectMonthBottomSheet = billboardSelectMonthBottomSheetCreator.createBottomSheet(binding.billboardSelectMonth.bottomSheet)
            bottomSheetBehavior = billboardSelectMonthBottomSheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.billboardSelectMonth.back.setOnClickListener{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val billboardDescriptionBottomSheet = billboardDescriptionBottomSheetCreator.createBottomSheet(binding.billboardDescription.bottomSheet)
            bottomSheetBehavior = billboardDescriptionBottomSheet
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        val navigationView = findViewById<NavigationView>(R.id.nav)
        navigationView.itemIconTintList = null
        val menuItemPosition = 1
        val menuItem = navigationView.menu.getItem(menuItemPosition)
        changeMenuItemColor(menuItem, Color.parseColor("#c62928"))

        setSupportActionBar(binding.myToolbar)


        supportActionBar?.title = ""

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.myToolbar, R.string.app_name, R.string.app_name)
        toggle.setHomeAsUpIndicator(R.drawable.billboard_marker)
        toggle.syncState()

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_burger_horizontal_svgrepo_com__2_)

        //#2 Initializing the BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.test1.bottomSheet)

        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })



        //#4 Changing the BottomSheet State on ButtonClick

    }




    /**
     * Adds marker representations of the places list on the provided GoogleMap object
     */
    private fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMarkerClickListener { marker ->
            val billboard = marker.tag as Billboard
            Toast.makeText(this, billboard.location, Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showBillboardDetails(billboard)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            true
        }
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
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
        // Get the Firestore instance
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the "billboards" collection
        val billboardsCollection = db.collection("billboards")

        // Retrieve billboards from Firebase Firestore
        billboardsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Clear existing markers
                map.clear()

                // Loop through each billboard document in the snapshot
                for (document in snapshot.documents) {
                    // Get the GeoPoint and other fields of the billboard
                    val geoPoint = document.getGeoPoint("geopoint")
                    val id = document.getString("id") ?: ""
                    val price = document.getDouble("price") ?: 0.0
                    val location = document.getString("location") ?: ""
                    val size = document.getString("size") ?: ""
                    val surface = document.getString("surface") ?: ""
                    val type = document.getString("type") ?: ""

                    // Retrieve the "available months" field from the document and get a reference to the corresponding "months" document
                    val availableMonthsReference = document.get("availablemonths") as DocumentReference
                    availableMonthsReference.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val availableMonthsDocument = task.result

                            // Create a HashMap to store the availability of each month
                            val availableMonths = HashMap<Int, Boolean>()
                            availableMonthsDocument?.let { months ->
                                for (i in 1..12) {
                                    val available = months.getBoolean(i.toString()) ?: false
                                    availableMonths[i] = available
                                }
                            }

                            // Create a new Billboard object and set the available months HashMap
                            val billboard = Billboard(geoPoint, id, price, location, size, surface, type, availableMonths)

                            // Create a marker and add it to the map
                            val marker = map.addMarker(
                                MarkerOptions().position(LatLng(billboard.geoPoint!!.latitude, billboard.geoPoint.longitude))
                                    .title(billboard.location)
                                    .icon(bitmapFromVector(applicationContext, R.drawable.billboard_marker)
                                    )
                            )

                            // Associate the Billboard object with the marker
                            marker.tag = billboard
                        } else {
                            Log.e(
                                TAG,
                                "Error retrieving available months document: ",
                                task.exception
                            )
                        }
                    }
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return true
    }
    private fun showBillboardDetails(billboard: Billboard) {
        binding.billboardDescription.billboardLocation.text = billboard.location
        val billboardDescriptionBottomSheet = billboardDescriptionBottomSheetCreator.createBottomSheet(binding.billboardDescription.bottomSheet)
        bottomSheetBehavior = billboardDescriptionBottomSheet
    }
    private fun changeMenuItemColor(menuItem: MenuItem, @ColorInt color: Int) {
        val coloredMenuItemTitle = SpannableString(menuItem.title)
        coloredMenuItemTitle.setSpan(
            ForegroundColorSpan(color),
            0,
            coloredMenuItemTitle.length,
            0
        )
        menuItem.title = coloredMenuItemTitle
    }
}

