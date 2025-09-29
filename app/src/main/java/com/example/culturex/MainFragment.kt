package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentMainBinding
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.viewmodels.MainViewModel
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    private var countriesList = emptyList<CountryModels.CountryDTO>()
    private var categoriesList = emptyList<CountryModels.CulturalCategoryDTO>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        sharedPrefsManager = SharedPreferencesManager(requireContext())

        setupObservers()
        setupClickListeners()
        setupTopNavigation()

        // Load initial data
        mainViewModel.loadCountries()
    }

    private fun setupTopNavigation() {
        // Profile icon click - navigate to edit profile
        binding.profileIcon.setOnClickListener {
            Log.d("MainFragment", "Profile icon clicked")
            try {
                findNavController().navigate(R.id.action_main_to_editProfile)
            } catch (e: Exception) {
                Log.e("MainFragment", "Navigation to edit profile failed", e)
                Toast.makeText(requireContext(), "Profile feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }

        // Search icon click - navigate to maps/tourist attractions
        binding.searchIcon.setOnClickListener {
            Log.d("MainFragment", "Search icon clicked")
            try {
                // Pass selected country data to maps fragment
                val selectedCountry = mainViewModel.selectedCountry.value
                val bundle = Bundle().apply {
                    putString("countryId", selectedCountry?.id)
                    putString("countryName", selectedCountry?.name)
                }
                findNavController().navigate(R.id.action_main_to_touristAttractions, bundle)
            } catch (e: Exception) {
                Log.e("MainFragment", "Navigation to tourist attractions failed", e)
                Toast.makeText(requireContext(), "Maps feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        mainViewModel.countries.observe(viewLifecycleOwner) { countries ->
            countriesList = countries
            setupCountrySpinner(countries)
        }

        mainViewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            updateCategoryButtonsVisibility(categories)
        }

        mainViewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            // Update UI to show selected country
            country?.let {
                // You can add logic here to update UI with selected country info
            }
        }

        mainViewModel.areCategoriesLoaded.observe(viewLifecycleOwner) { loaded ->
            if (loaded) {
                // Enable category buttons
                enableCategoryButtons()
            } else {
                // Disable category buttons until categories are loaded
                disableCategoryButtons()
            }
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator
            if (isLoading) {
                // You can show a progress bar here
                disableCategoryButtons()
            }
        }

        mainViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                mainViewModel.clearError()
            }
        }
    }

    private fun setupCountrySpinner(countries: List<CountryModels.CountryDTO>) {
        if (countries.isEmpty()) {
            Toast.makeText(requireContext(), "No countries available", Toast.LENGTH_SHORT).show()
            return
        }

        val countryNames = countries.map { it.name ?: "Unknown" }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.countrySpinner.adapter = adapter
        binding.countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position < countries.size) {
                    val selectedCountry = countries[position]
                    mainViewModel.selectCountry(selectedCountry)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateCategoryButtonsVisibility(categories: List<CountryModels.CulturalCategoryDTO>) {
        // Update button states based on available categories
        val categoryNames = categories.map { it.name }

        // Enable/disable buttons based on available categories
        binding.menuDressCode.isEnabled = categoryNames.contains("Dress Code")
        binding.menuCommunication.isEnabled = categoryNames.contains("Communication Style")
        binding.menuGreetings.isEnabled = categoryNames.contains("Greeting Customs")
        binding.menuEtiquette.isEnabled = categoryNames.contains("General Etiquette")
        binding.menuTipping.isEnabled = categoryNames.contains("Tipping Norms")

        // Optional: Change button appearance for disabled state
        val disabledAlpha = 0.5f
        val enabledAlpha = 1.0f

        binding.menuDressCode.alpha = if (binding.menuDressCode.isEnabled) enabledAlpha else disabledAlpha
        binding.menuCommunication.alpha = if (binding.menuCommunication.isEnabled) enabledAlpha else disabledAlpha
        binding.menuGreetings.alpha = if (binding.menuGreetings.isEnabled) enabledAlpha else disabledAlpha
        binding.menuEtiquette.alpha = if (binding.menuEtiquette.isEnabled) enabledAlpha else disabledAlpha
        binding.menuTipping.alpha = if (binding.menuTipping.isEnabled) enabledAlpha else disabledAlpha
    }

    private fun disableCategoryButtons() {
        binding.menuDressCode.isEnabled = false
        binding.menuCommunication.isEnabled = false
        binding.menuGreetings.isEnabled = false
        binding.menuEtiquette.isEnabled = false
        binding.menuTipping.isEnabled = false

        // Set disabled appearance
        val disabledAlpha = 0.5f
        binding.menuDressCode.alpha = disabledAlpha
        binding.menuCommunication.alpha = disabledAlpha
        binding.menuGreetings.alpha = disabledAlpha
        binding.menuEtiquette.alpha = disabledAlpha
        binding.menuTipping.alpha = disabledAlpha
    }

    private fun enableCategoryButtons() {
        // This will be overridden by updateCategoryButtonsVisibility() with actual availability
        binding.menuDressCode.isEnabled = true
        binding.menuCommunication.isEnabled = true
        binding.menuGreetings.isEnabled = true
        binding.menuEtiquette.isEnabled = true
        binding.menuTipping.isEnabled = true

        // Set enabled appearance
        val enabledAlpha = 1.0f
        binding.menuDressCode.alpha = enabledAlpha
        binding.menuCommunication.alpha = enabledAlpha
        binding.menuGreetings.alpha = enabledAlpha
        binding.menuEtiquette.alpha = enabledAlpha
        binding.menuTipping.alpha = enabledAlpha
    }

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

    private fun navigateToCategory(categoryName: String) {
        val selectedCountry = mainViewModel.selectedCountry.value
        if (selectedCountry == null) {
            Toast.makeText(requireContext(), "Please select a country first", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if category is available using ViewModel method
        if (!mainViewModel.isCategoryAvailable(categoryName)) {
            Toast.makeText(requireContext(), "$categoryName not available for ${selectedCountry.name}", Toast.LENGTH_SHORT).show()
            return
        }

        val category = mainViewModel.getCategoryByName(categoryName)
        if (category == null) {
            Toast.makeText(requireContext(), "Category not found", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putString("countryId", selectedCountry.id)
            putString("categoryId", category.id)
            putString("categoryName", categoryName)
            putString("countryName", selectedCountry.name)
        }

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
                    Toast.makeText(requireContext(), "Navigation not implemented for $categoryName", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToEmergency() {
        val selectedCountry = mainViewModel.selectedCountry.value
        if (selectedCountry == null) {
            Toast.makeText(requireContext(), "Please select a country first", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putString("countryId", selectedCountry.id)
            putString("countryName", selectedCountry.name)
        }

        try {
            findNavController().navigate(R.id.emergencyFragment, bundle)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Emergency feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSaved() {
        try {
            findNavController().navigate(R.id.savedFragment)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Saved feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToNotifications() {
        try {
            findNavController().navigate(R.id.notificationsFragment)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Notifications feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}