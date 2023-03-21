package com.example.signify

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

class BillboardBuilder : Builder {
    private var geoPoint: GeoPoint? = null
    private var id: String? = null
    private var price: Double? = null
    private var location: String? = null
    private var size: String? = null
    private var surface: String? = null
    private var type: String? = null
    private var availableMonths: HashMap<Int, Boolean>? = null

    override fun setGeoPoint(geoPoint: GeoPoint?): BillboardBuilder {
        this.geoPoint = geoPoint
        return this
    }

    override fun setId(id: String?): BillboardBuilder {
        this.id = id
        return this
    }

    override fun setPrice(price: Double?): BillboardBuilder {
        this.price = price
        return this
    }

    override fun setLocation(location: String?): BillboardBuilder {
        this.location = location
        return this
    }

    override fun setSize(size: String?): BillboardBuilder {
        this.size = size
        return this
    }

    override fun setSurface(surface: String?): BillboardBuilder {
        this.surface = surface
        return this
    }

    override fun setType(type: String?): BillboardBuilder {
        this.type = type
        return this
    }

    override fun setAvailableMonths(availableMonths: HashMap<Int, Boolean>?): BillboardBuilder {
        this.availableMonths = availableMonths
        return this
    }

    override fun build(): Billboard {
        return Billboard(
            geoPoint,
            id!!,
            price!!,
            location!!,
            size!!,
            surface!!,
            type!!,
            availableMonths!!
        )
    }
}