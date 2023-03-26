package com.example.signify

import com.google.android.gms.maps.model.MarkerOptions


interface Marker {
    fun getMarkerOptions(billboard: Billboard): MarkerOptions
}