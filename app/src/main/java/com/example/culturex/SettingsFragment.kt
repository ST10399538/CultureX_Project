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
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // Manager to handle SharedPreferences for storing user settings
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    // Helper class to handle biometric authentication
    private lateinit var biometricHelper: BiometricHelper

    // UI components
    private var isDarkMode = false
    private var isLanguageExpanded = false

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
        setupThemeToggle(view)
        setupNotificationsToggle(view)
        setupAboutSection(view)

        // Load saved preferences
        loadSavedSettings(view)
    }

    // Setup back navigation button
    private fun setupBackButton(view: View) {
        val backArrow = view.findViewById<ImageView>(R.id.back_arrow)
        backArrow?.setOnClickListener {
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
        val biometricSwitch = view.findViewById<SwitchMaterial>(R.id.biometric_switch)

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
            if (isChecked && biometricHelper.isBiometricAvailable()) {
                // Enable biometric login and save preference
                sharedPrefsManager.setBiometricEnabled(true)
                Toast.makeText(requireContext(), "✓ Biometric login enabled", Toast.LENGTH_SHORT).show()
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

    // Setup language selection with expandable card and radio buttons
    private fun setupLanguageOptions(view: View) {
        val languageCard = view.findViewById<MaterialCardView>(R.id.language_card)
        val languageOptions = view.findViewById<MaterialCardView>(R.id.language_options)
        val languageArrow = view.findViewById<ImageView>(R.id.language_arrow)
        val selectedLanguageText = view.findViewById<TextView>(R.id.selected_language_text)
        val radioGroup = view.findViewById<RadioGroup>(R.id.language_radio_group)

        // Toggle language options visibility
        languageCard?.setOnClickListener {
            isLanguageExpanded = !isLanguageExpanded

            if (isLanguageExpanded) {
                languageOptions?.visibility = View.VISIBLE
                rotateArrow(languageArrow, 0f, 180f)
            } else {
                languageOptions?.visibility = View.GONE
                rotateArrow(languageArrow, 180f, 0f)
            }
        }

        // Handle language selection
        radioGroup?.setOnCheckedChangeListener { _, checkedId ->
            val selectedLanguage = when (checkedId) {
                R.id.radio_english -> "English"
                R.id.radio_afrikaans -> "Afrikaans"
                R.id.radio_zulu -> "Zulu"
                R.id.radio_french -> "French"
                R.id.radio_spanish -> "Spanish"
                else -> "English"
            }

            // Update UI
            selectedLanguageText?.text = selectedLanguage

            // Save to SharedPreferences
            val languageCode = when (selectedLanguage) {
                "English" -> "en"
                "Afrikaans" -> "af"
                "Zulu" -> "zu"
                "French" -> "fr"
                "Spanish" -> "es"
                else -> "en"
            }
            sharedPrefsManager.updateUserProfile(
                displayName = null,
                preferredLanguage = languageCode
            )

            Toast.makeText(requireContext(), "Language changed to $selectedLanguage", Toast.LENGTH_SHORT).show()
            Log.d("SettingsFragment", "Language changed to: $selectedLanguage ($languageCode)")

            // Collapse the options after selection
            languageOptions?.visibility = View.GONE
            isLanguageExpanded = false
            rotateArrow(languageArrow, 180f, 0f)
        }

        // Initially hide the language options
        languageOptions?.visibility = View.GONE
    }

    // Setup theme toggle functionality
    private fun setupThemeToggle(view: View) {
        val themeCard = view.findViewById<MaterialCardView>(R.id.theme_card)
        val themeIcon = view.findViewById<ImageView>(R.id.theme_icon)
        val themeStatusText = view.findViewById<TextView>(R.id.theme_status_text)

        themeCard?.setOnClickListener {
            isDarkMode = !isDarkMode

            if (isDarkMode) {
                // Switch to dark mode
                themeIcon?.setImageResource(R.drawable.ic_dark_mode)
                themeStatusText?.text = "Dark Mode"
                Toast.makeText(requireContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Theme changed to Dark Mode")
            } else {
                // Switch to light mode
                themeIcon?.setImageResource(R.drawable.ic_light_mode)
                themeStatusText?.text = "Light Mode"
                Toast.makeText(requireContext(), "Switched to Light Mode", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Theme changed to Light Mode")
            }

            // TODO: Implement actual theme switching using AppCompatDelegate.setDefaultNightMode()
        }
    }

    // Setup notifications toggle
    private fun setupNotificationsToggle(view: View) {
        val notificationsSwitch = view.findViewById<SwitchMaterial>(R.id.notifications_switch)

        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "✓ Notifications enabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Notifications enabled")
            } else {
                Toast.makeText(requireContext(), "Notifications disabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Notifications disabled")
            }

            // TODO: Implement actual notification management
        }
    }

    // Setup about section clicks
    private fun setupAboutSection(view: View) {
        val privacyCard = view.findViewById<MaterialCardView>(R.id.privacy_card)

        privacyCard?.setOnClickListener {
            try {
                findNavController().navigate(R.id.privacyPolicyFragment)
                Log.d("SettingsFragment", "Navigating to Privacy Policy")
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Navigation to Privacy Policy failed", e)
                Toast.makeText(requireContext(), "Unable to open Privacy Policy", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Load saved settings from SharedPreferences
    private fun loadSavedSettings(view: View) {
        // Load saved language
        val savedLanguage = sharedPrefsManager.getPreferredLanguage() ?: "en"
        val selectedLanguageText = view.findViewById<TextView>(R.id.selected_language_text)
        val radioGroup = view.findViewById<RadioGroup>(R.id.language_radio_group)

        val languageName = when (savedLanguage) {
            "en" -> {
                radioGroup?.check(R.id.radio_english)
                "English"
            }
            "af" -> {
                radioGroup?.check(R.id.radio_afrikaans)
                "Afrikaans"
            }
            "zu" -> {
                radioGroup?.check(R.id.radio_zulu)
                "Zulu"
            }
            "fr" -> {
                radioGroup?.check(R.id.radio_french)
                "French"
            }
            "es" -> {
                radioGroup?.check(R.id.radio_spanish)
                "Spanish"
            }
            else -> "English"
        }

        selectedLanguageText?.text = languageName
        Log.d("SettingsFragment", "Loaded saved language: $languageName ($savedLanguage)")

        // Log current settings state
        sharedPrefsManager.logCurrentState()
    }

    // Helper function to animate arrow rotation
    private fun rotateArrow(arrow: ImageView?, fromDegrees: Float, toDegrees: Float) {
        val rotate = RotateAnimation(
            fromDegrees, toDegrees,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        arrow?.startAnimation(rotate)
    }

    override fun onResume() {
        super.onResume()
        Log.d("SettingsFragment", "Settings screen resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SettingsFragment", "Settings screen paused")
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