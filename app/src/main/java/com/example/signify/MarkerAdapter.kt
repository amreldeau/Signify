package com.example.signify

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import com.google.android.gms.maps.model.Marker
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap


class MarkerAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvSnippet = view.findViewById<TextView>(R.id.snippet)

        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

}

