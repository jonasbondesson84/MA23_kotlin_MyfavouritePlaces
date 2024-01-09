package com.example.myfavouriteplaces

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var etvName: EditText
    private lateinit var etvLocation: EditText
    private lateinit var btnSave: Button
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private lateinit var fabAddImage: FloatingActionButton
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage

    private var IMAGE_REQUEST_CODE = 1

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri
        imageUri = uri
        Log.d("!!!", "uri = $uri")
        imAccount.setImageURI(uri)
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
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        auth = Firebase.auth
        db = Firebase.firestore
        btnSave = view.findViewById(R.id.btnSave)
        etvLocation = view.findViewById(R.id.etvAccountLocation)
        etvName = view.findViewById(R.id.etcAccountName)
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        tvAccount = view.findViewById(R.id.tvAccount)
        tvAccount.text = auth.currentUser?.email
        imAccount = view.findViewById(R.id.imAccountImage)
        fabAddImage = view.findViewById(R.id.fabAddImageAccount)
        storage = Firebase.storage

        setUserUi()


        fabAddImage.setOnClickListener {
            //getContent.launch("image/*")
            openCamera()
        }

        btnSave.setOnClickListener {
            saveImage(view)
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogoutAccount -> {
                    signOut()
                    true
                }

                R.id.menuDeleteAccount -> {
                    showDeleteDialog(view)

                    true
                }

                else -> false
            }
        }


        return view
    }

    private fun setUserUi() {
        if (currentUser.name != null) {
            etvName.setText(currentUser.name)
        }
        if (currentUser.location != null) {
            etvLocation.setText(currentUser.location)
        }
        Log.d("!!!", "userimage: ${currentUser.userImage.toString()}")
        if(currentUser.userImage != null) {
            Glide
                .with(this)
                .load(currentUser.userImage)
                .placeholder(R.drawable.baseline_image_not_supported_24)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imAccount)
        }
    }

    private fun saveImage(view: View) {
        if(imageBitmap != null) {
            val storage = Firebase.storage
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("images").child(fileName)
            // Convert the Bitmap to a byte array
            val baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Upload the image to Firebase Storage
            val uploadTask = storageRef.putBytes(data)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Log.d("MainActivity", "Download URL: $downloadUrl")


                        Toast.makeText(
                            requireContext(),
                            "Image uploaded successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        saveData(downloadUrl)
                        // You can save the downloadUrl or use it to display the image later
                    }
                } else {
                    // Image upload failed
                    Log.d("!!!", "failed")
                    val exception = task.exception
                    // Handle the exception
                }
            }
        } else {
            saveData(currentUser.userImage)
        }
    }


    private fun showDeleteDialog(view: View) {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.warning))
                .setMessage(resources.getString(R.string.deleteAccountDesc))

                .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                    // Respond to negative button press

                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    // Respond to positive button press
                    deleteAccount(view)
                }
                .show()
        }
    }

    private fun deleteAccount(view: View) {

        val user = Firebase.auth.currentUser
        Log.d("!!!", user?.uid.toString())
        Log.d("!!!", currentUser.userID.toString())
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Snackbar.make(view, getString(R.string.accountDeleted), 2000).show()
                deleteFromUsersCollection(view)
            }
        }

    }

    private fun deleteFromUsersCollection(view: View) {
        val db = Firebase.firestore
        Log.d("!!!", currentUser.documentId.toString())
        db.collection("usersCollection").document(currentUser.documentId.toString()).delete()
            .addOnSuccessListener {
                deleteUsersFavourites(view)
            }
//        for (place in currentUser.favouritesList) {
//            val docID = place.docID
//            if (docID != null) {
//                db.collection("usersCollection").document(currentUser.userID.toString())
//                    .collection("favourites").document(docID).delete()
//                    .addOnSuccessListener {
//                        db.collection("users").document(currentUser.userID.toString()).delete()
//                            .addOnSuccessListener {
//                                Snackbar.make(view, getString(R.string.userCollectionDeleted), 2000)
//                                    .show()
//                                signOut()
//                            }
//
//                    }
//            }
//        }
    }

    private fun deleteUsersFavourites(view: View) {
        val db = Firebase.firestore
        Log.d("!!!", currentUser.userID.toString())
        for (place in currentUser.favouritesList) {
            val docID = place.docID
            if (docID != null) {
                db.collection("users").document(currentUser.userID.toString())
                    .collection("favourites").document(docID).delete()
                    .addOnSuccessListener {
                        db.collection("users").document(currentUser.userID.toString()).delete()
                            .addOnSuccessListener {
                                Snackbar.make(view, getString(R.string.userCollectionDeleted), 2000)
                                    .show()
                                signOut()
                            }

                    }
            }
        }

    }

    private fun saveData(imageURL: String?) {
        val name = etvName.text.toString()
        val location = etvLocation.text.toString()
        if (currentUser.userID != null) {
            db.collection("usersCollection").document(currentUser.documentId.toString())
                .update("name", name,
                    "location", location,
                    "userImage", imageURL)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("!!!", "got here")
                        Toast.makeText(requireContext(), getString(R.string.accountSaved), Toast.LENGTH_SHORT).show()
                        currentUser.updateUser(name, location, imageURL)

                    //                        Snackbar.make(view, getString(R.string.accountSaved), 2000).show()
                    }

                }
        }
    }

    private fun signOut() {
        currentUser.resetUser()
        auth.signOut()
        findNavController().navigate(R.id.loginFragment)
    }

    private fun openCamera() {

        if (checkPermissionsCamera()) {
            if (isCameraPermissionEnabled()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE)
                } catch (e: ActivityNotFoundException) {
                    // display error state to the user
                }}
        }
        else{
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            (activity as MainActivity),
            arrayOf(android.Manifest.permission.CAMERA),
            1
        )
    }

    private fun isCameraPermissionEnabled(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionsCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            imageBitmap = data?.extras?.get("data") as Bitmap

            imAccount.setImageBitmap(imageBitmap)

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}