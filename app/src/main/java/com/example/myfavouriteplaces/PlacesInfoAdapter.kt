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
        val tvTitle = infoWindow.findViewById<TextView>(R.id.tvWindowTitle)
        val tvDesc = infoWindow.findViewById<TextView>(R.id.tvWindowDesc)
        val imImage = infoWindow.findViewById<ImageView>(R.id.cardImage)
        val place = p0.tag as? Place

        tvTitle.text = place?.title
        tvDesc.text = place?.description
        val iconsArray = context.resources.obtainTypedArray(R.array.categories_icons)
        val categoryArray = context.resources.getStringArray(R.array.categories_array)
        val categoryIndex = categoryArray.indexOf(place?.category)
        if(categoryIndex != -1) {
            val icon = iconsArray.getResourceId(categoryIndex, -1)
            imImage.setImageResource(icon)
        } else {
            imImage.setImageResource(R.drawable.baseline_ballot_24)
        }
        iconsArray.recycle()

        return infoWindow
    }


}