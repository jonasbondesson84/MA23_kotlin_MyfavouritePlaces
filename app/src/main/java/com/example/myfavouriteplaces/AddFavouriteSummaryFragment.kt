package com.example.myfavouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouriteSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouriteSummaryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

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
        val view = inflater.inflate(R.layout.fragment_add_favourite_summary, container, false)

        mapView = view.findViewById(R.id.summaryMap)
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topSummary)
        btnSave = view.findViewById(R.id.btnSummarySave)
        btnCancel = view.findViewById(R.id.btnSummaryCancel)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {googleMap ->
            this.googleMap = googleMap
            val latLng = sharedViewModel.lat.value?.let { sharedViewModel.lng.value?.let { it1 ->
                LatLng(it,
                    it1
                )

            } }
            Log.d("!!!", latLng.toString())
            val cameraUpdate = latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
            if (cameraUpdate != null) {
                googleMap.moveCamera(cameraUpdate)
                googleMap.addMarker(MarkerOptions().position(latLng))
            }

            }

        topAppBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFavouriteSummaryFragment_to_favourites_fragment)
        }

        btnSave.setOnClickListener {
            savePlace(view)

        }

        return view
    }

    private fun savePlace(view: View) {
        val db = Firebase.firestore
        val place = Place(
            title = sharedViewModel.title.value,
            description = sharedViewModel.description.value,
            category = sharedViewModel.category.value,
            stars = sharedViewModel.stars.value,
            review = sharedViewModel.review.value,
            public = sharedViewModel.sharePublic.value,
            lat = sharedViewModel.lat.value,
            lng = sharedViewModel.lng.value,
            author = currentUser.userID


        )
        db.collection("users").document(currentUser.userID.toString()).collection("favourites").add(place)
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    findNavController().navigate(R.id.action_addFavouriteSummaryFragment_to_favourites_fragment)
                    //(activity as MainActivity).switchFragment(FavouriteFragment())
                } else {
                    Snackbar.make(view, "Error", 2000).show()
                }
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFavouriteSummaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFavouriteSummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}