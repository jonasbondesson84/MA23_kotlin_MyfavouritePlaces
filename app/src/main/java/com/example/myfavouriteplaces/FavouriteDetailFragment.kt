package com.example.myfavouriteplaces

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.myfavouriteplaces.databinding.FragmentFavouriteDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
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
    private var currentPlace: Place? = null
    private val args: FavouriteDetailFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFavouriteDetailBinding? = null
    private var isSavable = true
    private var isEditable = false

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



        val place = args
//        if (placeID != null) {
//            getPlace(placeID)
//        } else {
//            Snackbar.make(binding.root, "Error, favourite place not found.", 2000).show()
//        }
        currentPlace = sharedViewModel.getPlace()
        binding.tvFavouriteTitle.transitionName = sharedViewModel.title.value
        binding.tvFavouriteDescription.transitionName = sharedViewModel.description.value
        binding.imFavouriteImage.transitionName = sharedViewModel.imageURL.value

        binding.card.transitionName = place.placeID

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 1000
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(resources.getColor(R.color.md_theme_primary))
        }

        hideSaveIcon()

        getImage()
        setIcon()
        setAuthorDetails()

        binding.fabGoToMap.setOnClickListener {
            Log.d("!!!", currentPlace!!.lng.toString())
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
                Log.d("!!!", action.toString())
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
                    if(isSavable) {
                        saveShared()
                    }
                    true
                }
                R.id.menuEditFavourite -> {
                    if(isEditable) {
                        editPlace()
                    }
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

    private fun setAuthorDetails() {
        val authorID = sharedViewModel.author.value
        if (authorID != null) {
            db.collection("usersCollection").whereEqualTo("userID", authorID).get()
                .addOnSuccessListener {document->
                    val authorName = document.documents[0].data?.get("name").toString()
                    val authorImage = document.documents[0].data?.get("userImage").toString()

                    if(document != null) {
                        binding.tvAuthor.text = authorName
                        var requestOptions = RequestOptions()
                        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(50), Rotate(270))
                        Glide.with(this)
                            .load(authorImage)
                            .apply(requestOptions)
                            .placeholder(R.drawable.baseline_image_not_supported_24)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(binding.imAuthor)
//
                    }

                }
        }
    }

    private fun editPlace() {
        currentPlace?.let { sharedViewModel.setPlace(it) }
        findNavController().navigate(R.id.action_favouriteDetailFragment_to_addFavouritePartOneFragment)
    }

    private fun hideSaveIcon() {
            if(currentPlace?.author == currentUser.userID) {
                val icon = binding.topAppBarDetails.menu.getItem(0)
                icon.icon = resources.getDrawable(R.drawable.baseline_favorite_24)
                isSavable = false
                isEditable = true
            }
    }

    private fun saveShared() {
        val savePlace = sharedViewModel.getPlace()
        db.collection("users").document(currentUser.userID.toString()).collection("favourites").add(savePlace)
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {
                    Snackbar.make(binding.root, getString(R.string.savedPlace), 2000).show()
                    val icon = binding.topAppBarDetails.menu.getItem(0)
                    icon.icon = resources.getDrawable(R.drawable.baseline_favorite_24)
                } else {
                    Snackbar.make(binding.root, "Error", 2000).show()
                }
            }
    }

    private fun setIcon() {
        val iconsArray = resources.obtainTypedArray(R.array.categories_icons)
        val categoryArray = resources.getStringArray(R.array.categories_array)
        val categoryIndex = categoryArray.indexOf(sharedViewModel.category.value)
        if(categoryIndex != -1) {
            val icon = iconsArray.getResourceId(categoryIndex, -1)
            binding.imDetailsCategory.setImageResource(icon)
        } else {
            binding.imDetailsCategory.setImageResource(R.drawable.baseline_ballot_24)
        }

    }

    private fun getImage() {
        Glide.with(this)
            .load(currentPlace?.imageURL.toString())
            //.apply(requestOptions)
            .placeholder(R.drawable.baseline_image_not_supported_24)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(binding.imFavouriteImage)
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
        currentPlace.let { it->
            if (it != null) {
                if(it.reviewTitle.isNullOrEmpty()) {
                    binding.rbDetailsStars.visibility = View.INVISIBLE
                }

            } else {
                binding.rbDetailsStars.visibility= View.INVISIBLE
            }
        }

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
                    (activity as MainActivity).getUserFavourites()
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