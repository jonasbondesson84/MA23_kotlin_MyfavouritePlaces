package com.example.myfavouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myfavouriteplaces.databinding.FragmentFavouriteDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
//    private lateinit var tvTitle: TextView
//    private lateinit var tvDesc: TextView
//    private lateinit var tvReview: TextView
//    private lateinit var tvCategory: TextView
//    private lateinit var rbStar: RatingBar
//    private lateinit var btnLocation: ImageButton
    private var currentPlace: Place? = null
    private val args: FavouriteDetailFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFavouriteDetailBinding? = null

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
        //val view = inflater.inflate(R.layout.fragment_favourite_detail, container, false)
        _binding = FragmentFavouriteDetailBinding.inflate(inflater, container, false)
        db = Firebase.firestore
        auth = Firebase.auth
//        tvTitle = view.findViewById(R.id.tvDetailsTitle)
//        tvDesc = view.findViewById(R.id.tvDetailsDescription)
//        tvCategory = view.findViewById(R.id.tvDetailsCatecory)
//        tvReview = view.findViewById(R.id.tvDetailsReview)
//        rbStar = view.findViewById(R.id.rbDetailsStars)
//        btnLocation = view.findViewById(R.id.imbDetailsLocation)
        //val topBarFavourite = view.findViewById<MaterialToolbar>(R.id.topAppBarDetails)

        val placeID = args.placeID
        if (placeID != null) {
            getPlace(placeID)
        } else {
            Snackbar.make(binding.root, "Error, favourite place not found.", 2000).show()
        }

        hideAllElements()

        binding.imbDetailsLocation.setOnClickListener {
            if(currentPlace != null) {
                val lat = currentPlace!!.lat?.toFloat()
                val lng = currentPlace!!.lng?.toFloat()
                val action = lat?.let { it1 ->
                    lng?.let { it2->
                    FavouriteDetailFragmentDirections.actionFavouriteDetailFragmentToMapsFragment(
                        it1, it2
                    )
                }
            }
                if (action != null) {
                    findNavController().navigate(action)
                }
            }
        }

        binding.topAppBarDetails.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.topAppBarDetails.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.menuSavePlace -> {
                    true
                }
                R.id.menuEditFavourite -> {
                    true
                }
                R.id.menuDeleteFavourite -> {
                    showDeleteDialog()
                    true
                }
                else -> false
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            viewModel = sharedViewModel
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPlace(placeID: String) {
        currentPlace = sharedViewModel.getPlace()
        Log.d("!!!", currentPlace!!.description.toString())
        showElements(currentPlace!!)
//        for(place in currentUser.favouritesList) {
//            if(place.docID == placeID) {
//                currentPlace = place
//                showElements(place)
//            }
//        }
//        if(currentPlace == null) {
//            for(place in currentUser.sharedFavouritesList) {
//                if(place.docID == placeID) {
//                    currentPlace = place
//                    showElements(place)
//                }
//            }
//        }
    }

    private fun hideAllElements() {
//        tvDesc.visibility = View.INVISIBLE
//        tvCategory.visibility = View.INVISIBLE
//        tvReview.visibility = View.INVISIBLE
//        rbStar.visibility = View.INVISIBLE
    }

    private fun showElements(place: Place){
//        tvTitle.text = place.title
//
//        if (place.description != null) {
//            tvDesc.visibility = View.VISIBLE
//            tvDesc.text = place.description
//        }
//        if (place.category != null) {
//            tvCategory.text = place.category
//            tvCategory.visibility = View.VISIBLE
//        }
//        if(place.review != null) {
//            tvReview.text = place.review
//            tvReview.visibility = View.VISIBLE
//        }
//        if(place.stars != null) {
//            rbStar.visibility = View.VISIBLE
//            rbStar.rating = place.stars
//            Log.d("!!!", place.stars.toString())
//        }

    }

    private fun showDeleteDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.warning))
                .setMessage(resources.getString(R.string.deleteDesc))

                .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                    // Respond to negative button press

                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    // Respond to positive button press
                    deleteItem()
                }
                .show()
        }
    }

    private fun deleteItem() {
        currentPlace?.docID?.let {
            db.collection("users").document(currentUser.userID.toString()).collection(
                "favourites"
            ).document(it).delete().addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    findNavController().navigate(R.id.action_favouriteDetailFragment_to_favourites_fragment)
                    //(activity as MainActivity).switchFragment(FavouriteFragment())
                }
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
         * @return A new instance of fragment FavouriteDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}