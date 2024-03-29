package com.example.myfavouriteplaces

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_LOCATION = 1
    private var lat: Double? = null
    private var lng: Double? = null
    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        auth = Firebase.auth
        db = Firebase.firestore
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(2000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
            }
        }
        checkIfLocationGrated()
        checkIfUserIsLoggedIn()
        NavigationUI.setupWithNavController(
            bottomNav, navController
        )

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_fragment -> {
                    setNavTransition()
                    navController.navigate(R.id.home_fragment)
                    true
                }

                R.id.favourites_fragment -> {
                    setNavTransition()
                    navController.navigate(R.id.favourites_fragment)
                    true
                }

                R.id.maps_fragment -> {
                    setNavTransition()
                    //moveToMaps()
                    navController.navigate(R.id.maps_fragment)
                    true
                }

                R.id.account_fragment -> {
                    setNavTransition()
                    if (auth.currentUser == null) {
                        navController.navigate(R.id.loginFragment)
                        true
                    } else {
                        navController.navigate(R.id.account_fragment)
                        true
                    }
                }

                else -> false
            }
        }
    }


    private fun setNavTransition() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                duration = 1000
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                duration = 1000
            }
        }
    }

    private fun checkIfLocationGrated() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        }
    }

    private fun checkIfUserIsLoggedIn() {
        val user = auth.currentUser
        if (user != null) {
            getUserDetails(user)
        }
        getLastLocation()
    }

    fun getUserDetails(user: FirebaseUser) {
        db.collection("usersCollection").get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot) {
                if (document.get("userID").toString() == user.uid) {
                    Log.d("!!!", "Got it!")
                    val newUser = document.toObject<User>()
                    CurrentUser.name = newUser.name
                    CurrentUser.userID = newUser.userID
                    CurrentUser.userImage = newUser.userImage
                    CurrentUser.location = newUser.location
                    CurrentUser.documentId = newUser.documentId
                    Log.d("!!!", CurrentUser.documentId.toString())
                    getUserFavourites()
                    getSharedFavourites()
                    return@addOnSuccessListener
                }
            }
        }

    }

    fun getUserFavourites() {
        CurrentUser.favouritesList.clear()
        if (CurrentUser.userID == null) {
            return
        } else {
            db.collection("users").document(CurrentUser.userID.toString()).collection("favourites")
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    for (document in documentSnapshot.documents) {
                        val place = document.toObject<Place>()
                        if (place != null) {
                            CurrentUser.favouritesList.add(place)
                        }
                    }
                }
        }
    }

    private fun getSharedFavourites() {
        db.collection("usersCollection").get().addOnSuccessListener { documentSnapshot ->
            for (document in documentSnapshot) {
                val user = document.get("userID").toString()
                if (user != null) {
                    db.collection("users").document(user).collection("favourites").get()
                        .addOnSuccessListener {
                            for (document in it) {
                                val place = document.toObject<Place>()
                                if (place != null && place.public == true && place.author != CurrentUser.userID) {
                                    CurrentUser.sharedFavouritesList.add(place)
                                    Log.d("!!!", "place: ${place.docID}")
                                }
                            }
                        }
                }
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationProvider.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) {
                        lat = location.latitude
                        lng = location.longitude
                    } else {
                        val sthlm = LatLng(59.334591, 18.063240)
                        lat = sthlm.latitude
                        lng = sthlm.longitude
                    }
                    CurrentUser.latLng = LatLng(lat!!, lng!!)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

}