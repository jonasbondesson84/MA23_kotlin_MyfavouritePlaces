package com.example.myfavouriteplaces

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

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
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnContinue: Button
    private lateinit var etvUser: EditText
    private lateinit var etvPassword: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnSignUp = view.findViewById(R.id.btnSignup)
        btnContinue = view.findViewById(R.id.btnContinueWithoutLogin)
        etvUser = view.findViewById(R.id.etvUser)
        etvPassword = view.findViewById(R.id.etvPassword)
        auth = Firebase.auth
        db = Firebase.firestore

        btnLogin.setOnClickListener {
            signIn(view)
        }

        btnSignUp.setOnClickListener {
            signUp(view)
        }

        return view
    }


    private fun signIn(view: View) {
        val email = etvUser.text.toString()
        val password = etvPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getCurrentUserInfo()
                    findNavController().navigate(R.id.action_loginFragment_to_home_fragment)
//                    (activity as MainActivity).switchFragment(StartFragment())
                } else {
                    Snackbar.make(view, getText(R.string.errorLogIn), 2000).show()
                }
            }
    }

    private fun signUp(view: View) {
        val email = etvUser.text.toString()
        val password = etvPassword.text.toString()
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
                                    getCurrentUserInfo()
                                    findNavController().navigate(R.id.action_loginFragment_to_account_fragment)
                                    //(activity as MainActivity).switchFragment(StartFragment())
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

    private fun getCurrentUserInfo() {
        val user = auth.currentUser
        if(user != null) {
            db.collection("usersCollection").get().addOnSuccessListener {DocumentSnapshot ->
                for(document in DocumentSnapshot) {
                    if(document.get("userID").toString() == user.uid) {
                        Log.d("!!!", "Got it!")
                        val user = document.toObject<User>()
                        currentUser.name = user.name
                        currentUser.userID = user.userID
                        currentUser.userImage = user.userImage
                        currentUser.location = user.location
                        currentUser.documentId = user.documentId
                        Log.d("!!!", currentUser.documentId.toString())
                        return@addOnSuccessListener
                    }
                }
                Log.d("!!!", "user not found!")
            }
        }


        //currentUser.name =

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