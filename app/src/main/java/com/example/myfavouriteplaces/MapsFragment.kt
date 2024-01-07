package com.example.myfavouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val args: MapsFragmentArgs by navArgs()
    private var lat: Double? = null
    private var lng: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        getLatLng()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun getLatLng() {

            lat = args.lat.toDouble()
            lng = args.lng.toDouble()
            if (lat == 0.0 || lat == null || lng == null) {
                lat = currentUser.latLng?.latitude
                lng = currentUser.latLng?.longitude
                if (lat == null) {
                    lat = 59.334591
                    lng = 18.063240
                }

            }
//            if (lng == 0.0 || lng == null) {
//                lng = currentUser.latLng?.longitude
//                if (lng == null) {
//                    lng = 18.063240
//                }
//            }

    }


    override fun onMapReady(map: GoogleMap) {
        createMarksFavourites(map)
        createMarksShared(map)
        Log.d("!!!", lat.toString() + lng.toString())
        if (lat != null && lng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lng!!), 15f))

        } else {
            val sthlm = LatLng(59.334591, 18.063240)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sthlm, 15f))
        }
    }

    private fun createMarksFavourites(map: GoogleMap) {
        for(place in currentUser.favouritesList) {
            place.lat?.let {it1 ->
                place.lng?.let {it2 ->
                    val position = LatLng(it1, it2)
                    val marker = map.addMarker(MarkerOptions().position(position).title(place.title))
                    marker?.tag = place
                }
            }

        }
    }

    private fun createMarksShared(map: GoogleMap) {
        for(place in currentUser.sharedFavouritesList) {
            place.lat?.let {it1 ->
                place.lng?.let {it2 ->
                    val position = LatLng(it1, it2)
                    val marker = map.addMarker(MarkerOptions().position(position).title(place.title))
                    marker?.tag = place
                }
            }

        }
    }

}