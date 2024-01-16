package com.example.myfavouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myfavouriteplaces.databinding.FragmentAddFavouritePartTwoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


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
    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentAddFavouritePartTwoBinding? = null
    val binding get() = _binding!!


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
        _binding = FragmentAddFavouritePartTwoBinding.inflate(inflater, container, false)

        binding.addLocationMap.onCreate(savedInstanceState)
        binding.addLocationMap.getMapAsync { googleMap ->
            setMap(googleMap)
        }

        binding.topAddPartTwo.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddPartTwoCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFavouritePartTwoFragment_to_favourites_fragment)
        }

        binding.btnAddPartTwoNext.setOnClickListener {
            if (sharedViewModel.lat.value != null) {
                sharedViewModel.setSharePublic(binding.swPublic.isChecked)
                findNavController().navigate(R.id.action_addFavouritePartTwoFragment_to_addFaouritePartThreeFragment)
            }
        }

        setSwitchIfEdit()
        return binding.root
    }

    private fun setMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        var latLng = CurrentUser.latLng
        if (latLng == null) {
            latLng = LatLng(
                59.334591,
                18.063240
            )  //If you cant get latlng from user, it sets to stockholm
        }
        var marker: Marker? = null
        if (sharedViewModel.lat.value != null && sharedViewModel.lng.value != null) {
            latLng = LatLng(sharedViewModel.lat.value!!, sharedViewModel.lng.value!!)
            marker = googleMap.addMarker(MarkerOptions().position(latLng))
            binding.btnAddPartTwoNext.isEnabled = true
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        googleMap.moveCamera(cameraUpdate)

        googleMap.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = googleMap.addMarker(MarkerOptions().position(latLng))
            sharedViewModel.setLocation(latLng)
            binding.btnAddPartTwoNext.isEnabled = true

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.addLocationMap.onDestroy()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        binding.addLocationMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.addLocationMap.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.addLocationMap.onLowMemory()
    }

    private fun setSwitchIfEdit() {
        if (sharedViewModel.sharePublic.value != null)
            binding.swPublic.isChecked = sharedViewModel.sharePublic.value!!
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