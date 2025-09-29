package com.example.culturex

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var profileImage: ImageView
    private lateinit var editProfileButton: Button
    private lateinit var downloadOption: LinearLayout
    private lateinit var settingsOption: LinearLayout
    private lateinit var itineraryOption: LinearLayout
    private lateinit var signOutOption: LinearLayout

    // Bottom navigation
    private lateinit var navEmergency: LinearLayout
    private lateinit var navHome: LinearLayout
    private lateinit var navSaved: LinearLayout
    private lateinit var navNotifications: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())

        initializeViews(view)
        loadProfilePicture()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Reload profile picture when returning from edit profile
        loadProfilePicture()
    }

    private fun initializeViews(view: View) {
        profileImage = view.findViewById(R.id.profile_image)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        downloadOption = view.findViewById(R.id.download_option)
        settingsOption = view.findViewById(R.id.settings_option)
        itineraryOption = view.findViewById(R.id.itinerary_option)
        signOutOption = view.findViewById(R.id.sign_out_option)

        // Bottom navigation
        navEmergency = view.findViewById(R.id.nav_emergency)
        navHome = view.findViewById(R.id.nav_home)
        navSaved = view.findViewById(R.id.nav_saved)
        navNotifications = view.findViewById(R.id.nav_notifications)
    }

    private fun loadProfilePicture() {
        val profilePictureUrl = sharedPrefsManager.getProfilePictureUrl()
        profilePictureUrl?.let { urlString ->
            try {
                val uri = Uri.parse(urlString)
                profileImage.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error loading profile picture", e)
            }
        }
    }

    private fun setupClickListeners() {
        // Edit Profile Button
        editProfileButton.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profile_to_editProfile)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Navigation to edit profile failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Download Option
        downloadOption.setOnClickListener {
            Toast.makeText(requireContext(), "Download functionality coming soon",
                Toast.LENGTH_SHORT).show()
        }

        // Settings Option
        settingsOption.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profile_to_settings)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Navigation to settings failed", e)
                Toast.makeText(requireContext(), "Settings feature coming soon",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Itinerary Plans Option
        itineraryOption.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profile_to_itinerary)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Navigation to itinerary failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Sign Out Option
        signOutOption.setOnClickListener {
            showSignOutConfirmation()
        }

        // Bottom Navigation
        navEmergency.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_profile_to_emergency)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Emergency feature coming soon",
                    Toast.LENGTH_SHORT).show()
            }
        }

        navHome.setOnClickListener {
            try {
                findNavController().navigate(R.id.mainFragment)
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Navigation to home failed", e)
            }
        }

        navSaved.setOnClickListener {
            try {
                findNavController().navigate(R.id.savedFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Saved feature coming soon",
                    Toast.LENGTH_SHORT).show()
            }
        }

        navNotifications.setOnClickListener {
            try {
                findNavController().navigate(R.id.notificationsFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Notifications feature coming soon",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignOutConfirmation() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign Out") { _, _ ->
                performSignOut()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSignOut() {
        // Clear all saved data
        sharedPrefsManager.logout()

        Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to login screen
        try {
            findNavController().navigate(R.id.loginFragment)
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Navigation to login failed", e)
            activity?.finish() // Close app if navigation fails
        }
    }
}