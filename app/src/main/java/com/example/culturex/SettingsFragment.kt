package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.utils.BiometricHelper
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        setupBiometricToggle()
        setupLanguageOptions()
        setupOtherSettings()
    }

    private fun setupBiometricToggle() {
        val biometricSwitch = view?.findViewById<Switch>(R.id.biometric_switch)

        // Set initial state
        biometricSwitch?.isChecked = sharedPrefsManager.isBiometricEnabled()

        // Enable/disable based on device capability
        if (!biometricHelper.isBiometricAvailable()) {
            biometricSwitch?.isEnabled = false
            Log.d("SettingsFragment", "Biometric not available: ${biometricHelper.getBiometricStatusMessage()}")
        } else {
            biometricSwitch?.isEnabled = true
            Log.d("SettingsFragment", "Biometric is available")
        }

        biometricSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && biometricHelper.isBiometricAvailable()) {
                // Enable biometric login
                sharedPrefsManager.setBiometricEnabled(true)
                Toast.makeText(requireContext(), "Biometric login enabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login enabled")
            } else if (isChecked && !biometricHelper.isBiometricAvailable()) {
                // Device doesn't support biometric
                biometricSwitch.isChecked = false
                Toast.makeText(requireContext(), biometricHelper.getBiometricStatusMessage(), Toast.LENGTH_LONG).show()
                Log.w("SettingsFragment", "Attempted to enable biometric but not available")
            } else {
                // Disable biometric login
                sharedPrefsManager.setBiometricEnabled(false)
                Toast.makeText(requireContext(), "Biometric login disabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login disabled")
            }
        }
    }

    private fun setupLanguageOptions() {
        val languageCard = view?.findViewById<View>(R.id.language_card)
        val languageOptions = view?.findViewById<View>(R.id.language_options)

        languageCard?.setOnClickListener {
            // Toggle language options visibility
            if (languageOptions?.visibility == View.VISIBLE) {
                languageOptions.visibility = View.GONE
            } else {
                languageOptions?.visibility = View.VISIBLE
            }
        }

        // Set up language selection
        languageOptions?.visibility = View.GONE
    }

    private fun setupOtherSettings() {
        // Notifications toggle
        val notificationsSwitch = view?.findViewById<Switch>(R.id.notifications_switch)
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT).show()
        }

        // Only set up click listeners for views that exist in your layout
        // Remove references to export_data_card and theme_card since they don't exist
    }
}