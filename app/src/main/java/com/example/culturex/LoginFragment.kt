package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.AuthViewModel
import com.example.culturex.utils.SharedPreferencesManager
import com.example.culturex.utils.BiometricHelper
import com.google.android.material.textfield.TextInputEditText
import android.util.Log

class LoginFragment : Fragment(R.layout.fragment_login) {
    // It manages login with email/password, admin test login, and biometric login.

    // UI components
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var fingerprintContainer: View
    private lateinit var fingerprintIcon: ImageView
    private lateinit var fingerprintText: TextView

    // Helpers & managers, storing data, handling authentification and viewmodel for login API call
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper
    private val authViewModel: AuthViewModel by viewModels()

    // Called when the fragment’s view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize helpers
        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        // Setup fragments
        initializeViews(view)
        setupBiometric()
        setupObservers()
        setupClickListeners()
    }

    // Find and bind all view elements from XML layout
    private fun initializeViews(view: View) {
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        signUpLink = view.findViewById(R.id.sign_up_link)
        forgotPasswordLink = view.findViewById(R.id.forgot_password)
        fingerprintContainer = view.findViewById(R.id.fingerprint_container)
        fingerprintIcon = view.findViewById(R.id.fingerprint_icon)
        fingerprintText = view.findViewById(R.id.fingerprint_text)
    }

    // Configure biometric authentication
    private fun setupBiometric() {
        biometricHelper.setAuthListener(object : BiometricHelper.BiometricAuthListener {
            override fun onAuthenticationSucceeded() {
                Log.d("LoginFragment", "Biometric authentication succeeded")
                val savedEmail = sharedPrefsManager.getEmail()
                val savedUserId = sharedPrefsManager.getUserId()

                // Only proceed if saved credentials exist
                if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
                    handleBiometricLogin()
                } else {
                    Toast.makeText(requireContext(), "No saved credentials found. Please login with email and password first.",
                        Toast.LENGTH_LONG).show()
                }
            }

            // User’s fingerprint/face didn’t match
            override fun onAuthenticationFailed() {
                Toast.makeText(requireContext(), "Biometric authentication failed. Try again.", Toast.LENGTH_SHORT).show()
            }

            // Handle system-level errors
            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                Log.e("LoginFragment", "Biometric error: $errorCode - $errorMessage")
                when (errorCode) {
                    androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED -> {
                        // User canceled, just show a message
                    }
                    androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Toast.makeText(requireContext(), "Please login with email and password", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Biometric error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        // Show or hide fingerprint icon depending on settings & availability
        updateFingerprintVisibility()
    }

    // Show fingerprint option only if supported and user has enabled it
    private fun updateFingerprintVisibility() {
        val isBiometricAvailable = biometricHelper.isBiometricAvailable()
        val hasSavedCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
        val isBiometricEnabled = sharedPrefsManager.isBiometricEnabled()

        if (isBiometricAvailable && hasSavedCredentials && isBiometricEnabled) {
            fingerprintContainer.visibility = View.VISIBLE
            fingerprintText.visibility = View.VISIBLE
            fingerprintText.text = "Tap fingerprint to login as ${sharedPrefsManager.getEmail()}"
        } else {
            fingerprintContainer.visibility = View.GONE
            fingerprintText.visibility = View.GONE
        }
    }
    // Login flow when biometric authentication succeeds
    private fun handleBiometricLogin() {
        val savedEmail = sharedPrefsManager.getEmail()
        val savedUserId = sharedPrefsManager.getUserId()
        val savedDisplayName = sharedPrefsManager.getDisplayName()

        if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Welcome back, ${savedDisplayName ?: savedEmail}!", Toast.LENGTH_SHORT).show()

            try {
                findNavController().navigate(R.id.mainFragment)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Navigation failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            // Fallback if credentials are missing
        } else {
            Toast.makeText(requireContext(), "Please login with email and password first to enable biometric login.", Toast.LENGTH_LONG).show()
        }
    }
    // Setup LiveData observers from ViewModel
    private fun setupObservers() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { authResponse ->
                        Log.d("LoginFragment", "Login successful, saving auth data")

                        // Preserve existing phone number if it exists
                        val existingPhone = sharedPrefsManager.getPhoneNumber()

                        // Save auth data
                        sharedPrefsManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.user?.id,
                            email = authResponse.user?.email,
                            displayName = authResponse.user?.displayName,
                            phoneNumber = existingPhone // Preserve phone number
                        )

                        if (biometricHelper.isBiometricAvailable()) {
                            sharedPrefsManager.setBiometricEnabled(true)
                            Toast.makeText(requireContext(), "Login successful! Biometric login enabled for next time.",
                                Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                        }

                        // Navigate to onboarding (fallback to main if it fails)
                        try {
                            findNavController().navigate(R.id.action_login_to_onboarding)
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "Navigation to onboarding failed", e)
                            try {
                                findNavController().navigate(R.id.mainFragment)
                            } catch (e2: Exception) {
                                Log.e("LoginFragment", "Navigation to main also failed", e2)
                                Toast.makeText(requireContext(), "Navigation error: ${e2.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("LoginFragment", "Login failed: ${error.message}")
                        Toast.makeText(requireContext(), error.message ?: "Login failed", Toast.LENGTH_LONG).show()
                    }
                )
                authViewModel.clearResults()
                // Login failed
                // Reset state after handling
            }
        }

        // Observe loading state (disable button while logging in)
        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loginButton.isEnabled = !isLoading
            loginButton.text = if (isLoading) "Logging in..." else "Log In"
            if (isLoading) {
                loginButton.alpha = 0.6f
                fingerprintContainer.isEnabled = false
            } else {
                loginButton.alpha = 1.0f
                fingerprintContainer.isEnabled = true
            }
        }
    }

    // Handle button clicks and navigation events
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Special case: admin test login
            if (email == "admin" && password == "admin") {
                Log.d("LoginFragment", "Admin test login")
                handleAdminLogin()
                return@setOnClickListener
            }
// Validate and attempt login
            if (validateInput(email, password)) {
                Log.d("LoginFragment", "Attempting login for: $email")
                authViewModel.login(email, password)
            }
        }

        fingerprintContainer.setOnClickListener {
            if (biometricHelper.isBiometricAvailable()) {
                biometricHelper.authenticate()
            } else {
                Toast.makeText(requireContext(), biometricHelper.getBiometricStatusMessage(), Toast.LENGTH_LONG).show()
            }
        }

        signUpLink.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_login_to_signup)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Navigation to signup failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Placeholder for forgot password
        forgotPasswordLink.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        // Back button navigation
        view?.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("LoginFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        // Social logins (placeholders)
        view?.findViewById<View>(R.id.google_login)?.setOnClickListener {
            Toast.makeText(requireContext(), "Google login coming soon", Toast.LENGTH_SHORT).show()
        }

        view?.findViewById<View>(R.id.apple_login)?.setOnClickListener {
            Toast.makeText(requireContext(), "Apple login coming soon", Toast.LENGTH_SHORT).show()
        }

        view?.findViewById<View>(R.id.facebook_login)?.setOnClickListener {
            Toast.makeText(requireContext(), "Facebook login coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle admin (mock) login for testing
    private fun handleAdminLogin() {
        sharedPrefsManager.saveAuthData(
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token",
            userId = "admin_id",
            email = "admin@culturex.com",
            displayName = "Administrator",
            phoneNumber = "+27123456789" // Add mock phone for admin
        )

        if (biometricHelper.isBiometricAvailable()) {
            sharedPrefsManager.setBiometricEnabled(true)
        }

        Toast.makeText(requireContext(), "Admin login successful!", Toast.LENGTH_SHORT).show()
        try {
            findNavController().navigate(R.id.mainFragment)
        } catch (e: Exception) {
            Log.e("LoginFragment", "Admin navigation failed", e)
            Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Validate email and password inputs
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Please enter a valid email"
            isValid = false
        } else {
            emailInput.error = null
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordInput.error = null
        }

        return isValid
    }

    // Refresh fingerprint visibility whenever fragment is resumed
    override fun onResume() {
        super.onResume()
        updateFingerprintVisibility()
    }

    // Clear login results when view is destroyed to avoid stale state
    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.clearResults()
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