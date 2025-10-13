package com.example.catconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.catconnect.R
import com.example.catconnect.databinding.ActivityMainBinding
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // jadikan toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController

        // top-level = tab utama bottom nav (tanpa tombol back)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.feedFragment, R.id.mapFragment, R.id.profileFragment)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        val topLevel = setOf(R.id.feedFragment, R.id.mapFragment, R.id.profileFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevel) {
                binding.bottomNav.visibility = View.VISIBLE
            } else {
                binding.bottomNav.visibility = View.GONE
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
