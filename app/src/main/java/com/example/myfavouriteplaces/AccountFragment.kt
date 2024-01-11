package com.example.myfavouriteplaces

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.myfavouriteplaces.databinding.FragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
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
    private var imageBitmap: Bitmap? = null
    private lateinit var storage: FirebaseStorage
    private var showLoadingIcon = false
    private var IMAGE_REQUEST_CODE = 1
    private var _binding: FragmentAccountBinding? = null
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

        _binding = FragmentAccountBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        //Sets the transition to and from the view.
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 1000
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 1000
        }

        enableButtons()
        setUserUi()


        binding.fabAddImageAccount.setOnClickListener {
            if (!showLoadingIcon) {
                openCamera()
            }
        }

        binding.btnSave.setOnClickListener {
            if (!showLoadingIcon) {
                saveImage(binding.root)
            }
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            if(!showLoadingIcon) {
                menuClickResult(menuItem)
            } else {
                false
            }
        }
        return binding.root
    }

    private fun menuClickResult(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menuLogoutAccount -> {
                signOut()
                return true
            }
            R.id.menuDeleteAccount -> {
                    showDeleteDialog(binding.root)
                return true
            }
            else ->  return false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUserUi() {
        if (CurrentUser.name != null) {
            binding.etcAccountName.setText(CurrentUser.name)
        }
        if (CurrentUser.location != null) {
            binding.etvAccountLocation.setText(CurrentUser.location)
        }
        if (CurrentUser.userImage != null) {
            Glide
                .with(this)
                .load(CurrentUser.userImage)
                .placeholder(R.drawable.baseline_image_not_supported_24)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.imAccountImage)
        }
    }

    private fun saveImage(view: View) {
        disableButtons()
        if (imageBitmap != null) {
            val storage = Firebase.storage
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("images").child(fileName)

            val baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        saveData(downloadUrl, view)
                    }
                } else {
                    // Image upload failed
                    Snackbar.make(view, getString(R.string.errorImageUpload), 2000).show()

                    enableButtons()
                }
            }
        } else {
            saveData(CurrentUser.userImage, view)
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
                    deleteFromUsersCollection(view)
                }
                .show()
        }
    }

    private fun deleteAccount(view: View) {
        disableButtons()
        val user = Firebase.auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(view, getString(R.string.accountDeleted), 2000).show()
                signOut()
            }
        }
    }

    private fun deleteFromUsersCollection(view: View) {
        val db = Firebase.firestore
        db.collection("usersCollection").document(CurrentUser.documentId.toString()).delete()
            .addOnSuccessListener {
                deleteUsersFavourites(view)
            }
    }

    private fun deleteUsersFavourites(view: View) {
        val db = Firebase.firestore
        for (place in CurrentUser.favouritesList) {
            val docID = place.docID
            if (docID != null) {
                db.collection("users").document(CurrentUser.userID.toString())
                    .collection("favourites").document(docID).delete()
                    .addOnSuccessListener {
                        db.collection("users").document(CurrentUser.userID.toString()).delete()
                            .addOnSuccessListener {
                                Snackbar.make(view, getString(R.string.userCollectionDeleted), 2000)
                                    .show()
                                deleteAccount(view)
                            }
                    }
            }
        }
    }

    private fun saveData(imageURL: String?, view: View) {
        val name = binding.etcAccountName.text.toString()
        val location = binding.etvAccountLocation.text.toString()
        if (CurrentUser.userID != null) {
            db.collection("usersCollection").document(CurrentUser.documentId.toString())
                .update(
                    "name", name,
                    "location", location,
                    "userImage", imageURL
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(view, getString(R.string.accountSaved), 2000).show()
                        CurrentUser.updateUser(name, location, imageURL)
                        enableButtons()
                    }
                }
        }
    }

    private fun disableButtons() {
        binding.btnSave.isEnabled = false
        showLoadingIcon = true
        binding.pbSaveAccount.visibility = View.VISIBLE
    }

    private fun enableButtons() {
        binding.btnSave.isEnabled = true
        showLoadingIcon = false
        binding.pbSaveAccount.visibility = View.INVISIBLE
    }

    private fun signOut() {
        enableButtons()
        CurrentUser.resetUser()
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
                }
            }
        } else {
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
            binding.imAccountImage.setImageBitmap(imageBitmap)

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