package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentSignupBinding
import com.example.culturex.data.viewmodels.AuthViewModel
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)
        sharedPrefsManager = SharedPreferencesManager(requireContext())

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { authResponse ->
                        Log.d("SignupFragment", "Registration successful for user: ${authResponse.user?.email}")

                        // Show success message
                        Toast.makeText(requireContext(), "Registration successful! Please login to continue.", Toast.LENGTH_LONG).show()

                        // Navigate to login page instead of saving auth data and going to dashboard
                        try {
                            if (findNavController().currentDestination?.id == R.id.signupFragment) {
                                // Go to login page after successful signup
                                findNavController().navigate(R.id.action_signup_to_login)
                            }
                        } catch (e: Exception) {
                            Log.e("SignupFragment", "Navigation to login failed", e)
                            // Fallback: try to navigate up (back to previous screen)
                            try {
                                findNavController().navigateUp()
                            } catch (e2: Exception) {
                                Log.e("SignupFragment", "Navigate up also failed", e2)
                                Toast.makeText(requireContext(), "Registration successful! Please restart the app and login.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("SignupFragment", "Registration failed: ${error.message}")
                        Toast.makeText(requireContext(), error.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                    }
                )
                // Clear the result to prevent re-triggering
                authViewModel.clearResults()
            }
        }

        authViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.registerButton.isEnabled = !isLoading
            binding.registerButton.text = if (isLoading) "Creating Account..." else "Register"

            if (isLoading) {
                binding.registerButton.alpha = 0.6f
            } else {
                binding.registerButton.alpha = 1.0f
            }
        }
    }

    private fun setupClickListeners() {
        // Back arrow → go back
        binding.backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("SignupFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        // Register button → call API
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (validateInput(name, email, password)) {
                Log.d("SignupFragment", "Attempting registration for: $email")
                // Note: We're not saving auth data here - just registering the user
                authViewModel.register(email, password, name, "en") // Default to English
            }
        }

        // Login link → go back to login
        binding.signInLink.setOnClickListener {
            try {
                // Check if the login action exists
                if (findNavController().currentDestination?.id == R.id.signupFragment) {
                    try {
                        findNavController().navigate(R.id.action_signup_to_login)
                    } catch (e: Exception) {
                        Log.w("SignupFragment", "Login action not found, navigating up", e)
                        findNavController().navigateUp()
                    }
                }
            } catch (e: Exception) {
                Log.e("SignupFragment", "Navigation to login failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Terms link
        binding.termsLink?.setOnClickListener {
            Toast.makeText(requireContext(), "Terms and conditions coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.nameInput.error = "Name is required"
            isValid = false
        } else if (name.length < 2) {
            binding.nameInput.error = "Name must be at least 2 characters"
            isValid = false
        } else {
            binding.nameInput.error = null
        }

        if (email.isEmpty()) {
            binding.emailInput.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.emailInput.error = null
        }

        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        } else if (!password.any { it.isDigit() }) {
            binding.passwordInput.error = "Password must contain at least one number"
            isValid = false
        } else {
            binding.passwordInput.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Clear any pending results to prevent memory leaks
        authViewModel.clearResults()
    }
}