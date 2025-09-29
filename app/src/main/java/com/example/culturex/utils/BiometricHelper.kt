package com.example.culturex.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class BiometricHelper(private val fragment: Fragment) {

    interface BiometricAuthListener {
        fun onAuthenticationSucceeded()
        fun onAuthenticationFailed()
        fun onAuthenticationError(errorCode: Int, errorMessage: String)
    }

    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    init {
        setupBiometricPrompt()
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(fragment.requireContext())

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
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Log.w("BiometricHelper", "Authentication failed")
                listener?.onAuthenticationFailed()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setDescription("Place your finger on the fingerprint sensor to authenticate")
            .setNegativeButtonText("Use Password")
            .setConfirmationRequired(false)
            .build()
    }

    private var listener: BiometricAuthListener? = null

    fun setAuthListener(listener: BiometricAuthListener) {
        this.listener = listener
    }

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

    fun authenticate() {
        if (isBiometricAvailable()) {
            biometricPrompt?.authenticate(promptInfo!!)
        } else {
            listener?.onAuthenticationError(-1, getBiometricStatusMessage())
        }
    }

    companion object {
        fun isBiometricSupported(context: Context): Boolean {
            val biometricManager = BiometricManager.from(context)
            return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
        }
    }
}