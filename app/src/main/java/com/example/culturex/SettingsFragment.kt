package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.utils.BiometricHelper
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // Manager to handle SharedPreferences for storing user settings
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    // Helper class to handle biometric authentication
    private lateinit var biometricHelper: BiometricHelper

    // Called when the fragment's view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize SharedPreferences manager and Biometric helper
        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        // Setup UI components and their behavior
        setupBackButton(view)
        setupBiometricToggle(view)
        setupLanguageOptions(view)
        setupOtherSettings(view)
    }
    // Setup back navigation button
    private fun setupBackButton(view: View) {
        val backArrow = view.findViewById<ImageView>(R.id.back_arrow)
        backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }
    }

    // Setup the biometric login toggle switch
    private fun setupBiometricToggle(view: View) {
        val biometricSwitch = view.findViewById<Switch>(R.id.biometric_switch)

        // Set the switch state based on saved preference
        biometricSwitch?.isChecked = sharedPrefsManager.isBiometricEnabled()

        // Check if device supports biometric authentication
        if (!biometricHelper.isBiometricAvailable()) {
            biometricSwitch?.isEnabled = false
            Log.d("SettingsFragment", "Biometric not available: ${biometricHelper.getBiometricStatusMessage()}")
        } else {
            biometricSwitch?.isEnabled = true
            Log.d("SettingsFragment", "Biometric is available")
        }

        // Handle toggle changes
        biometricSwitch?.setOnCheckedChangeListener { _, isChecked ->
            // Enable biometric login and save preference
            if (isChecked && biometricHelper.isBiometricAvailable()) {
                sharedPrefsManager.setBiometricEnabled(true)
                Toast.makeText(requireContext(), "Biometric login enabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login enabled")

            } else if (isChecked && !biometricHelper.isBiometricAvailable()) {
                // Prevent enabling if biometric is not available
                biometricSwitch.isChecked = false
                Toast.makeText(requireContext(), biometricHelper.getBiometricStatusMessage(), Toast.LENGTH_LONG).show()
                Log.w("SettingsFragment", "Attempted to enable biometric but not available")
            } else {
                // Disable biometric login and save preference
                sharedPrefsManager.setBiometricEnabled(false)
                Toast.makeText(requireContext(), "Biometric login disabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login disabled")
            }
        }
    }

    // Setup language selection card and expandable options
    private fun setupLanguageOptions(view: View) {
        val languageCard = view.findViewById<View>(R.id.language_card)
        val languageOptions = view.findViewById<View>(R.id.language_options)

        languageCard?.setOnClickListener {
            // Toggle visibility of language options
            if (languageOptions?.visibility == View.VISIBLE) {
                languageOptions.visibility = View.GONE
            } else {
                languageOptions?.visibility = View.VISIBLE
            }
        }

        // Initially hide the language options
        languageOptions?.visibility = View.GONE
    }

    // Setup other settings like notifications
    private fun setupOtherSettings(view: View) {
        val notificationsSwitch = view.findViewById<Switch>(R.id.notifications_switch)
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT).show()
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