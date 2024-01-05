package com.example.myfavouriteplaces

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth

    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        auth = Firebase.auth
        db = Firebase.firestore

        NavigationUI.setupWithNavController(
            bottomNav, navController
        )

        bottomNav.setOnNavigationItemSelectedListener {item ->
            when(item.itemId) { 
                R.id.home_fragment -> {
                    navController.navigate(R.id.home_fragment)
                    true
                }
                R.id.favourites_fragment -> {
                    navController.navigate(R.id.favourites_fragment)
                    true
                }
                R.id.maps_fragment -> {
                    navController.navigate(R.id.maps_fragment)
                    true
                }
                R.id.account_fragment -> {
                    if(auth.currentUser == null) {
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
    
}