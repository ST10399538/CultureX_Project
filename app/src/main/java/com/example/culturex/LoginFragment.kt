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
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var fingerprintContainer: View
    private lateinit var fingerprintIcon: ImageView
    private lateinit var fingerprintText: TextView
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper
    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        initializeViews(view)
        setupBiometric()
        setupObservers()
        setupClickListeners()
    }

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

    private fun setupBiometric() {
        biometricHelper.setAuthListener(object : BiometricHelper.BiometricAuthListener {
            override fun onAuthenticationSucceeded() {
                Log.d("LoginFragment", "Biometric authentication succeeded")

                // Get saved credentials for automatic login
                val savedEmail = sharedPrefsManager.getEmail()
                val savedUserId = sharedPrefsManager.getUserId()

                if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
                    // User has previous login data, proceed with biometric login
                    handleBiometricLogin()
                } else {
                    Toast.makeText(requireContext(), "No saved credentials found. Please login with email and password first.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(requireContext(), "Biometric authentication failed. Try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                Log.e("LoginFragment", "Biometric error: $errorCode - $errorMessage")

                when (errorCode) {
                    androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED -> {
                        // User canceled, don't show error message
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

        // Show/hide fingerprint option based on availability and saved credentials
        updateFingerprintVisibility()
    }

    private fun updateFingerprintVisibility() {
        val isBiometricAvailable = biometricHelper.isBiometricAvailable()
        val hasSavedCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
        val isBiometricEnabled = sharedPrefsManager.isBiometricEnabled()

        if (isBiometricAvailable && hasSavedCredentials && isBiometricEnabled) {
            fingerprintContainer.visibility = View.VISIBLE
            fingerprintText.visibility = View.VISIBLE
            fingerprintText.text = "Tap fingerprint to login as ${sharedPrefsManager.getEmail()}"
        } else if (isBiometricAvailable && !hasSavedCredentials) {
            fingerprintContainer.visibility = View.GONE
            fingerprintText.visibility = View.GONE
        } else if (!isBiometricAvailable) {
            fingerprintContainer.visibility = View.GONE
            fingerprintText.visibility = View.GONE
        } else {
            fingerprintContainer.visibility = View.GONE
            fingerprintText.visibility = View.GONE
        }
    }

    private fun handleBiometricLogin() {
        // Simulate successful biometric login by restoring saved auth data
        val savedEmail = sharedPrefsManager.getEmail()
        val savedUserId = sharedPrefsManager.getUserId()
        val savedDisplayName = sharedPrefsManager.getDisplayName()

        if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Welcome back, ${savedDisplayName ?: savedEmail}!", Toast.LENGTH_SHORT).show()

            // Navigate to main fragment
            try {
                findNavController().navigate(R.id.mainFragment)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Navigation failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please login with email and password first to enable biometric login.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { authResponse ->
                        Log.d("LoginFragment", "Login successful, saving auth data")

                        // Save auth data
                        sharedPrefsManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.user?.id,
                            email = authResponse.user?.email,
                            displayName = authResponse.user?.displayName
                        )

                        // Enable biometric login if available
                        if (biometricHelper.isBiometricAvailable()) {
                            sharedPrefsManager.setBiometricEnabled(true)
                            Toast.makeText(requireContext(), "Login successful! Biometric login enabled for next time.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                        }

                        // Navigate to main fragment
                        try {
                            findNavController().navigate(R.id.mainFragment)
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "Navigation failed", e)
                            Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { error ->
                        Log.e("LoginFragment", "Login failed: ${error.message}")
                        Toast.makeText(requireContext(), error.message ?: "Login failed", Toast.LENGTH_LONG).show()
                    }
                )
                // Clear the result to prevent re-triggering
                authViewModel.clearResults()
            }
        }

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

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Handle admin test login
            if (email == "admin" && password == "admin") {
                Log.d("LoginFragment", "Admin test login")
                handleAdminLogin()
                return@setOnClickListener
            }

            if (validateInput(email, password)) {
                Log.d("LoginFragment", "Attempting login for: $email")
                authViewModel.login(email, password)
            }
        }

        // Fingerprint login
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

        forgotPasswordLink.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot password functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        view?.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("LoginFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        // Social login buttons
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

    private fun handleAdminLogin() {
        // Save mock auth data for testing
        sharedPrefsManager.saveAuthData(
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token",
            userId = "admin_id",
            email = "admin@culturex.com",
            displayName = "Administrator"
        )

        // Enable biometric for admin too
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

    override fun onResume() {
        super.onResume()
        // Update fingerprint visibility when returning to the fragment
        updateFingerprintVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.clearResults()
    }
}