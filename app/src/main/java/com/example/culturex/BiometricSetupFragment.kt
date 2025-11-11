package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.utils.BiometricHelper
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class BiometricSetupFragment : Fragment(R.layout.fragment_biometric_setup) {

    // SharedPreferences manager to save user preferences (e.g., biometric enabled/disabled)
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    // Helper class that wraps BiometricPrompt setup and authentication handling
    private lateinit var biometricHelper: BiometricHelper

    // UI components
    private lateinit var continueButton: Button
    private lateinit var skipButton: TextView
    private lateinit var descriptionText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        initializeViews(view)
        setupBiometric()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        continueButton = view.findViewById(R.id.continue_button)
        skipButton = view.findViewById(R.id.skip_button)
        descriptionText = view.findViewById(R.id.description_text)
    }

    // Configures biometric authentication behavior and updates UI
    private fun setupBiometric() {
        // Set listener to handle authentication callbacks
        biometricHelper.setAuthListener(object : BiometricHelper.BiometricAuthListener {
            override fun onAuthenticationSucceeded() {
                Log.d("BiometricSetupFragment", "Biometric setup successful")

                // Enable biometric login
                sharedPrefsManager.setBiometricEnabled(true)
                Toast.makeText(requireContext(), "Biometric authentication enabled successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to main app
                navigateToMain()
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(requireContext(), "Biometric setup failed. Try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                Log.e("BiometricSetupFragment", "Biometric error: $errorCode - $errorMessage")

                when (errorCode) {
                    androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED -> {
                        // User canceled, just show a message
                        Toast.makeText(requireContext(), "Biometric setup canceled", Toast.LENGTH_SHORT).show()
                    }
                    androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        // User chose "Skip for now"
                        Toast.makeText(requireContext(), "You can enable biometric login later in settings", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Biometric setup error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        // Update UI based on biometric availability
        if (biometricHelper.isBiometricAvailable()) {
            continueButton.text = "Enable Biometric Login"
            continueButton.isEnabled = true
            descriptionText.text = "Biometric authentication lets you log in quickly and securely using this device's fingerprint or face recognition."
        } else {
            continueButton.text = "Biometric Not Available"
            continueButton.isEnabled = false
            descriptionText.text = biometricHelper.getBiometricStatusMessage()
        }
    }

    private fun setupClickListeners() {
        continueButton.setOnClickListener {
            if (biometricHelper.isBiometricAvailable()) {
                // Start biometric enrollment/test
                biometricHelper.authenticate()
            } else {
                Toast.makeText(requireContext(), biometricHelper.getBiometricStatusMessage(), Toast.LENGTH_LONG).show()
                navigateToLogin()
            }
        }

        skipButton.setOnClickListener {
            // Skip biometric setup - go to login since user needs to authenticate
            Toast.makeText(requireContext(), "Please login to continue. You can enable biometric login later in Settings", Toast.LENGTH_LONG).show()
            navigateToLogin()
        }
    }

    private fun navigateToMain() {
        try {
            // Navigate to main fragment (only after successful biometric setup)
            findNavController().navigate(R.id.action_biometric_setup_to_main)
        } catch (e: Exception) {
            Log.e("BiometricSetupFragment", "Navigation to main failed", e)
            try {
                // Fallback to onboarding if main navigation fails
                findNavController().navigate(R.id.action_biometric_setup_to_onboarding)
            } catch (e2: Exception) {
                Log.e("BiometricSetupFragment", "Onboarding navigation also failed", e2)
                // Last resort - navigate to main directly
                findNavController().navigate(R.id.mainFragment)
            }
        }
    }

    private fun navigateToLogin() {
        try {
            // Navigate back to login page for authentication
            findNavController().navigate(R.id.action_biometric_setup_to_login)
        } catch (e: Exception) {
            Log.e("BiometricSetupFragment", "Navigation to login failed", e)
            try {
                // Try alternative navigation
                findNavController().popBackStack(R.id.loginFragment, false)
            } catch (e2: Exception) {
                Log.e("BiometricSetupFragment", "Alternative login navigation also failed", e2)
                Toast.makeText(requireContext(), "Please restart the app and login", Toast.LENGTH_LONG).show()
            }
        }
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
