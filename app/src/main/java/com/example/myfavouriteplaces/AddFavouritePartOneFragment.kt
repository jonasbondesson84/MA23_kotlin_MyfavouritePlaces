package com.example.myfavouriteplaces

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myfavouriteplaces.databinding.FragmentAddFavouritePartOneBinding
import com.google.android.material.transition.MaterialContainerTransform

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AddFavouritePartOneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFavouritePartOneFragment : Fragment(), AdapterView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val textWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val title = binding.etvAddTitle.text.toString()
        binding.btnAddPartOneNext.isEnabled = title.isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) {}

}

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        Log.d("!!!", "uri = $uri")
        binding.imSelectedImage.setImageURI(uri)
        if (uri != null) {
            sharedViewModel.setImageUri(uri)
        }
    }
    private var _binding: FragmentAddFavouritePartOneBinding? = null
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
        //val view = inflater.inflate(R.layout.fragment_add_favourite_part_one, container, false)
        _binding = FragmentAddFavouritePartOneBinding.inflate(inflater,container,false)

        binding.etvAddTitle.addTextChangedListener(textWatcher)


        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.fabAddFavourite)
            endView = view
            duration = 1000
            scrimColor = Color.TRANSPARENT
            containerColor = resources.getColor(R.color.md_theme_primary)
            startContainerColor = resources.getColor(R.color.md_theme_secondary)
            endContainerColor = resources.getColor(R.color.md_theme_surface)
        }

        returnTransition = Slide().apply {
            duration = 500
            addTarget(R.id.cardAddFavourite)
        }

        binding.btnAddPartOneCancel.setOnClickListener {
            findNavController().navigateUp()
//            findNavController().navigate(R.id.action_addFavouritePartOneFragment_to_favourites_fragment)
        }
        binding.btnAddPartOneNext.setOnClickListener {
            if (binding.etvAddTitle.text.isNotEmpty()) {
                sharedViewModel.setTitle(binding.etvAddTitle.text.toString())
                sharedViewModel.setDescription(binding.etvAddDescription.text.toString())
                findNavController().navigate(R.id.action_addFavouritePartOneFragment_to_addFavouritePartTwoFragment)
            }
        }
        binding.fabAddImage.setOnClickListener {
            getContent.launch("image/*")
        }

        createSpinner()
        getImageIfEdit()


        binding.topAddPart1.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.spCategory.onItemSelectedListener = this
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
         * @return A new instance of fragment AddFavouritePartOneFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFavouritePartOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun createSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            binding.spCategory.adapter = adapter
            if(sharedViewModel.category.value != null) {
                binding.spCategory.setSelection(adapter.getPosition(sharedViewModel.category.value))
            }
        }
    }
    private fun getImageIfEdit() {
        if(sharedViewModel.imageURL.value != null) {
            Glide
                .with(this)
                .load(sharedViewModel.imageURL.value)
                .placeholder(R.drawable.baseline_image_not_supported_24)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imSelectedImage)
        }
    }

    private fun setSpinnerIfEdit() {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            sharedViewModel.setCategory(parent.getItemAtPosition(position).toString())
            Log.d("!!!", sharedViewModel.category.value.toString())
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}