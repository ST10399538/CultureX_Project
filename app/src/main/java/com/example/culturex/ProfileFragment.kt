package com.example.culturex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.UserViewModel
import com.example.culturex.databinding.FragmentProfileBinding
import com.example.culturex.utils.SharedPreferencesManager
import android.widget.TextView



class ProfileFragment : Fragment(R.layout.fragment_profile2) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        sharedPrefsManager = SharedPreferencesManager(requireContext())

        setupObservers()
        setupClickListeners()
        loadUserProfile()
    }

    private fun setupObservers() {
        userViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {

            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {


        binding.editProfileButton.setOnClickListener {
            // Navigate to edit profile or show edit dialog
            Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }


    }

    private fun loadUserProfile() {
        val token = sharedPrefsManager.getAccessToken()
        if (token != null) {
            userViewModel.loadUserProfile(token)
        } else {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
