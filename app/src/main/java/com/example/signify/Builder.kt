package com.example.signify

import com.google.firebase.firestore.GeoPoint

interface Builder {
    fun setGeoPoint(geoPoint: GeoPoint?): BillboardBuilder

    fun setId(id: String?): BillboardBuilder

    fun setPrice(price: Double?): BillboardBuilder

    fun setLocation(location: String?): BillboardBuilder

    fun setSize(size: String?): BillboardBuilder

    fun setSurface(surface: String?): BillboardBuilder

    fun setType(type: String?): BillboardBuilder

    fun setAvailableMonths(availableMonths: HashMap<Int, Boolean>?): BillboardBuilder

    fun build(): Billboard
}