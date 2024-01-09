package com.example.myfavouriteplaces

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myfavouriteplaces.databinding.FragmentAddFavouritePartThreeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouritePartThreeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouritePartThreeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
//    private lateinit var etvReview: EditText
//    private lateinit var rbStars: RatingBar
    private var _binding: FragmentAddFavouritePartThreeBinding? = null
    val binding get() = _binding!!
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val title = binding.etvAddReview.text.toString()
            binding.btnAddPartThreeNext.isEnabled = title.isNotEmpty()
        }

        override fun afterTextChanged(s: Editable?) {}

    }

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
        //val view = inflater.inflate(R.layout.fragment_add_favourite_part_three, container, false)
        _binding = FragmentAddFavouritePartThreeBinding.inflate(inflater,container,false)
//        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAddPartThree)
//        val btnCancel = view.findViewById<Button>(R.id.btnAddPartThreeCancel)
//        val btnNext = view.findViewById<Button>(R.id.btnAddPartThreeNext)
//        val btnSkip = view.findViewById<Button>(R.id.btnAddSkipReview)
//        etvReview = view.findViewById(R.id.etvAddReview)
//        rbStars = view.findViewById(R.id.rbAddStars)
        binding.etvAddReview.addTextChangedListener(textWatcher)

        binding.topAddPartThree.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.rbAddStars.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            sharedViewModel.setStars(binding.rbAddStars.rating)
        }


        binding.btnAddPartThreeCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFaouritePartThreeFragment_to_favourites_fragment)
        }

        binding.btnAddPartThreeNext.setOnClickListener {
            if(binding.etvAddReview.text.isNotEmpty()) {
                sharedViewModel.setReview(binding.etvAddReview.text.toString())
                sharedViewModel.setReviewTitle(binding.etvAddReviewTitle.text.toString())
                sharedViewModel.setStars(binding.rbAddStars.rating)

                findNavController().navigate(R.id.action_addFaouritePartThreeFragment_to_addFavouriteSummaryFragment)
            }
        }
        binding.btnAddSkipReview.setOnClickListener {
            sharedViewModel.resetReview()
            findNavController().navigate(R.id.action_addFaouritePartThreeFragment_to_addFavouriteSummaryFragment)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFaouritePartThreeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFavouritePartThreeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}