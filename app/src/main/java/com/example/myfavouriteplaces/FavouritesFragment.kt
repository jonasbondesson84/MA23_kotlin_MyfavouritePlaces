package com.example.myfavouriteplaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfavouriteplaces.databinding.FragmentFavouritesBinding
import com.google.android.material.transition.MaterialSharedAxis
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
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var _binding: FragmentFavouritesBinding? = null
    val binding get() = _binding!!


    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: FavouritesAdapter

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
        //val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        db = Firebase.firestore
        auth = Firebase.auth


        postponeEnterTransition()
        binding.root.doOnPreDraw { startPostponedEnterTransition() }

        binding.rvFavourites.layoutManager = GridLayoutManager(binding.root.context, 2)
        adapter = FavouritesAdapter(binding.root.context, CurrentUser.favouritesList)
        binding.rvFavourites.adapter = adapter

        sharedViewModel.resetValues()
        showFAB()

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 1000
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 1000
        }

        adapter.onCardClick = { place, card ->
            val placeID = place.docID
            sharedViewModel.setPlace(place)
            val action =
                FavouritesFragmentDirections.actionFavouritesFragmentToFavouriteDetailFragment(
                    placeID
                )
            if (placeID != null) {
                val extra = FragmentNavigatorExtras(card to placeID)
                findNavController().navigate(action, extra)
            }
        }

        binding.fabAddFavourite.setOnClickListener {
            sharedViewModel.resetValues()
            val extras = FragmentNavigatorExtras(binding.fabAddFavourite to "fabToScreen")
            findNavController().navigate(
                R.id.action_favourites_fragment_to_addFavouritePartOneFragment,
                null,
                null,
                extras
            )
        }

        return binding.root
    }

    private fun showFAB() {
        if (CurrentUser.userID == null) {
            binding.fabAddFavourite.visibility = View.INVISIBLE
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

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}