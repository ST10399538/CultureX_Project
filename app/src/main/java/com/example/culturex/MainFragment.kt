package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentMainBinding
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.viewmodels.MainViewModel
import com.example.culturex.utils.SharedPreferencesManager
import com.example.culturex.utils.ItineraryEventScheduler
import android.util.Log

class MainFragment : Fragment(R.layout.fragment_main) {

    // View binding reference
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    // ViewModel instance scoped to this fragment
    private val mainViewModel: MainViewModel by viewModels()
    // Shared preferences manager for storing/retrieving persistent data
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    // Lists to hold countries and categories fetched from the ViewModel
    private var countriesList = emptyList<CountryModels.CountryDTO>()
    private var categoriesList = emptyList<CountryModels.CulturalCategoryDTO>()

    // Track if holidays have been scheduled for this session
    private var lastScheduledCountry: String? = null
    private var isInitialSelection = true

    // Called after the fragment's view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        sharedPrefsManager = SharedPreferencesManager(requireContext())

        // Reset flag when view is created
        isInitialSelection = true

        // Setup LiveData observers
        setupObservers()
        setupClickListeners()
        setupTopNavigation()

        // Load initial data
        mainViewModel.loadCountries()
    }

    private fun setupTopNavigation() {
        // Profile icon click - navigate to profile
        binding.profileIcon.setOnClickListener {
            Log.d("MainFragment", "Profile icon clicked")
            try {
                findNavController().navigate(R.id.action_main_to_profile)
            } catch (e: Exception) {
                Log.e("MainFragment", "Navigation to profile failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Search icon click - navigate to maps/tourist attractions
        binding.searchIcon.setOnClickListener {
            Log.d("MainFragment", "Search icon clicked")
            try {
                val selectedCountry = mainViewModel.selectedCountry.value
                val bundle = Bundle().apply {
                    putString("countryId", selectedCountry?.id)
                    putString("countryName", selectedCountry?.name)
                }
                findNavController().navigate(R.id.action_main_to_touristAttractions, bundle)
            } catch (e: Exception) {
                Log.e("MainFragment", "Navigation to tourist attractions failed", e)
                Toast.makeText(requireContext(), "Maps feature coming soon",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Observes LiveData from ViewModel and updates UI accordingly
    private fun setupObservers() {
        mainViewModel.countries.observe(viewLifecycleOwner) { countries ->
            countriesList = countries
            setupCountrySpinner(countries)
        }

        // Observe country list and update spinner
        mainViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            updateCategoryButtonsVisibility(categories)
        }

        // Observe categories and update buttons visibility
        mainViewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            country?.let {
                // Update UI with selected country info if needed
            }
        }
        // Observe whether categories are loaded -> enable buttons if true
        mainViewModel.areCategoriesLoaded.observe(viewLifecycleOwner) { loaded ->
            if (loaded) {
                enableCategoryButtons()
            } else {
                disableCategoryButtons()
            }
        }

        // Observe loading state -> disable buttons while loading
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                disableCategoryButtons()
            }
        }
        // Observe error messages -> show toast and clear error
        mainViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                mainViewModel.clearError()
            }
        }
    }

    // Setup country spinner with a list of available countries
    private fun setupCountrySpinner(countries: List<CountryModels.CountryDTO>) {
        if (countries.isEmpty()) {
            Toast.makeText(requireContext(), "No countries available",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Extract country names
        val countryNames = countries.map { it.name ?: "Unknown" }

        // Setup adapter for spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Handle spinner item selection
        binding.countrySpinner.adapter = adapter
        binding.countrySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?,
                                        position: Int, id: Long) {
                if (position < countries.size) {
                    val selectedCountry = countries[position]
                    mainViewModel.selectCountry(selectedCountry)

                    // Only schedule holidays if:
                    // 1. Not the initial spinner setup
                    // 2. Country is different from last scheduled
                    val countryName = selectedCountry.name ?: "Unknown"
                    if (!isInitialSelection && countryName != lastScheduledCountry) {
                        schedulePublicHolidayNotifications(countryName)
                        lastScheduledCountry = countryName
                    }

                    // Mark that initial selection is complete
                    isInitialSelection = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    /**
     * Schedules public holiday notifications for the selected country
     */
    private fun schedulePublicHolidayNotifications(countryName: String) {
        Log.d("MainFragment", "Scheduling holidays for: $countryName")

        // Show loading message
        Toast.makeText(
            requireContext(),
            "🎉 Loading holiday reminders for $countryName...",
            Toast.LENGTH_SHORT
        ).show()

        // Schedule notifications using ItineraryEventScheduler
        ItineraryEventScheduler.schedulePublicHolidays(
            context = requireContext(),
            countryName = countryName,
            scope = lifecycleScope
        )

        // Show success message after a short delay
        view?.postDelayed({
            Toast.makeText(
                requireContext(),
                "✓ Holiday notifications set for $countryName",
                Toast.LENGTH_LONG
            ).show()
        }, 1500)
    }

    // Show/hide category buttons depending on availability for selected country
    private fun updateCategoryButtonsVisibility(categories:
                                                List<CountryModels.CulturalCategoryDTO>) {
        val categoryNames = categories.map { it.name }

        binding.menuDressCode.isEnabled = categoryNames.contains("Dress Code")
        binding.menuCommunication.isEnabled =
            categoryNames.contains("Communication Style")
        binding.menuGreetings.isEnabled = categoryNames.contains("Greeting Customs")
        binding.menuEtiquette.isEnabled = categoryNames.contains("General Etiquette")
        binding.menuTipping.isEnabled = categoryNames.contains("Tipping Norms")

        // Enable/disable category buttons
        val disabledAlpha = 0.5f
        val enabledAlpha = 1.0f

        // Set button transparency depending on availability
        binding.menuDressCode.alpha = if (binding.menuDressCode.isEnabled)
            enabledAlpha else disabledAlpha
        binding.menuCommunication.alpha = if (binding.menuCommunication.isEnabled)
            enabledAlpha else disabledAlpha
        binding.menuGreetings.alpha = if (binding.menuGreetings.isEnabled)
            enabledAlpha else disabledAlpha
        binding.menuEtiquette.alpha = if (binding.menuEtiquette.isEnabled)
            enabledAlpha else disabledAlpha
        binding.menuTipping.alpha = if (binding.menuTipping.isEnabled)
            enabledAlpha else disabledAlpha
    }

    // Disable all category buttons
    private fun disableCategoryButtons() {
        binding.menuDressCode.isEnabled = false
        binding.menuCommunication.isEnabled = false
        binding.menuGreetings.isEnabled = false
        binding.menuEtiquette.isEnabled = false
        binding.menuTipping.isEnabled = false

        val disabledAlpha = 0.5f
        binding.menuDressCode.alpha = disabledAlpha
        binding.menuCommunication.alpha = disabledAlpha
        binding.menuGreetings.alpha = disabledAlpha
        binding.menuEtiquette.alpha = disabledAlpha
        binding.menuTipping.alpha = disabledAlpha
    }

    // Enable all category buttons
    private fun enableCategoryButtons() {
        binding.menuDressCode.isEnabled = true
        binding.menuCommunication.isEnabled = true
        binding.menuGreetings.isEnabled = true
        binding.menuEtiquette.isEnabled = true
        binding.menuTipping.isEnabled = true

        val enabledAlpha = 1.0f
        binding.menuDressCode.alpha = enabledAlpha
        binding.menuCommunication.alpha = enabledAlpha
        binding.menuGreetings.alpha = enabledAlpha
        binding.menuEtiquette.alpha = enabledAlpha
        binding.menuTipping.alpha = enabledAlpha
    }

    // Setup click listeners for category and bottom navigation buttons
    private fun setupClickListeners() {
        binding.menuDressCode.setOnClickListener {
            navigateToCategory("Dress Code")
        }

        binding.menuCommunication.setOnClickListener {
            navigateToCategory("Communication Style")
        }

        binding.menuGreetings.setOnClickListener {
            navigateToCategory("Greeting Customs")
        }

        binding.menuEtiquette.setOnClickListener {
            navigateToCategory("General Etiquette")
        }

        binding.menuTipping.setOnClickListener {
            navigateToCategory("Tipping Norms")
        }

        // Bottom Navigation
        binding.navEmergency.setOnClickListener {
            navigateToEmergency()
        }

        binding.navSaved.setOnClickListener {
            navigateToSaved()
        }

        binding.navNotifications.setOnClickListener {
            navigateToNotifications()
        }
    }

    // Navigate to a specific cultural category (only if available)
    private fun navigateToCategory(categoryName: String) {
        val selectedCountry = mainViewModel.selectedCountry.value
        if (selectedCountry == null) {
            Toast.makeText(requireContext(), "Please select a country first",
                Toast.LENGTH_SHORT).show()
            return
        }
        // Check if category exists for selected country
        if (!mainViewModel.isCategoryAvailable(categoryName)) {
            Toast.makeText(requireContext(),
                "$categoryName not available for ${selectedCountry.name}",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Get category object from ViewModel
        val category = mainViewModel.getCategoryByName(categoryName)
        if (category == null) {
            Toast.makeText(requireContext(), "Category not found",
                Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare bundle to pass data to next fragment
        val bundle = Bundle().apply {
            putString("countryId", selectedCountry.id)
            putString("categoryId", category.id)
            putString("categoryName", categoryName)
            putString("countryName", selectedCountry.name)
        }

        // Navigate to the correct fragment based on category name
        try {
            when (categoryName) {
                "Dress Code" -> {
                    findNavController().navigate(R.id.dressCodeFragment, bundle)
                }
                "Communication Style" -> {
                    findNavController().navigate(R.id.communicationFragment, bundle)
                }
                "Greeting Customs" -> {
                    findNavController().navigate(R.id.greetingsFragment, bundle)
                }
                "General Etiquette" -> {
                    findNavController().navigate(R.id.etiquetteFragment, bundle)
                }
                "Tipping Norms" -> {
                    findNavController().navigate(R.id.tippingFragment, bundle)
                }
                else -> {
                    Toast.makeText(requireContext(),
                        "Navigation not implemented for $categoryName",
                        Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Navigation error: ${e.message}",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToEmergency() {
        val selectedCountry = mainViewModel.selectedCountry.value
        if (selectedCountry == null) {
            Toast.makeText(requireContext(), "Please select a country first",
                Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putString("countryId", selectedCountry.id)
            putString("countryName", selectedCountry.name)
        }

        try {
            findNavController().navigate(R.id.emergencyFragment, bundle)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Emergency feature coming soon",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSaved() {
        try {
            findNavController().navigate(R.id.savedFragment)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Saved feature coming soon",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNotifications() {
        try {
            findNavController().navigate(R.id.notificationsFragment)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Notifications feature coming soon",
                Toast.LENGTH_SHORT).show()
        }
    }

    // Clear binding when fragment view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]
// Android Developers, 2025. Schedule tasks with WorkManager. [online]. Available at: https://developer.android.com/topic/libraries/architecture/workmanager [Accessed on 9 November 2025]