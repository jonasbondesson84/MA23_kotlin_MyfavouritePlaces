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

//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        val sydney = LatLng(-34.0, 151.0)
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
    private val args: MapsFragmentArgs by navArgs()
    private var lat: Double? = null
    private var lng: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        lat = args.lat.toDouble()
        lng = args.lng.toDouble()



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
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