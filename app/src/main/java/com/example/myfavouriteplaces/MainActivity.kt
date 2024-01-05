package com.example.myfavouriteplaces

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav: BottomNavigationView
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

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
                    navController.navigate(R.id.account_fragment)
                    true
                }
                else -> false
            }
        }
    }

    
}