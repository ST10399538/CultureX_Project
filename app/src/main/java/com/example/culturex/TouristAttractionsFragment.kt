package com.example.culturex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.TouristAttractionsAdapter
import com.example.culturex.data.models.TouristAttraction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import android.util.Log

class TouristAttractionsFragment : Fragment(R.layout.fragment_tourist_attractions), OnMapReadyCallback {

    private lateinit var backArrow: ImageView
    private lateinit var searchInput: EditText
    private lateinit var clearSearch: ImageView
    private lateinit var locationStatusText: TextView
    private lateinit var sortButton: Button
    private lateinit var attractionsRecyclerView: RecyclerView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var emptyStateText: TextView
    private lateinit var mapView: MapView

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var attractionsAdapter: TouristAttractionsAdapter
    private lateinit var placesClient: PlacesClient

    private var googleMap: GoogleMap? = null
    private var currentLocation: Location? = null
    private var countryId: String? = null
    private var countryName: String = "South Africa" // Made non-nullable with default
    private var attractions: MutableList<TouristAttraction> = mutableListOf()
    private var isSearchingPlaces = false

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getUserLocation()
        } else {
            locationStatusText.text = "Location permission denied"
            Toast.makeText(requireContext(),
                "Location permission is required to show distances",
                Toast.LENGTH_LONG).show()
            loadCountrySpecificAttractions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyD1quqInPhHqHTGDtzTXT3iSOJjAJjl_1w")
        }
        placesClient = Places.createClient(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        countryId = arguments?.getString("countryId")
        countryName = arguments?.getString("countryName") ?: "South Africa" // Safe handling

        initializeViews(view)
        setupRecyclerView()
        setupSearchFunctionality()
        setupClickListeners()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        checkLocationPermission()
    }

    private fun initializeViews(view: View) {
        backArrow = view.findViewById(R.id.back_arrow)
        searchInput = view.findViewById(R.id.search_input)
        clearSearch = view.findViewById(R.id.clear_search)
        locationStatusText = view.findViewById(R.id.location_status_text)
        sortButton = view.findViewById(R.id.sort_button)
        attractionsRecyclerView = view.findViewById(R.id.attractions_recycler_view)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        emptyState = view.findViewById(R.id.empty_state)
        emptyStateText = view.findViewById(R.id.empty_state_text)
        mapView = view.findViewById(R.id.map_view)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isCompassEnabled = true

        val countryCenter = getCountryCenter(countryName)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(countryCenter, getCountryZoom(countryName)))

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            try {
                googleMap?.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                Log.e("TouristAttractions", "Location permission error", e)
            }
        }

        if (attractions.isNotEmpty()) {
            updateMapMarkers()
        }
    }

    private fun getCountryCenter(country: String): LatLng {
        return when (country.lowercase()) {
            "south africa" -> LatLng(-30.5595, 22.9375)
            "france" -> LatLng(46.2276, 2.2137)
            "japan" -> LatLng(36.2048, 138.2529)
            "india" -> LatLng(20.5937, 78.9629)
            "united states" -> LatLng(37.0902, -95.7129)
            else -> LatLng(0.0, 0.0)
        }
    }

    private fun getCountryZoom(country: String): Float {
        return when (country.lowercase()) {
            "south africa" -> 5.5f
            "france" -> 5.5f
            "japan" -> 5f
            "india" -> 4.5f
            "united states" -> 4f
            else -> 2f
        }
    }

    private fun getCountryBounds(country: String): RectangularBounds {
        return when (country.lowercase()) {
            "south africa" -> RectangularBounds.newInstance(
                LatLng(-35.0, 16.0),
                LatLng(-22.0, 33.0)
            )
            "france" -> RectangularBounds.newInstance(
                LatLng(41.0, -5.0),
                LatLng(51.0, 10.0)
            )
            "japan" -> RectangularBounds.newInstance(
                LatLng(24.0, 123.0),
                LatLng(46.0, 146.0)
            )
            "india" -> RectangularBounds.newInstance(
                LatLng(6.0, 68.0),
                LatLng(36.0, 98.0)
            )
            "united states" -> RectangularBounds.newInstance(
                LatLng(24.0, -125.0),
                LatLng(50.0, -66.0)
            )
            else -> RectangularBounds.newInstance(
                LatLng(-85.0, -180.0),
                LatLng(85.0, 180.0)
            )
        }
    }

    private fun updateMapMarkers() {
        googleMap?.clear()

        attractions.forEach { attraction ->
            val position = LatLng(attraction.latitude, attraction.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(attraction.name)
                    .snippet(attraction.category)
            )
        }

        if (attractions.isNotEmpty()) {
            val firstAttraction = attractions[0]
            val position = LatLng(firstAttraction.latitude, firstAttraction.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
        }

        currentLocation?.let { location ->
            val userPosition = LatLng(location.latitude, location.longitude)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 10f))
        }
    }

    private fun setupRecyclerView() {
        attractionsAdapter = TouristAttractionsAdapter { attraction ->
            openAttractionInMaps(attraction)
        }

        attractionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = attractionsAdapter
        }
    }

    private fun setupSearchFunctionality() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                clearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

                if (query.isEmpty()) {
                    isSearchingPlaces = false
                    loadCountrySpecificAttractions()
                } else if (query.length >= 3) {
                    searchPlaces(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearSearch.setOnClickListener {
            searchInput.text.clear()
        }

        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                searchPlaces(query)
            }
            false
        }
    }

    private fun searchPlaces(query: String) {
        loadingIndicator.visibility = View.VISIBLE
        isSearchingPlaces = true

        val bounds = getCountryBounds(countryName)
        val countryCodes = getCountryCode(countryName)

        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(bounds)
            .setQuery(query)

        if (countryCodes.isNotEmpty()) {
            requestBuilder.setCountries(countryCodes)
        }

        val request = requestBuilder.build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions

                if (predictions.isEmpty()) {
                    loadingIndicator.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    emptyStateText.text = "No places found for \"$query\" in $countryName"
                    attractionsRecyclerView.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val searchResults = mutableListOf<TouristAttraction>()
                var processedCount = 0
                val maxResults = minOf(10, predictions.size)

                predictions.take(maxResults).forEach { prediction ->
                    val placeFields = listOf(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.TYPES
                    )

                    val fetchRequest = FetchPlaceRequest.builder(prediction.placeId, placeFields)
                        .build()

                    placesClient.fetchPlace(fetchRequest)
                        .addOnSuccessListener { fetchResponse ->
                            val place = fetchResponse.place
                            place.latLng?.let { latLng ->
                                val category = place.types?.firstOrNull()?.name ?: "Place"
                                val attraction = TouristAttraction(
                                    id = place.id ?: System.currentTimeMillis().toString(),
                                    name = place.name ?: "Unknown",
                                    category = category,
                                    description = prediction.getFullText(null).toString(),
                                    latitude = latLng.latitude,
                                    longitude = latLng.longitude
                                )

                                currentLocation?.let { userLocation ->
                                    val attractionLocation = Location("").apply {
                                        latitude = attraction.latitude
                                        longitude = attraction.longitude
                                    }
                                    attraction.distanceFromUser = userLocation.distanceTo(attractionLocation) / 1000.0
                                }

                                searchResults.add(attraction)
                            }

                            processedCount++
                            if (processedCount == maxResults) {
                                loadingIndicator.visibility = View.GONE
                                attractions.clear()
                                attractions.addAll(searchResults.sortedBy { it.distanceFromUser })
                                attractionsAdapter.updateAttractions(attractions)
                                updateMapMarkers()
                                updateEmptyState()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("TouristAttractions", "Place fetch failed", exception)
                            processedCount++
                            if (processedCount == maxResults) {
                                loadingIndicator.visibility = View.GONE
                                if (searchResults.isNotEmpty()) {
                                    attractions.clear()
                                    attractions.addAll(searchResults.sortedBy { it.distanceFromUser })
                                    attractionsAdapter.updateAttractions(attractions)
                                    updateMapMarkers()
                                }
                                updateEmptyState()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                loadingIndicator.visibility = View.GONE
                Log.e("TouristAttractions", "Place search failed", exception)
                Toast.makeText(
                    requireContext(),
                    "Search failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getCountryCode(country: String): List<String> {
        return when (country.lowercase()) {
            "south africa" -> listOf("ZA")
            "france" -> listOf("FR")
            "japan" -> listOf("JP")
            "india" -> listOf("IN")
            "united states" -> listOf("US")
            else -> emptyList()
        }
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        sortButton.setOnClickListener {
            attractionsAdapter.sortByDistance()
            Toast.makeText(requireContext(), "Sorted by distance", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getUserLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    requireContext(),
                    "Location access is needed to show distances to attractions",
                    Toast.LENGTH_LONG
                ).show()
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        loadingIndicator.visibility = View.VISIBLE
        locationStatusText.text = "Getting your location..."

        val cancellationToken = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        ).addOnSuccessListener { location: Location? ->
            loadingIndicator.visibility = View.GONE

            if (location != null) {
                currentLocation = location
                locationStatusText.text = "Location: ${location.latitude.format(4)}, ${location.longitude.format(4)}"
                Log.d("TouristAttractions", "Location obtained: ${location.latitude}, ${location.longitude}")

                loadCountrySpecificAttractions()
            } else {
                locationStatusText.text = "Unable to get location"
                loadCountrySpecificAttractions()
            }
        }.addOnFailureListener { e ->
            loadingIndicator.visibility = View.GONE
            locationStatusText.text = "Location error: ${e.message}"
            Log.e("TouristAttractions", "Location error", e)
            loadCountrySpecificAttractions()
        }
    }

    private fun loadCountrySpecificAttractions() {
        if (isSearchingPlaces) return

        attractions.clear()

        val countryAttractions = when (countryName.lowercase()) {
            "south africa" -> getSouthAfricaAttractions()
            "france" -> getFranceAttractions()
            "japan" -> getJapanAttractions()
            "india" -> getIndiaAttractions()
            "united states" -> getUSAttractions()
            else -> getSouthAfricaAttractions()
        }

        attractions.addAll(countryAttractions)

        currentLocation?.let { userLocation ->
            attractions.forEach { attraction ->
                val attractionLocation = Location("").apply {
                    latitude = attraction.latitude
                    longitude = attraction.longitude
                }
                attraction.distanceFromUser = userLocation.distanceTo(attractionLocation) / 1000.0
            }
        }

        attractionsAdapter.updateAttractions(attractions)
        updateMapMarkers()
        updateEmptyState()
    }

    private fun getSouthAfricaAttractions() = listOf(
        TouristAttraction("1", "Table Mountain", "Natural Wonder", "Iconic flat-topped mountain", -33.9628, 18.4098),
        TouristAttraction("2", "Kruger National Park", "Wildlife Reserve", "Largest game reserve", -23.9884, 31.5547),
        TouristAttraction("3", "Robben Island", "Historical Site", "Nelson Mandela prison", -33.8070, 18.3704),
        TouristAttraction("4", "V&A Waterfront", "Shopping", "Popular waterfront complex", -33.9022, 18.4187),
        TouristAttraction("5", "Cape of Good Hope", "Natural Landmark", "Cape Peninsula headland", -34.3567, 18.4974),
        TouristAttraction("6", "Apartheid Museum", "Museum", "Apartheid history", -26.2349, 27.9873),
        TouristAttraction("7", "Blyde River Canyon", "Natural Wonder", "Third largest canyon", -24.5589, 30.8165),
        TouristAttraction("8", "Kirstenbosch Gardens", "Garden", "Botanical gardens", -33.9880, 18.4325)
    )

    private fun getFranceAttractions() = listOf(
        TouristAttraction("1", "Eiffel Tower", "Landmark", "Iconic Paris monument", 48.8584, 2.2945),
        TouristAttraction("2", "Louvre Museum", "Museum", "World's largest art museum", 48.8606, 2.3376),
        TouristAttraction("3", "Notre-Dame Cathedral", "Religious Site", "Gothic cathedral", 48.8530, 2.3499),
        TouristAttraction("4", "Palace of Versailles", "Palace", "Royal château", 48.8049, 2.1204),
        TouristAttraction("5", "Mont Saint-Michel", "Island", "Tidal island abbey", 48.6361, -1.5115),
        TouristAttraction("6", "Arc de Triomphe", "Monument", "Triumphal arch", 48.8738, 2.2950),
        TouristAttraction("7", "Côte d'Azur", "Beach", "French Riviera", 43.7034, 7.2663),
        TouristAttraction("8", "Château de Chambord", "Castle", "Renaissance castle", 47.6163, 1.5172)
    )

    private fun getJapanAttractions() = listOf(
        TouristAttraction("1", "Mount Fuji", "Mountain", "Iconic volcano", 35.3606, 138.7274),
        TouristAttraction("2", "Tokyo Tower", "Landmark", "Communications tower", 35.6586, 139.7454),
        TouristAttraction("3", "Fushimi Inari Shrine", "Shrine", "Thousands of torii gates", 34.9671, 135.7727),
        TouristAttraction("4", "Hiroshima Peace Memorial", "Memorial", "Peace museum", 34.3955, 132.4536),
        TouristAttraction("5", "Kinkaku-ji Temple", "Temple", "Golden Pavilion", 35.0394, 135.7292),
        TouristAttraction("6", "Osaka Castle", "Castle", "Historic fortress", 34.6873, 135.5262),
        TouristAttraction("7", "Arashiyama Bamboo Grove", "Nature", "Bamboo forest", 35.0170, 135.6710),
        TouristAttraction("8", "Tokyo Skytree", "Tower", "Broadcasting tower", 35.7101, 139.8107)
    )

    private fun getIndiaAttractions() = listOf(
        TouristAttraction("1", "Taj Mahal", "Monument", "Marble mausoleum", 27.1751, 78.0421),
        TouristAttraction("2", "Red Fort", "Fort", "Historic fortification", 28.6562, 77.2410),
        TouristAttraction("3", "Gateway of India", "Landmark", "Arch monument", 18.9220, 72.8347),
        TouristAttraction("4", "Hawa Mahal", "Palace", "Palace of Winds", 26.9239, 75.8267),
        TouristAttraction("5", "Golden Temple", "Temple", "Sikh shrine", 31.6200, 74.8765),
        TouristAttraction("6", "Qutub Minar", "Tower", "Minaret tower", 28.5245, 77.1855),
        TouristAttraction("7", "India Gate", "Memorial", "War memorial", 28.6129, 77.2295),
        TouristAttraction("8", "Amber Fort", "Fort", "Hilltop fort", 26.9855, 75.8513)
    )

    private fun getUSAttractions() = listOf(
        TouristAttraction("1", "Statue of Liberty", "Monument", "Liberty symbol", 40.6892, -74.0445),
        TouristAttraction("2", "Grand Canyon", "Natural Wonder", "Canyon landscape", 36.1069, -112.1129),
        TouristAttraction("3", "Times Square", "District", "Major intersection", 40.7580, -73.9855),
        TouristAttraction("4", "Golden Gate Bridge", "Bridge", "Suspension bridge", 37.8199, -122.4783),
        TouristAttraction("5", "Yellowstone National Park", "Park", "Geothermal park", 44.4280, -110.5885),
        TouristAttraction("6", "White House", "Government", "Presidential residence", 38.8977, -77.0365),
        TouristAttraction("7", "Hollywood Sign", "Landmark", "LA landmark", 34.1341, -118.3215),
        TouristAttraction("8", "Niagara Falls", "Waterfall", "Waterfalls", 43.0962, -79.0377)
    )

    private fun updateEmptyState() {
        val hasItems = attractionsAdapter.itemCount > 0
        emptyState.visibility = if (hasItems) View.GONE else View.VISIBLE
        attractionsRecyclerView.visibility = if (hasItems) View.VISIBLE else View.GONE

        emptyStateText.text = if (searchInput.text.isEmpty()) {
            "No attractions available for $countryName"
        } else {
            "No attractions match your search in $countryName"
        }
    }

    private fun openAttractionInMaps(attraction: TouristAttraction) {
        val uri = Uri.parse("geo:${attraction.latitude},${attraction.longitude}?q=${attraction.latitude},${attraction.longitude}(${attraction.name})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${attraction.latitude},${attraction.longitude}")
            startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}