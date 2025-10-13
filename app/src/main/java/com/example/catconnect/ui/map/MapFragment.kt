package com.example.catconnect.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.catconnect.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import android.util.Log

class MapFragment : Fragment(R.layout.fragment_map) {

    private var googleMap: GoogleMap? = null

    private val requestPerms = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { res ->
        val granted = (res[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (res[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        if (granted) enableMyLocation() else
            Snackbar.make(requireView(), "Location permission denied", Snackbar.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFrag = childFragmentManager.findFragmentById(R.id.mapFragmentContainer)
                as SupportMapFragment
        mapFrag.getMapAsync { map ->
            Log.d("MapDebug", "onMapReady called")
            googleMap = map
            setupMarkers(map)
            checkLocationPermission()
        }
    }

    private fun setupMarkers(map: GoogleMap) {
        val center = LatLng(-6.2, 106.8)
        val vet = LatLng(-6.201, 106.81)
        val shop = LatLng(-6.205, 106.79)
        map.addMarker(MarkerOptions().position(vet).title("Vet Paws Clinic"))
        map.addMarker(MarkerOptions().position(shop).title("Meow Pet Shop"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
    }

    private fun checkLocationPermission() {
        val fine = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) enableMyLocation() else {
            requestPerms.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun enableMyLocation() { googleMap?.isMyLocationEnabled = true }
}
