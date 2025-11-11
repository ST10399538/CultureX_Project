package com.example.culturex.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class BiometricHelper(private val fragment: Fragment) {

    // Interface to communicate authentication results back to the caller
    interface BiometricAuthListener {
        fun onAuthenticationSucceeded()
        fun onAuthenticationFailed()
        fun onAuthenticationError(errorCode: Int, errorMessage: String)
    }

    // BiometricPrompt instance for handling biometric authentication
    private var biometricPrompt: BiometricPrompt? = null
    // Prompt info that configures the biometric dialog
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    // Initialize the helper by setting up the biometric prompt
    init {
        setupBiometricPrompt()
    }

    // Private method to configure the BiometricPrompt and its callback
    private fun setupBiometricPrompt() {
        // Executor for handling callback events on the main thread
        val executor = ContextCompat.getMainExecutor(fragment.requireContext())

        // Initialize BiometricPrompt with callback methods for success, failure, and error
        biometricPrompt = BiometricPrompt(fragment, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e("BiometricHelper", "Authentication error: $errorCode - $errString")
                listener?.onAuthenticationError(errorCode, errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("BiometricHelper", "Authentication succeeded")
                listener?.onAuthenticationSucceeded()
                // Notify listener of success
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w("BiometricHelper", "Authentication failed")
                listener?.onAuthenticationFailed()
                // Notify listener of failed authentication
            }
        })

        // Configure the appearance and behavior of the biometric prompt dialog
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setDescription("Place your finger on the fingerprint sensor to authenticate")
            .setNegativeButtonText("Use Password")
            .setConfirmationRequired(false)
            .build()
    }

    // Listener to receive authentication callbacks
    private var listener: BiometricAuthListener? = null

    // Setter to attach a listener from outside the class
    fun setAuthListener(listener: BiometricAuthListener) {
        this.listener = listener
    }

    // Checks if the device supports biometric authentication and if the user has enrolled credentials
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(fragment.requireContext())
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("BiometricHelper", "App can authenticate using biometrics")
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("BiometricHelper", "No biometric features available on this device")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("BiometricHelper", "Biometric features are currently unavailable")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("BiometricHelper", "The user hasn't associated any biometric credentials with their account")
                false
            }
            else -> {
                Log.e("BiometricHelper", "Unknown biometric status")
                false
            }
        }
    }

    // Returns a human-readable message describing the device's biometric capability/status
    fun getBiometricStatusMessage(): String {
        val biometricManager = BiometricManager.from(fragment.requireContext())
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> "Biometric authentication is available"
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric hardware available on this device"
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric hardware is currently unavailable"
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometric credentials enrolled. Please set up fingerprint in device settings"
            else -> "Biometric authentication is not available"
        }
    }

    // Trigger biometric authentication if available; otherwise, return error via listener
    fun authenticate() {
        if (isBiometricAvailable()) {
            biometricPrompt?.authenticate(promptInfo!!)
        } else {
            listener?.onAuthenticationError(-1, getBiometricStatusMessage())
        }
    }

    // Static method to check if biometric authentication is supported on a given context/device
    companion object {
        fun isBiometricSupported(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
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