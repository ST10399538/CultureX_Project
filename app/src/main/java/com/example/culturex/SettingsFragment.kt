package com.example.culturex

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentSettingsBinding
import com.example.culturex.utils.BiometricHelper
import com.example.culturex.utils.SharedPreferencesManager
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper

    private var isDarkMode = false
    private var isLanguageExpanded = false
    private var isLanguageChanging = false
    private var isProgrammaticChange = false
    private var biometricSwitch: SwitchMaterial? = null

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        setupBackButton()
        setupBiometricToggle()
        setupLanguageOptions()
        setupThemeToggle()
        setupNotificationsToggle()
        setupAboutSection()
        loadSavedSettings()
    }

    /** BACK BUTTON **/
    private fun setupBackButton() {
        binding.backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }
    }

    /** BIOMETRIC **/
    private fun setupBiometricToggle() {
        biometricSwitch = binding.biometricSwitch
        val isAvailable = biometricHelper.isBiometricAvailable()
        val hasSavedCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
        val currentState = sharedPrefsManager.isBiometricEnabled()

        biometricSwitch?.isChecked = currentState
        when {
            !isAvailable -> disableBiometric(getString(R.string.biometric_not_available))
            !hasSavedCredentials -> disableBiometric(getString(R.string.biometric_login_first))
            else -> enableBiometric()
        }

        biometricSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isEnabled) return@setOnCheckedChangeListener
            sharedPrefsManager.setBiometricEnabled(isChecked)
            val msg = if (isChecked) getString(R.string.biometric_enabled) else getString(R.string.biometric_disabled)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableBiometric(message: String) {
        biometricSwitch?.isEnabled = false
        biometricSwitch?.isChecked = false
        binding.biometricSubtext.text = message
    }

    private fun enableBiometric() {
        biometricSwitch?.isEnabled = true
        binding.biometricSubtext.text = getString(R.string.biometric_subtext)
    }

    /** LANGUAGE **/
    private fun setupLanguageOptions() {
        val languageCard = binding.languageCard
        val languageOptions = binding.languageOptions
        val languageArrow = binding.languageArrow
        val radioGroup = binding.languageRadioGroup

        languageCard.setOnClickListener {
            isLanguageExpanded = !isLanguageExpanded
            languageOptions.visibility = if (isLanguageExpanded) View.VISIBLE else View.GONE
            rotateArrow(
                languageArrow,
                if (isLanguageExpanded) 0f else 180f,
                if (isLanguageExpanded) 180f else 0f
            )
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (isLanguageChanging || isProgrammaticChange) return@setOnCheckedChangeListener

            val currentLanguage = sharedPrefsManager.getPreferredLanguage()
            val languageCode = when (checkedId) {
                R.id.radio_english -> "en"
                R.id.radio_afrikaans -> "af"
                R.id.radio_zulu -> "zu"
                R.id.radio_french -> "fr"
                R.id.radio_spanish -> "es"
                else -> "en"
            }

            if (languageCode != currentLanguage) {
                changeLanguage(languageCode)
            }
        }

        languageOptions.visibility = View.GONE
    }

    private fun changeLanguage(languageCode: String) {
        val currentLanguage = sharedPrefsManager.getPreferredLanguage()
        if (currentLanguage == languageCode) return

        isLanguageChanging = true
        sharedPrefsManager.setPreferredLanguage(languageCode)
        activity?.recreate()  // triggers attachBaseContext in BaseActivity
    }

    /** THEME **/
    private fun setupThemeToggle() {
        val themeCard = binding.themeCard
        themeCard.setOnClickListener {
            isDarkMode = !isDarkMode
            binding.themeStatusText.text = if (isDarkMode) getString(R.string.dark_mode) else getString(R.string.light_mode)
            binding.themeIcon.setImageResource(if (isDarkMode) R.drawable.ic_dark_mode else R.drawable.ic_light_mode)
            Toast.makeText(requireContext(), getString(R.string.theme_changed), Toast.LENGTH_SHORT).show()
        }
    }

    /** NOTIFICATIONS **/
    private fun setupNotificationsToggle() {
        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefsManager.setNotificationsEnabled(isChecked)
            val msg = if (isChecked) getString(R.string.notifications_enabled) else getString(R.string.notifications_disabled)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    /** ABOUT / PRIVACY **/
    private fun setupAboutSection() {
        binding.privacyCard.setOnClickListener {
            try {
                findNavController().navigate(R.id.privacyPolicyFragment)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Navigation to Privacy Policy failed", e)
                Toast.makeText(requireContext(), "Unable to open Privacy Policy", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** LOAD SAVED SETTINGS **/
    private fun loadSavedSettings() {
        val savedLang = sharedPrefsManager.getPreferredLanguage()
        val langMap = mapOf(
            "en" to R.id.radio_english,
            "af" to R.id.radio_afrikaans,
            "zu" to R.id.radio_zulu,
            "fr" to R.id.radio_french,
            "es" to R.id.radio_spanish
        )

        isProgrammaticChange = true
        binding.languageRadioGroup.check(langMap[savedLang] ?: R.id.radio_english)
        isProgrammaticChange = false

        val langName = when (savedLang) {
            "en" -> getString(R.string.language_english)
            "af" -> getString(R.string.language_afrikaans)
            "zu" -> getString(R.string.language_zulu)
            "fr" -> getString(R.string.language_french)
            "es" -> getString(R.string.language_spanish)
            else -> getString(R.string.language_english)
        }
        binding.selectedLanguageText.text = langName
    }

    /** UTILITIES **/
    private fun rotateArrow(arrow: ImageView?, from: Float, to: Float) {
        val rotate = RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        arrow?.startAnimation(rotate)
    }

    override fun onResume() {
        super.onResume()
        isLanguageChanging = false
        refreshBiometric()
    }

    private fun refreshBiometric() {
        val isAvailable = biometricHelper.isBiometricAvailable()
        val hasCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
        val currentState = sharedPrefsManager.isBiometricEnabled()
        biometricSwitch?.let { switch ->
            switch.isChecked = currentState
            when {
                !isAvailable -> disableBiometric(getString(R.string.biometric_not_available))
                !hasCredentials -> disableBiometric(getString(R.string.biometric_login_first))
                else -> enableBiometric()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        biometricSwitch = null
        _binding = null
    }
}
