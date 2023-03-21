package com.example.signify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.*


class BillboardAdapter(private val context: Context) : Marker {

    override fun getMarkerOptions(billboard: Billboard): MarkerOptions {
        val options = MarkerOptions()
            .position(LatLng(billboard.geoPoint!!.latitude, billboard.geoPoint.longitude))
            .title(billboard.location)
            .icon(bitmapFromVector(context, R.drawable.billboard_marker))
        return options
    }

    private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}





