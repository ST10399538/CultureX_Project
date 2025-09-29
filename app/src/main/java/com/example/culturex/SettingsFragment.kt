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

    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        setupBackButton(view)
        setupBiometricToggle(view)
        setupLanguageOptions(view)
        setupOtherSettings(view)
    }

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

    private fun setupBiometricToggle(view: View) {
        val biometricSwitch = view.findViewById<Switch>(R.id.biometric_switch)

        biometricSwitch?.isChecked = sharedPrefsManager.isBiometricEnabled()

        if (!biometricHelper.isBiometricAvailable()) {
            biometricSwitch?.isEnabled = false
            Log.d("SettingsFragment", "Biometric not available: ${biometricHelper.getBiometricStatusMessage()}")
        } else {
            biometricSwitch?.isEnabled = true
            Log.d("SettingsFragment", "Biometric is available")
        }

        biometricSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && biometricHelper.isBiometricAvailable()) {
                sharedPrefsManager.setBiometricEnabled(true)
                Toast.makeText(requireContext(), "Biometric login enabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login enabled")
            } else if (isChecked && !biometricHelper.isBiometricAvailable()) {
                biometricSwitch.isChecked = false
                Toast.makeText(requireContext(), biometricHelper.getBiometricStatusMessage(), Toast.LENGTH_LONG).show()
                Log.w("SettingsFragment", "Attempted to enable biometric but not available")
            } else {
                sharedPrefsManager.setBiometricEnabled(false)
                Toast.makeText(requireContext(), "Biometric login disabled", Toast.LENGTH_SHORT).show()
                Log.d("SettingsFragment", "Biometric login disabled")
            }
        }
    }

    private fun setupLanguageOptions(view: View) {
        val languageCard = view.findViewById<View>(R.id.language_card)
        val languageOptions = view.findViewById<View>(R.id.language_options)

        languageCard?.setOnClickListener {
            if (languageOptions?.visibility == View.VISIBLE) {
                languageOptions.visibility = View.GONE
            } else {
                languageOptions?.visibility = View.VISIBLE
            }
        }

        languageOptions?.visibility = View.GONE
    }

    private fun setupOtherSettings(view: View) {
        val notificationsSwitch = view.findViewById<Switch>(R.id.notifications_switch)
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(),
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Toast.LENGTH_SHORT).show()
        }
    }
}