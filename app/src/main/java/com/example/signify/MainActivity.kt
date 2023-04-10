package com.example.signify

<<<<<<< HEAD
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    // Firebase variable
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //START: [Firebase]
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        //END: [Firebase]
=======
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
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    //#1 Defining a BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private val almaty = LatLng(43.24, 76.88)
    private lateinit var map: GoogleMap
    private val invoker = Invoker()
    // Define the fragments
    private lateinit var mapFragment: SupportMapFragment
    private var billboard: Billboard? = null

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** setup GoogleMap */
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            onMapReady(googleMap)
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    applicationContext,
                    R.raw.map_style
                )
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almaty, 12f))
            googleMap.setOnCameraMoveListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        /** end setup GoogleMap */

        binding.billboardDescription.selectMonth.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardSelectMonth.bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            updateSelectMonthBottomSheet(billboard!!)
        }
        binding.billboardSelectMonth.back.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardDescription.bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        /** setup toolbar */
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_burger_horizontal_svgrepo_com__2_)
        /** end setup toolbar */

        /** setup navigation drawer */
        val navigationView = binding.nav
        navigationView.itemIconTintList = null
        val headerView = navigationView.getHeaderView(0)
        val headerText = headerView.findViewById<TextView>(R.id.user)
        headerText.text = currentUser?.email
        val menuItem = navigationView.menu.getItem(1)
        changeMenuItemColor(menuItem, Color.parseColor("#c62928"))
        navigationView.setNavigationItemSelectedListener {
            // Handle navigation view item clicks here.
            when (menuItem.itemId) {
                // Handle other menu items here
                R.id.signout -> {
                    // Sign out the current user
                    val command = SignOutCommand(FirebaseAuth.getInstance())
                    invoker.executeCommand(command)
                    // Start the sign-in activity (or any other activity as appropriate)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            // Close the navigation drawer
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.myToolbar,
            R.string.app_name,
            R.string.app_name
        )
        toggle.setHomeAsUpIndicator(R.drawable.billboard_marker)
        toggle.syncState()
        /** end setup navigation drawer */

        /** setup bottom sheet */
        bottomSheetBehavior = BottomSheetBehavior.from(binding.test1.bottomSheet)

        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
        })
        /** end setup bottom sheet */
    }


    /**Adds marker representations of the places list on the provided GoogleMap object*/
    private fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the "billboards" collection
        val billboardsCollection = db.collection("billboards")
        val adapter = BillboardAdapter(this)
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
                    val availableMonthsReference =
                        document.get("availablemonths") as DocumentReference
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

                            val billboard = Billboard(geoPoint!!, id, price, location, size, surface, type, availableMonths)

                            // Create a Marker object using the adapter
                            val marker = map.addMarker(adapter.getMarkerOptions(billboard))
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
        map.setOnMarkerClickListener { marker ->
            billboard = marker.tag as Billboard
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showBillboardDetails(billboard!!)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            true
        }
    }
    /**end Adds marker representations of the places list on the provided GoogleMap object*/


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.account_menu, menu)
        return true
    }

    private fun showBillboardDetails(billboard: Billboard) {
        binding.billboardDescription.billboard.text = getString(R.string.billboard_id, billboard.id)
        binding.billboardDescription.location.text = getString(
            R.string.billboard_location,
            billboard.geoPoint!!.longitude,
            billboard.geoPoint.latitude
        )
        binding.billboardDescription.billboardPrice.text =
            getString(R.string.billboard_price, billboard.price)
        binding.billboardDescription.billboardSize.text =
            getString(R.string.billboard_size, billboard.size)
        binding.billboardDescription.billboardSurface.text =
            getString(R.string.billboard_surface, billboard.surface)
        binding.billboardDescription.billboardType.text =
            getString(R.string.billboard_type, billboard.type)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.billboardDescription.bottomSheet)
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

    private fun updateSelectMonthBottomSheet(billboard: Billboard) {
        val monthViewLayout = binding.billboardSelectMonth.gridLayout
        var price = 0.00
        val selectedMonthsList = arrayListOf<Boolean>()
        for (i in 1..12) {
            selectedMonthsList.add(false)
        }

        binding.billboardSelectMonth.billboardPrice.text = getString(R.string.billboard_order_price, price)
        binding.billboardSelectMonth.billboard.text = getString(R.string.billboard_id, billboard.id)

        for (month in 1..12) {
            val monthView = monthViewLayout.getChildAt(month-1) as TextView
            if (billboard.availableMonths[month] == true) {
                monthView.setBackgroundResource(R.drawable.background_available)
                monthView.setTextColor(getColor(R.color.black))
                monthView.isClickable = true
            } else {
                monthView.setBackgroundResource(R.drawable.background_not_available)
                monthView.setTextColor(getColor(R.color.black))
                monthView.isClickable = false
            }
            monthView.setOnClickListener {
                if (billboard.availableMonths[month] == true) {
                    if (selectedMonthsList[month - 1]) {
                        selectedMonthsList[month - 1] = false
                        price -= billboard.price
                        monthView.setBackgroundResource(R.drawable.background_available)
                        monthView.setTextColor(getColor(R.color.black))
                    } else {
                        selectedMonthsList[month - 1] = true
                        price += billboard.price
                        monthView.setBackgroundResource(R.drawable.background_selected_month)
                        monthView.setTextColor(getColor(R.color.white))
                    }
                    binding.billboardSelectMonth.billboardPrice.text = getString(R.string.billboard_order_price, price)
                }
            }
        }
        binding.billboardSelectMonth.order.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val order = Order()
            order.generateID()
            order.setPrice(100.0) // set the price to 100
            OrderSingleton.getInstance().order = order

            val fragment = CheckoutFragment() // create a new instance of your fragment
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.addToBackStack(null) // add the transaction to the back stack so the user can navigate back to the previous fragment
            transaction.commit() // commit the transaction
        }
>>>>>>> Nurzhan
    }
}