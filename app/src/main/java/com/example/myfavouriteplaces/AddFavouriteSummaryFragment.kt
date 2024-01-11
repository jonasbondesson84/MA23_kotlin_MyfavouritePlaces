package com.example.myfavouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myfavouriteplaces.databinding.FragmentAddFavouriteSummaryBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

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
    private lateinit var googleMap: GoogleMap
    private lateinit var storage: FirebaseStorage
    private var showLoadingIcon = false
    private lateinit var db: FirebaseFirestore

    private var _binding: FragmentAddFavouriteSummaryBinding? = null
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
        _binding = FragmentAddFavouriteSummaryBinding.inflate(inflater,container,false)
        storage = Firebase.storage
        db = Firebase.firestore

        setIcon()
        getImageIfEdited()
        hideElements()
        enableButtons()

        binding.summaryMap.onCreate(savedInstanceState)

        binding.summaryMap.getMapAsync {googleMap ->
            setMap(googleMap)
        }

        binding.topSummary.setNavigationOnClickListener {
            if(!showLoadingIcon) {
                findNavController().navigateUp()
            }
        }

        binding.btnSummaryCancel.setOnClickListener {
            if(!showLoadingIcon) {
                findNavController().navigate(R.id.action_addFavouriteSummaryFragment_to_favourites_fragment)
            }
        }

        binding.btnSummarySave.setOnClickListener {
            if(!showLoadingIcon) {
                saveImage(binding.root)
            }
        }

        return binding.root
    }

    private fun setMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val latLng = sharedViewModel.lat.value?.let { sharedViewModel.lng.value?.let { it1 ->
            LatLng(it, it1)
        } }
        val cameraUpdate = latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }
        if (cameraUpdate != null) {
            googleMap.moveCamera(cameraUpdate)
            googleMap.addMarker(MarkerOptions().position(latLng))
        }
    }

    private fun getImageIfEdited() {
        if(sharedViewModel.imageUri.value == null) {
            Glide
                .with(this)
                .load(sharedViewModel.imageURL.value)
                .placeholder(R.drawable.baseline_image_not_supported_24)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imSummaryImage)
        }
    }


    private fun hideElements() {
        if(sharedViewModel.imageUri.value == null) {
            binding.imSummaryImage.setImageResource(R.drawable.border)
        }
        if(sharedViewModel.stars.value == null && sharedViewModel.review.value == null) {
            binding.dividerThree.visibility = View.INVISIBLE
        }
        if(sharedViewModel.stars.value == null) {
            binding.rbSummaryStars.visibility = View.INVISIBLE
        }
        if(sharedViewModel.review.value == null) {
            binding.tvSummaryReview.visibility = View.INVISIBLE
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
        _binding = null
    }

    private fun setIcon() {
        val iconsArray = resources.obtainTypedArray(R.array.categories_icons)
        val categoryArray = resources.getStringArray(R.array.categories_array)
        val categoryIndex = categoryArray.indexOf(sharedViewModel.category.value)
        if(categoryIndex != -1) {
            val icon = iconsArray.getResourceId(categoryIndex, -1)
            binding.imCategory.setImageResource(icon)
        } else {
            binding.imCategory.setImageResource(R.drawable.baseline_ballot_24)
        }
        iconsArray.recycle()
    }

    private fun saveImage(view:View) {
        disableButtons()
        if(sharedViewModel.imageUri.value != null) { //If you have selected an image, if first saves the image to firebase.storage, then saves the post in firebase
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val filePath = sharedViewModel.imageUri.value
            val storageRef = storage.reference.child("images").child(fileName)

            filePath?.let { storageRef.putFile(it) }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        sharedViewModel.setImageURL(downloadUrl)
                        savePlaceInfo(view)
                    }
                } else {
                    // Image upload failed
                    Snackbar.make(view, getString(R.string.errorImageUpload), 2000).show()
                    enableButtons()
                }
            }
        } else { //if you haven't selected an image you just saves the place to firebase
            savePlaceInfo(view)
        }
    }

    private fun savePlaceInfo(view: View) {
        val place = Place(
            title = sharedViewModel.title.value,
            description = sharedViewModel.description.value,
            category = sharedViewModel.category.value,
            stars = sharedViewModel.stars.value,
            review = sharedViewModel.review.value,
            public = sharedViewModel.sharePublic.value,
            lat = sharedViewModel.lat.value,
            lng = sharedViewModel.lng.value,
            author = CurrentUser.userID,
            imageURL = sharedViewModel.imageURL.value,
            reviewTitle = sharedViewModel.reviewTitle.value
        )
        if(sharedViewModel.docID.value != null) { // if it has a docID, you edit existing post
            editPost(view, place)
        } else {  //if it is a new post, it saves a new
            savePlace(view, place)
        }
    }

    private fun savePlace(view: View, place: Place) {
        db.collection("users").document(CurrentUser.userID.toString()).collection("favourites").add(place)
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    (activity as MainActivity).getUserFavourites()
                    enableButtons()
                    findNavController().navigate(R.id.action_addFavouriteSummaryFragment_to_favourites_fragment)
                } else {
                    Snackbar.make(view, "Error", 2000).show()
                }
            }
    }

    private fun editPost(view: View, place: Place) {

        sharedViewModel.docID.value?.let {
            db.collection("users").document(CurrentUser.userID.toString()).collection("favourites").document(
                it
            ).update("title", place.title,
                "description", place.description,
                "category", place.category,
                "stars", place.stars,
                "review", place.review,
                "public", place.public,
                "lat", place.lat,
                "lng", place.lng,
                "imageURL", place.imageURL,
                "reviewTitle", place.reviewTitle)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        (activity as MainActivity).getUserFavourites()
                        enableButtons()
                        findNavController().navigate(R.id.action_addFavouriteSummaryFragment_to_favourites_fragment)
                    } else {
                        Snackbar.make(view, "Error", 2000).show()
                    }
                }
        }
    }

    private fun disableButtons() {
        showLoadingIcon = true
        binding.pbSaveFavourite.visibility = View.VISIBLE
        binding.btnSummarySave.isEnabled = false
        binding.btnSummaryCancel.isEnabled = false
    }

    private fun enableButtons() {
        showLoadingIcon = false
        binding.pbSaveFavourite.visibility = View.INVISIBLE
        binding.btnSummarySave.isEnabled = true
        binding.btnSummaryCancel.isEnabled = true
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