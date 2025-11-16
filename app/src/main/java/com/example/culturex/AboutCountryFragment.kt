package com.example.culturex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.AboutCountryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator


class AboutCountryFragment : Fragment(R.layout.fragment_about_country) {

    // ViewModel instance to handle data logic and lifecycle awareness
    private val viewModel: AboutCountryViewModel by viewModels()

    // Called after the fragment's view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve navigation arguments passed to this fragment
        val countryId = arguments?.getString("countryId")
        val countryName = arguments?.getString("countryName") ?: "Country"

        // If required arguments are missing, show a message and navigate back
        if (countryId == null) {
            Toast.makeText(requireContext(), "Missing country data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Set the country name in the title
        view.findViewById<TextView>(R.id.country_title)?.text = countryName

        // Setup UI elements
        setupObservers(view)
        setupClickListeners(view)

        // Trigger ViewModel to load country information
        viewModel.loadCountryInfo(countryId, countryName)
    }

    // Setup observers to react to changes in ViewModel LiveData
    private fun setupObservers(view: View) {
        // Observe country information
        viewModel.countryInfo.observe(viewLifecycleOwner) { info ->
            info?.let {
                // Geography Section
                view.findViewById<TextView>(R.id.geography_content)?.text =
                    it.geography ?: "Information not available"

                // Location Section
                view.findViewById<TextView>(R.id.location_content)?.text =
                    it.location ?: "Information not available"

                // History Section
                view.findViewById<TextView>(R.id.history_content)?.text =
                    it.history ?: "Information not available"

                // Culture Section
                view.findViewById<TextView>(R.id.culture_content)?.text =
                    it.culture ?: "Information not available"

                // Demographics Section
                view.findViewById<TextView>(R.id.demographics_content)?.text =
                    it.demographics ?: "Information not available"

                // Update quick facts
                view.findViewById<TextView>(R.id.capital_value)?.text =
                    it.capital ?: "N/A"
                view.findViewById<TextView>(R.id.population_value)?.text =
                    it.population ?: "N/A"
                view.findViewById<TextView>(R.id.language_value)?.text =
                    it.language ?: "N/A"
                view.findViewById<TextView>(R.id.currency_value)?.text =
                    it.currency ?: "N/A"
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<CircularProgressIndicator>(R.id.loading_indicator)?.isVisible = isLoading
            view.findViewById<View>(R.id.content_container)?.isVisible = !isLoading
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    // Setup click listeners for navigation and actions
    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialButton>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Collapsible sections
        setupCollapsibleSection(view, R.id.geography_header, R.id.geography_content)
        setupCollapsibleSection(view, R.id.location_header, R.id.location_content)
        setupCollapsibleSection(view, R.id.history_header, R.id.history_content)
        setupCollapsibleSection(view, R.id.culture_header, R.id.culture_content)
        setupCollapsibleSection(view, R.id.demographics_header, R.id.demographics_content)

        // Bottom navigation
        view.findViewById<View>(R.id.nav_emergency)?.setOnClickListener {
            navigateToEmergency()
        }

        view.findViewById<View>(R.id.nav_home)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        view.findViewById<View>(R.id.nav_saved)?.setOnClickListener {
            navigateToSaved()
        }
    }

    // Setup collapsible section functionality
    private fun setupCollapsibleSection(view: View, headerId: Int, contentId: Int) {
        val header = view.findViewById<MaterialCardView>(headerId)
        val content = view.findViewById<TextView>(contentId)

        header?.setOnClickListener {
            content?.let {
                it.isVisible = !it.isVisible
            }
        }
    }

    private fun navigateToEmergency() {
        val bundle = Bundle().apply {
            putString("countryId", arguments?.getString("countryId"))
            putString("countryName", arguments?.getString("countryName"))
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

}