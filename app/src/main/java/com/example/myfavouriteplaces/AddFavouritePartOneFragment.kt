package com.example.myfavouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar

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
    private lateinit var spCategory: Spinner
    private lateinit var btnNext: Button
    private lateinit var btnCancel: Button
    private lateinit var etvTitle: EditText
    private lateinit var etvDesc: EditText

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
        val view = inflater.inflate(R.layout.fragment_add_favourite_part_one, container, false)
        spCategory = view.findViewById(R.id.spCategory)
        btnNext = view.findViewById(R.id.btnAddPartOneNext)
        btnCancel = view.findViewById(R.id.btnAddPartOneCancel)
        etvTitle = view.findViewById(R.id.etvAddTitle)
        etvDesc = view.findViewById(R.id.etvAddDescription)


        btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFavouritePartOneFragment_to_favourites_fragment)
        }
        btnNext.setOnClickListener {
            if (etvTitle.text.isNotEmpty()) {
                sharedViewModel.setTitle(etvTitle.text.toString())
                sharedViewModel.setDescription(etvDesc.text.toString())
                findNavController().navigate(R.id.action_addFavouritePartOneFragment_to_addFavouritePartTwoFragment)
            }
        }

        createSpinner()

        val topAddFavBar = view.findViewById<MaterialToolbar>(R.id.topAddPart1)

        topAddFavBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        spCategory.onItemSelectedListener = this
        return view
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
            spCategory.adapter = adapter
        }
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