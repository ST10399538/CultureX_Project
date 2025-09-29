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

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!
    private val emergencyViewModel: EmergencyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEmergencyBinding.bind(view)

        val countryId = arguments?.getString("countryId")
        val countryName = arguments?.getString("countryName")

        if (countryId == null) {
            Toast.makeText(requireContext(), "Please select a country first", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupObservers()
        setupClickListeners()

        // Load country details and emergency contacts
        emergencyViewModel.loadCountryEmergencyInfo(countryId)
    }

    private fun setupObservers() {
        emergencyViewModel.countryInfo.observe(viewLifecycleOwner) { country ->
            country?.let {
                binding.countryName.text = it.name
                binding.countryDescription.text = "Emergency Services in ${it.name}"

                val contacts = it.emergencyContacts as? Map<String, String>
                    ?: EmergencyViewModel.getDefaultEmergencyContacts(it.name)

                displayEmergencyContacts(contacts)
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

    private fun dialNumber(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to dial number", Toast.LENGTH_SHORT).show()
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
