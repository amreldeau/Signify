package com.example.signify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.signify.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapFragment : Fragment(), OnMapReadyCallback{
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private val almaty = LatLng(43.24, 76.88)
    val db = Firebase.firestore
    val billboardsCollection = db.collection("billboards")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }
    fun billboardmarkers(){
        billboardsCollection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val billboardName = document.id
                val location = document.getString("location")
                val geopoint = document.getGeoPoint("geopoint")
                val billboardLatLng = LatLng(geopoint!!.latitude, geopoint.longitude)

                // Add the marker to the map
                googleMap.addMarker(
                    MarkerOptions()
                        .position(billboardLatLng)
                        .icon(bitmapFromVector(requireContext(), R.drawable.billboard_marker))
                        .title(billboardName)
                        .snippet(location)
                )

            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(almaty, 12f))
        val adapter = MarkerAdapter(requireContext())
        googleMap.setInfoWindowAdapter(adapter)
        googleMap.setOnInfoWindowClickListener { marker ->
            // Handle InfoWindow click event here
            val bundle = Bundle()
            bundle.putString("key", marker.title)
            val fragment = DescriptionFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
        // Add markers, etc. here
        billboardmarkers()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
