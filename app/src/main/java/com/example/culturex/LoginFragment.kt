package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.AuthViewModel
import com.example.culturex.utils.SharedPreferencesManager
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())

        // Check if user is already logged in
        if (sharedPrefsManager.isLoggedIn()) {
            findNavController().navigate(R.id.mainFragment)
            return
        }

        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        signUpLink = view.findViewById(R.id.sign_up_link)

        // Create progress bar programmatically if not in layout
        progressBar = ProgressBar(requireContext()).apply {
            isVisible = false
        }

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { authResponse ->
                    // Save auth data
                    sharedPrefsManager.saveAuthData(
                        accessToken = authResponse.accessToken,
                        refreshToken = authResponse.refreshToken,
                        userId = authResponse.user?.id,
                        email = authResponse.user?.email,
                        displayName = authResponse.user?.displayName
                    )

                    Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.mainFragment)
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Login failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.isVisible = isLoading
            loginButton.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Predefined login for testing
            if (email == "admin" && password == "admin") {
                // Save mock auth data
              //  sharedPrefsManager.saveAuthData(
                //    accessToken = "mock_access_token",
                //    refreshToken = "mock_refresh_token",
                //    userId = "admin_id",
                //    email = "admin",
                //    displayName = "Administrator"
              //  )

                Toast.makeText(requireContext(), "Admin login successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.mainFragment)
                return@setOnClickListener
            }

            if (validateInput(email, password)) {
                authViewModel.login(email, password)
            }
        }

        signUpLink.setOnClickListener {
            // Navigate to signup if you have one, or show a message
            Toast.makeText(requireContext(), "Registration not implemented yet", Toast.LENGTH_SHORT).show()
        }

        view?.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            passwordInput.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            return false
        }

        return true
    }
}

