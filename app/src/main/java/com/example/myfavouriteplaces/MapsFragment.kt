package com.example.myfavouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myfavouriteplaces.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.transition.MaterialSharedAxis

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val args: MapsFragmentArgs by navArgs()
    private var lat: Double? = null
    private var lng: Double? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentMapsBinding? = null
    val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 1000
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 1000
        }

        getLatLng()

        binding.topAppMaps.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getLatLng() {
        lat = args.lat.toDouble()
        lng = args.lng.toDouble()
        if (lat == 0.0 || lat == null) {
            lat = CurrentUser.latLng?.latitude
            lng = CurrentUser.latLng?.longitude
            if (lat == null) {
                lat = 59.334591
                lng = 18.063240
            }

        }

    }


    override fun onMapReady(map: GoogleMap) {
        val adapter = PlacesInfoAdapter(requireContext())
        map.setInfoWindowAdapter(adapter)

        createMarksShared(map)
        createMarksFavourites(map)

        if (lat != null && lng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lng!!), 15f))

        } else {
            val sthlm = LatLng(59.334591, 18.063240)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sthlm, 15f))
        }
        map.setOnInfoWindowClickListener {
            val place = it.tag as? Place
            val placeID = place?.docID
            if (place != null) {
                sharedViewModel.setPlace(place)
            }
            val action = MapsFragmentDirections.actionMapsFragmentToFavouriteDetailFragment(placeID)
            if (placeID != null) {
                findNavController().navigate(action)
            }
        }
    }

    private fun createMarksFavourites(map: GoogleMap) {
        for (place in CurrentUser.favouritesList) {
            place.lat?.let { it1 ->
                place.lng?.let { it2 ->
                    val position = LatLng(it1, it2)
                    val marker =
                        map.addMarker(MarkerOptions().position(position).title(place.title))
                    marker?.tag = place
                }
            }

        }
    }

    private fun createMarksShared(map: GoogleMap) {

        for (place in CurrentUser.sharedFavouritesList) {
            place.lat?.let { it1 ->
                place.lng?.let { it2 ->
                    val position = LatLng(it1, it2)
                    val marker = map.addMarker(
                        MarkerOptions().position(position).title(place.title).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )
                    )
                    marker?.tag = place
                }
            }

        }
    }

}