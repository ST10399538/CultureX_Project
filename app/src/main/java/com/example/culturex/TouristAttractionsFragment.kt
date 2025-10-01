package com.example.culturex

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.util.Log
import kotlinx.coroutines.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import org.json.JSONObject
import java.net.URL

class TouristAttractionsFragment : Fragment(R.layout.fragment_tourist_attractions), OnMapReadyCallback {

    // Google Map object
    private lateinit var googleMap: GoogleMap
    // FusedLocationProviderClient for getting current location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // UI elements
    private lateinit var backArrow: ImageView
    private lateinit var searchInput: EditText
    private lateinit var searchButton: ImageView
    private lateinit var currentLocationText: TextView
    private lateinit var distanceText: TextView

    // Data passed from previous fragment/activity
    private var countryId: String? = null
    private var countryName: String? = null

    // Current location coordinates
    private var currentLocation: LatLng? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1 // Request code for location permission
        private const val DEFAULT_ZOOM = 15f // Default zoom level for map camera
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get country data from arguments
        countryId = arguments?.getString("countryId")
        countryName = arguments?.getString("countryName")

        initializeViews(view)
        setupMapFragment()
        setupClickListeners()

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun initializeViews(view: View) {
        backArrow = view.findViewById(R.id.back_arrow)
        searchInput = view.findViewById(R.id.search_input)
        searchButton = view.findViewById(R.id.search_button)
        currentLocationText = view.findViewById(R.id.current_location_text)
        distanceText = view.findViewById(R.id.distance_text)

        // Set the title to show current country or default
        view.findViewById<TextView>(R.id.title_text)?.text = countryName?.let { "Tourist Attractions - $it" } ?: "Tourist Attractions"
    }

    private fun setupMapFragment() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("TouristAttractionsFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPlaces(query)
            } else {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }

        // Allow search on enter key
        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString().trim()
            if (query.isNotEmpty()) {
                searchPlaces(query)
                true
            } else {
                false
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable zoom controls
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }

        // Set up map click listener for place selection
        googleMap.setOnMarkerClickListener { marker ->
            // Handle marker click - could show place details
            Toast.makeText(requireContext(), "Selected: ${marker.title}", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)

                    // Move camera to current location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation!!, DEFAULT_ZOOM))

                    // Update UI with location info
                    updateLocationUI(it)

                    // Search for nearby tourist attractions
                    searchNearbyAttractions()
                }
            }
        }
    }

    private fun updateLocationUI(location: Location) {
        currentLocationText.text = "Current location"
        // You could use reverse geocoding here to get address
        distanceText.text = "${location.latitude.format(4)}, ${location.longitude.format(4)}"
    }

    private fun searchPlaces(query: String) {
        if (currentLocation == null) {
            Toast.makeText(requireContext(), "Location not available. Please enable location services.", Toast.LENGTH_LONG).show()
            return
        }

        // Clear existing markers
        googleMap.clear()

        // Simulate search results (In a real app, you'd use Places API)
        simulateSearchResults(query)
    }

    private fun searchNearbyAttractions() {
        if (currentLocation == null) return

        // Simulate nearby tourist attractions
        val attractions = listOf(
            "Museums" to LatLng(currentLocation!!.latitude + 0.01, currentLocation!!.longitude + 0.01),
            "Parks" to LatLng(currentLocation!!.latitude - 0.01, currentLocation!!.longitude + 0.01),
            "Restaurants" to LatLng(currentLocation!!.latitude + 0.01, currentLocation!!.longitude - 0.01),
            "Shopping" to LatLng(currentLocation!!.latitude - 0.01, currentLocation!!.longitude - 0.01)
        )

        attractions.forEach { (name, location) ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(name)
                    .snippet("Popular tourist attraction")
            )
        }
    }

    private fun simulateSearchResults(query: String) {
        // In a real implementation, you would use Google Places API
        // For now, simulate some results based on the query

        val searchResults = when {
            query.contains("restaurant", true) || query.contains("food", true) -> {
                listOf(
                    "Local Restaurant 1" to LatLng(currentLocation!!.latitude + 0.005, currentLocation!!.longitude + 0.005),
                    "Local Restaurant 2" to LatLng(currentLocation!!.latitude - 0.005, currentLocation!!.longitude + 0.005),
                    "Local Restaurant 3" to LatLng(currentLocation!!.latitude + 0.005, currentLocation!!.longitude - 0.005)
                )
            }
            query.contains("museum", true) || query.contains("culture", true) -> {
                listOf(
                    "Cultural Museum" to LatLng(currentLocation!!.latitude + 0.008, currentLocation!!.longitude + 0.003),
                    "Art Gallery" to LatLng(currentLocation!!.latitude - 0.003, currentLocation!!.longitude + 0.008),
                    "History Center" to LatLng(currentLocation!!.latitude + 0.003, currentLocation!!.longitude - 0.008)
                )
            }
            query.contains("park", true) || query.contains("nature", true) -> {
                listOf(
                    "Central Park" to LatLng(currentLocation!!.latitude + 0.01, currentLocation!!.longitude + 0.005),
                    "Nature Reserve" to LatLng(currentLocation!!.latitude - 0.005, currentLocation!!.longitude + 0.01),
                    "Botanical Garden" to LatLng(currentLocation!!.latitude + 0.005, currentLocation!!.longitude - 0.01)
                )
            }
            else -> {
                listOf(
                    "Tourist Spot 1" to LatLng(currentLocation!!.latitude + 0.007, currentLocation!!.longitude + 0.007),
                    "Tourist Spot 2" to LatLng(currentLocation!!.latitude - 0.007, currentLocation!!.longitude + 0.007),
                    "Tourist Spot 3" to LatLng(currentLocation!!.latitude + 0.007, currentLocation!!.longitude - 0.007)
                )
            }
        }

        searchResults.forEach { (name, location) ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(name)
                    .snippet("Search result for: $query")
            )
        }

        // Calculate distances and update UI
        if (searchResults.isNotEmpty()) {
            val firstResult = searchResults.first().second
            val distance = calculateDistance(currentLocation!!, firstResult)
            distanceText.text = "${distance.toInt()} min [${(distance * 0.02).format(2)} km]"
        }

        Toast.makeText(requireContext(), "Found ${searchResults.size} results for '$query'", Toast.LENGTH_SHORT).show()
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        // Simple distance calculation (in a real app, you'd use proper distance calculation)
        val deltaLat = end.latitude - start.latitude
        val deltaLng = end.longitude - start.longitude
        return kotlin.math.sqrt(deltaLat * deltaLat + deltaLng * deltaLng) * 111320 / 1000 // Rough conversion to km
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation()
                } else {
                    Toast.makeText(requireContext(), "Location permission is required for this feature", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Extension function for formatting doubles
    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]