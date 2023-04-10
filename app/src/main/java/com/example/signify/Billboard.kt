package com.example.signify

import com.google.firebase.firestore.GeoPoint

data class Billboard(
    val geoPoint: GeoPoint?,
    val id: String,
    val price: Double,
    val location: String,
    val size: String,
    val surface: String,
    val type: String,
    val availableMonths: HashMap<Int, Boolean>

)
