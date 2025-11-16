package com.example.culturex

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import com.example.culturex.utils.GoogleSignInHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginFragment : Fragment(R.layout.fragment_login) {
    // It manages login with email/password, admin test login, and biometric login.

    // UI Components
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var biometricLoginButton: View
    private lateinit var biometricIcon: ImageView
    private lateinit var biometricHelperText: TextView
    private lateinit var googleLoginButton: ImageView

    // Utility and helper classes
    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private lateinit var biometricHelper: BiometricHelper
    private val authViewModel: AuthViewModel by viewModels()

    // Google Sign-In client
    private lateinit var googleSignInClient: GoogleSignInClient

    // Result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleGoogleSignInResult(task)
        } else {
            Log.e("LoginFragment", "Google Sign-In failed with result code: ${result.resultCode}")
            Toast.makeText(requireContext(), "Google Sign-In cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize helpers and managers
        sharedPrefsManager = SharedPreferencesManager(requireContext())
        biometricHelper = BiometricHelper(this)

        // Initialize views and setup features
        initializeViews(view)
        setupGoogleSignIn()
        setupBiometric()
        setupObservers()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    // Bind layout views to variables
    private fun initializeViews(view: View) {
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        signUpLink = view.findViewById(R.id.sign_up_link)
        forgotPasswordLink = view.findViewById(R.id.forgot_password)
        biometricLoginButton = view.findViewById(R.id.biometric_login_button)
        biometricIcon = view.findViewById(R.id.biometric_icon)
        biometricHelperText = view.findViewById(R.id.biometric_helper_text)
        googleLoginButton = view.findViewById(R.id.google_login)
    }

    // Setup biometric authentication and listeners
    private fun setupBiometric() {
        biometricHelper.setAuthListener(object : BiometricHelper.BiometricAuthListener {
            override fun onAuthenticationSucceeded() {
                Log.d("LoginFragment", "Biometric authentication succeeded")
                val savedEmail = sharedPrefsManager.getEmail()
                val savedUserId = sharedPrefsManager.getUserId()

                if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
                    handleBiometricLogin()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No saved credentials found. Please login with email and password first.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(
                    requireContext(),
                    "Biometric authentication failed. Try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
                Log.e("LoginFragment", "Biometric error: $errorCode - $errorMessage")
                when (errorCode) {
                    androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED -> {}
                    androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                        Toast.makeText(
                            requireContext(),
                            "Please login with email and password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            requireContext(),
                            "Biometric error: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        // Update biometric button visibility
        updateBiometricButtonVisibility()
    }

    /**
     * Control visibility of biometric button
     * Button is visible when:
     * 1. Device has biometric capability
     * 2. User has saved credentials (has logged in before)
     * 3. Biometric is enabled in settings
     */
    private fun updateBiometricButtonVisibility() {
        val isBiometricAvailable = biometricHelper.isBiometricAvailable()
        val hasSavedCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
        val isBiometricEnabled = sharedPrefsManager.isBiometricEnabled()

        Log.d("LoginFragment", "Biometric status - Available: $isBiometricAvailable, HasCreds: $hasSavedCredentials, Enabled: $isBiometricEnabled")

        // Show button if biometric is available AND either:
        // - User has credentials saved and biometric enabled, OR
        // - Just biometric hardware is available (for first-time setup visibility)
        if (isBiometricAvailable) {
            biometricLoginButton.visibility = View.VISIBLE

            if (hasSavedCredentials && isBiometricEnabled) {
                // Show with user info
                biometricHelperText.visibility = View.VISIBLE
                val email = sharedPrefsManager.getEmail()
                biometricHelperText.text = "Quick login as ${email?.substringBefore("@") ?: "user"}"
                biometricLoginButton.alpha = 1.0f
                biometricLoginButton.isEnabled = true
            } else {
                // Show but indicate not set up
                biometricHelperText.visibility = View.VISIBLE
                biometricHelperText.text = "Biometric login (setup required)"
                biometricLoginButton.alpha = 0.6f
                biometricLoginButton.isEnabled = true
            }
        } else {
            // Hide if no biometric hardware
            biometricLoginButton.visibility = View.GONE
            biometricHelperText.visibility = View.GONE
        }
    }

    // Logs user in using saved biometric credentials
    private fun handleBiometricLogin() {
        val savedEmail = sharedPrefsManager.getEmail()
        val savedUserId = sharedPrefsManager.getUserId()
        val savedDisplayName = sharedPrefsManager.getDisplayName()

        if (!savedEmail.isNullOrEmpty() && !savedUserId.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Welcome back, ${savedDisplayName ?: savedEmail}!",
                Toast.LENGTH_SHORT
            ).show()

            try {
                findNavController().navigate(R.id.mainFragment)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Navigation failed", e)
                Toast.makeText(
                    requireContext(),
                    "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please login with email and password first to enable biometric login.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Observe LiveData from ViewModel to update UI based on authentication result
    private fun setupObservers() {
        // Observe email/password login result
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { authResponse ->
                        Log.d("LoginFragment", "Login successful, saving auth data")

                        val existingPhone = sharedPrefsManager.getPhoneNumber()

                        sharedPrefsManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.user?.id,
                            email = authResponse.user?.email,
                            displayName = authResponse.user?.displayName,
                            phoneNumber = existingPhone
                        )

                        if (biometricHelper.isBiometricAvailable()) {
                            sharedPrefsManager.setBiometricEnabled(true)
                            Toast.makeText(
                                requireContext(),
                                "Login successful! Biometric login enabled for next time.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Login successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        try {
                            findNavController().navigate(R.id.action_login_to_onboarding)
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "Navigation to onboarding failed", e)
                            try {
                                findNavController().navigate(R.id.mainFragment)
                            } catch (e2: Exception) {
                                Log.e("LoginFragment", "Navigation to main also failed", e2)
                                Toast.makeText(
                                    requireContext(),
                                    "Navigation error: ${e2.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("LoginFragment", "Login failed: ${error.message}")
                        Toast.makeText(
                            requireContext(),
                            error.message ?: "Login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
                authViewModel.clearResults()
            }
        }

        // Observe Google login result
        authViewModel.googleLoginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { authResponse ->
                        Log.d("LoginFragment", "Google login successful")

                        sharedPrefsManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.user?.id,
                            email = authResponse.user?.email,
                            displayName = authResponse.user?.displayName,
                            phoneNumber = null
                        )

                        Toast.makeText(
                            requireContext(),
                            "Welcome, ${authResponse.user?.displayName}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        try {
                            findNavController().navigate(R.id.action_login_to_onboarding)
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "Navigation failed", e)
                            try {
                                findNavController().navigate(R.id.mainFragment)
                            } catch (e2: Exception) {
                                Log.e("LoginFragment", "Navigation to main also failed", e2)
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("LoginFragment", "Google login failed: ${error.message}")
                        Toast.makeText(
                            requireContext(),
                            error.message ?: "Google login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
                authViewModel.clearResults()
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loginButton.isEnabled = !isLoading
            googleLoginButton.isEnabled = !isLoading
            biometricLoginButton.isEnabled = !isLoading
            loginButton.text = if (isLoading) "Logging in..." else "Log In"

            if (isLoading) {
                loginButton.alpha = 0.6f
                googleLoginButton.alpha = 0.6f
                biometricLoginButton.alpha = 0.6f
            } else {
                loginButton.alpha = 1.0f
                googleLoginButton.alpha = 1.0f
                updateBiometricButtonVisibility() // Restore proper alpha
            }
        }
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

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

        // Google Sign-In button
        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        // Biometric login button - always attempts authentication when clicked
        biometricLoginButton.setOnClickListener {
            if (biometricHelper.isBiometricAvailable()) {
                val hasSavedCredentials = !sharedPrefsManager.getEmail().isNullOrEmpty()
                val isBiometricEnabled = sharedPrefsManager.isBiometricEnabled()

                if (hasSavedCredentials && isBiometricEnabled) {
                    // User has credentials, proceed with biometric auth
                    biometricHelper.authenticate()
                } else {
                    // No saved credentials yet
                    Toast.makeText(
                        requireContext(),
                        "Please login with email and password first to enable biometric login",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    biometricHelper.getBiometricStatusMessage(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        signUpLink.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_login_to_signup)
            } catch (e: Exception) {
                Log.e("LoginFragment", "Navigation to signup failed", e)
                Toast.makeText(
                    requireContext(),
                    "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        forgotPasswordLink.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Forgot password functionality coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }

        view?.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("LoginFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        view?.findViewById<View>(R.id.facebook_login)?.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Facebook login coming soon",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("LoginFragment", "Google Sign-In successful: ${account?.email}")

            val idToken = account?.idToken
            if (idToken != null) {
                authViewModel.googleLogin(
                    idToken = idToken,
                    displayName = account.displayName,
                    email = account.email,
                    profilePictureUrl = account.photoUrl?.toString()
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to get Google ID token",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: ApiException) {
            Log.e("LoginFragment", "Google Sign-In failed", e)
            Toast.makeText(
                requireContext(),
                "Google Sign-In failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun handleAdminLogin() {
        sharedPrefsManager.saveAuthData(
            accessToken = "mock_access_token",
            refreshToken = "mock_refresh_token",
            userId = "admin_id",
            email = "admin@culturex.com",
            displayName = "Administrator",
            phoneNumber = "+27123456789"
        )

        if (biometricHelper.isBiometricAvailable()) {
            sharedPrefsManager.setBiometricEnabled(true)
        }

        Toast.makeText(requireContext(), "Admin login successful!", Toast.LENGTH_SHORT).show()
        try {
            findNavController().navigate(R.id.mainFragment)
        } catch (e: Exception) {
            Log.e("LoginFragment", "Admin navigation failed", e)
            Toast.makeText(
                requireContext(),
                "Navigation error: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
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
        updateBiometricButtonVisibility()
    }

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