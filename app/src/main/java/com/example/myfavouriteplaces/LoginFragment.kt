package com.example.myfavouriteplaces

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myfavouriteplaces.databinding.FragmentLoginBinding
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
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var _binding: FragmentLoginBinding? = null
    val binding get() = _binding!!


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val user = binding.etvUser.text.toString()
            val pw = binding.etvPassword.text.toString()
            binding.btnLogin.isEnabled = (user.isNotEmpty() && pw.isNotEmpty())
            binding.btnSignup.isEnabled = (user.isNotEmpty() && pw.isNotEmpty())
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        binding.etvUser.addTextChangedListener(textWatcher)
        binding.etvPassword.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            signIn(binding.root)
        }

        binding.btnSignup.setOnClickListener {
            signUp(binding.root)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun signIn(view: View) {
        val email = binding.etvUser.text.toString()
        val password = binding.etvPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.let { (activity as MainActivity).getUserDetails(it) }
                    findNavController().navigate(R.id.action_loginFragment_to_home_fragment)

                } else {
                    Snackbar.make(view, getText(R.string.errorLogIn), 2000).show()
                }
            }
    }

    private fun signUp(view: View) {
        val email = binding.etvUser.text.toString()
        val password = binding.etvPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.uid.let {
                        val user = User(userID = it)
                        db.collection("usersCollection").add(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    auth.currentUser?.let { user ->
                                        (activity as MainActivity).getUserDetails(user)
                                    }
                                    findNavController().navigate(R.id.action_loginFragment_to_account_fragment)

                                } else {
                                    Snackbar.make(view, getString(R.string.errorSignUp), 2000)
                                        .show()
                                }
                            }
                    }

                } else {
                    Snackbar.make(view, getText(R.string.errorSignUp), 2000).show()
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}