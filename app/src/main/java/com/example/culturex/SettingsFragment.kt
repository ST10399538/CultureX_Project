package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        setupBasicSettings()
    }

    private fun setupBiometricToggle() {
        val biometricSwitch = view?.findViewById<Switch>(R.id.biometric_switch)

        if (biometricSwitch != null) {
            Log.d("SettingsFragment", "Found biometric switch")

            // Set initial state
            biometricSwitch.isChecked = sharedPrefsManager.isBiometricEnabled()

            // Enable/disable based on device capability
            if (!biometricHelper.isBiometricAvailable()) {
                biometricSwitch.isEnabled = false
                Log.d("SettingsFragment", "Biometric not available: ${biometricHelper.getBiometricStatusMessage()}")
            } else {
                biometricSwitch.isEnabled = true
                Log.d("SettingsFragment", "Biometric is available")
            }

            biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
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
        } else {
            Log.w("SettingsFragment", "Biometric switch not found in layout")
        }
    }

    private fun setupBasicSettings() {
        // Notifications switch (if it exists)
        val notificationsSwitch = view?.findViewById<Switch>(R.id.notifications_switch)
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT).show()
            Log.d("SettingsFragment", "Notifications ${if (isChecked) "enabled" else "disabled"}")
        }

        // Language card (if it exists)
        val languageCard = view?.findViewById<View>(R.id.language_card)
        languageCard?.setOnClickListener {
            Toast.makeText(requireContext(), "Language selection coming soon", Toast.LENGTH_SHORT).show()
        }

        Log.d("SettingsFragment", "Basic settings setup complete")
    }
}