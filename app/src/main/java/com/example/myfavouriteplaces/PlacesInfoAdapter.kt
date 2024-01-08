package com.example.myfavouriteplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class PlacesInfoAdapter(val context: Context): GoogleMap.InfoWindowAdapter {

    private val layoutInflater = LayoutInflater.from(context)


    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        val infoWindow = layoutInflater.inflate(R.layout.place_info_window, null)

        val imImage = infoWindow.findViewById<ImageView>(R.id.imWindowImage)
        val tvTitle = infoWindow.findViewById<TextView>(R.id.tvWindowTitle)
        val tvDesc = infoWindow.findViewById<TextView>(R.id.tvWindowDesc)

        val place = p0.tag as? Place

        tvTitle.text = place?.title
        tvDesc.text = place?.description


        return infoWindow
    }


}