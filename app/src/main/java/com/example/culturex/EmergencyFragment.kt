package com.example.culturex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.EmergencyViewModel
import com.example.culturex.databinding.FragmentEmergencyBinding

class EmergencyFragment : Fragment(R.layout.fragment_emergency) {

    // View binding instance for accessing layout views
    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    // ViewModel instance for managing UI-related emergency data
    private val emergencyViewModel: EmergencyViewModel by viewModels()

    // Called when the fragment's view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEmergencyBinding.bind(view)

        // Retrieve arguments passed to this fragment (countryId and countryName)
        val countryId = arguments?.getString("countryId")
        val countryName = arguments?.getString("countryName")

        // If no countryId was provided, show a message and navigate back
        if (countryId == null) {
            Toast.makeText(requireContext(), "Please select a country first", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Set up LiveData observers and button click listeners
        setupObservers()
        setupClickListeners()

        // Load country details and emergency contacts
        emergencyViewModel.loadCountryEmergencyInfo(countryId)
    }

    // Observes changes in LiveData from the ViewModel
    private fun setupObservers() {
        // Observe country info (emergency contacts)
        emergencyViewModel.countryInfo.observe(viewLifecycleOwner) { country ->
            country?.let {
                binding.countryName.text = it.name
                binding.countryDescription.text = "Emergency Services in ${it.name}"

                val contacts = it.emergencyContacts as? Map<String, String>
                    ?: EmergencyViewModel.getDefaultEmergencyContacts(it.name)

                // Display contacts in the UI
                displayEmergencyContacts(contacts)
            }
        }

        emergencyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        // Observe error messages and show them in a Toast
        emergencyViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                emergencyViewModel.clearError()
            }
        }

        emergencyViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        emergencyViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                emergencyViewModel.clearError()
            }
        }
    }

    // Dynamically displays available emergency contacts in the UI
    private fun displayEmergencyContacts(contacts: Map<String, String>) {
        // Police
        contacts["police"]?.let { number ->
            binding.policeNumber.text = number
            binding.policeCard.setOnClickListener { dialNumber(number) }
        }

        // Medical
        contacts["medical"]?.let { number ->
            binding.medicalNumber.text = number
            binding.medicalCard.setOnClickListener { dialNumber(number) }
        }

        // Fire
        contacts["fire"]?.let { number ->
            binding.fireNumber.text = number
            binding.fireCard.setOnClickListener { dialNumber(number) }
        }

        // Tourist Helpline
        contacts["tourist_helpline"]?.let { number ->
            binding.touristHelplineNumber.text = number
            binding.touristHelplineCard.setOnClickListener { dialNumber(number) }
        }

        // General Emergency
        contacts["emergency"]?.let { number ->
            if (number != contacts["police"]) {
                binding.generalEmergencyNumber.text = number
                binding.generalEmergencyCard.isVisible = true
                binding.generalEmergencyCard.setOnClickListener { dialNumber(number) }
            }
        }

    }

    // Opens the phone dialer with the selected number
    private fun dialNumber(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to dial number", Toast.LENGTH_SHORT).show()
        }
    }

    // Sets up navigation and button click listeners
    private fun setupClickListeners() {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bottom navigation
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        binding.navSaved.setOnClickListener {
            try {
                findNavController().navigate(R.id.savedFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Saved feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }

        binding.navNotifications.setOnClickListener {
            try {
                findNavController().navigate(R.id.notificationsFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Notifications feature coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Clean up binding when the view is destroyed to avoid memory leaks
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