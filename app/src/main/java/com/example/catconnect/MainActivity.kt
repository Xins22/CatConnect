package com.example.catconnect

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.catconnect.data.repo.FakeRepository
import com.example.catconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.feedFragment, R.id.mapFragment),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        binding.navView.setupWithNavController(navController)

        val topLevel = setOf(R.id.feedFragment, R.id.mapFragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevel) {
                binding.bottomNav.visibility = View.VISIBLE
            } else {
                binding.bottomNav.visibility = View.GONE
            }
        }

        FakeRepository.events.observe(this) { events ->
            val menu = binding.navView.menu
            val eventsGroup = menu.findItem(R.id.group_events).subMenu
            eventsGroup?.clear()

            events.forEach { event ->
                eventsGroup?.add(event.title)?.setOnMenuItemClickListener { _ ->
                    val bundle = bundleOf("eventId" to event.id)
                    navController.navigate(R.id.eventDetailFragment, bundle)
                    binding.drawerLayout.close()
                    true
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
