package com.example.myfavouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouritePartTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouritePartTwoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var tvTitle: TextView
    private lateinit var tvLatLng: TextView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var btnNext: Button
    private lateinit var btnCancel: Button
    private lateinit var swPublic: SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_favourite_part_two, container, false)

        mapView = view.findViewById(R.id.addLocationMap)
        tvTitle = view.findViewById(R.id.addPartTwoTitle)
        tvLatLng = view.findViewById(R.id.tvLatLng)
        topAppBar = view.findViewById(R.id.topAddPartTwo)
        btnCancel = view.findViewById(R.id.btnAddPartTwoCancel)
        btnNext = view.findViewById(R.id.btnAddPartTwoNext)
        swPublic = view.findViewById(R.id.swPublic)

        tvTitle.text = sharedViewModel.title.value.toString()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {googleMap ->
            this.googleMap = googleMap
            var latLng = currentUser.latLng
            if( latLng == null) {
                latLng = LatLng(59.334591, 18.063240)
            }
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap.moveCamera(cameraUpdate)
            var marker: Marker? = null

            googleMap.setOnMapClickListener {latLng ->
                marker?.remove()
                marker = googleMap.addMarker(MarkerOptions().position(latLng))
                sharedViewModel.setLocation(latLng)
                val text =" Latitude: ${sharedViewModel.lat.value}  \n Longitude: ${sharedViewModel.lat.value}"
                tvLatLng.text = text


            }
        }

        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFavouritePartTwoFragment_to_favourites_fragment)
        }

        btnNext.setOnClickListener {
            if(sharedViewModel.lat.value != null) {
                sharedViewModel.setSharePublic(swPublic.isChecked)
                findNavController().navigate(R.id.action_addFavouritePartTwoFragment_to_addFaouritePartThreeFragment)
            }
        }




        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFavouritePartTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFavouritePartTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}