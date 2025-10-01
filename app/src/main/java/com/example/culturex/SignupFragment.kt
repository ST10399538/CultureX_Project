package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentSignupBinding
import com.example.culturex.data.viewmodels.RegisterViewModel
import com.example.culturex.utils.SharedPreferencesManager
import android.util.Log

class SignupFragment : Fragment(R.layout.fragment_signup) {

    // ViewBinding instance for this fragment. _binding is nullable for lifecycle safety.
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    // ViewModel for registration logic
    private val registerViewModel: RegisterViewModel by viewModels()
    // SharedPreferences manager to store user data locally
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Bind the layout view to the binding object
        _binding = FragmentSignupBinding.bind(view)

        // Initialize SharedPreferencesManager
        sharedPrefsManager = SharedPreferencesManager(requireContext())

        setupObservers()
        setupClickListeners()
    }

    // Observe registration result from the ViewModel
    private fun setupObservers() {
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                result.fold(
                    onSuccess = { successMessage ->
                        Log.d("SignupFragment", "Registration successful")

                        // Save phone number locally
                        val phone = binding.phoneInput.text.toString().trim()
                        if (phone.isNotEmpty()) {
                            val fullPhone = "+27$phone" // Combine country code with number
                            sharedPrefsManager.updateUserProfile(
                                displayName = null,
                                phoneNumber = fullPhone
                            )
                        }

                        // Show success message
                        Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show()

                        // Attempt to navigate to biometric setup screen
                        try {
                            if (findNavController().currentDestination?.id == R.id.signupFragment) {
                                findNavController().navigate(R.id.action_signup_to_biometric_setup)
                            }
                        } catch (e: Exception) {
                            Log.e("SignupFragment", "Navigation to biometric setup failed", e)
                            try {
                                findNavController().navigate(R.id.action_signup_to_login)
                            } catch (e2: Exception) {
                                Log.e("SignupFragment", "Login navigation also failed", e2)
                                Toast.makeText(requireContext(),
                                    "Registration successful! Please restart the app and login.",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    onFailure = { error ->
                        // Handle registration failure and show error message
                        Log.e("SignupFragment", "Registration failed: ${error.message}")
                        Toast.makeText(requireContext(), error.message ?: "Registration failed",
                            Toast.LENGTH_LONG).show()
                    }
                )
                // Clear the result after processing to avoid re-triggering
                registerViewModel.clearResults()
            }
        }

        // Observe loading state to update button UI
        registerViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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
        // Back arrow navigation
        binding.backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("SignupFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        // Register button click listener
        binding.registerButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val surname = binding.surnameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val phone = binding.phoneInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            // Validate input fields
            if (validateInput(name, surname, email, phone, password)) {
                val displayName = "$name $surname"

                // Call ViewModel to register user
                Log.d("SignupFragment", "Attempting registration for: $email")
                registerViewModel.register(email, password, displayName, "en")
            }
        }

        // Link to sign in page
        binding.signInLink.setOnClickListener {
            try {
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
                Toast.makeText(requireContext(), "Navigation error: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // Terms and conditions link click
        binding.termsLink?.setOnClickListener {
            Toast.makeText(requireContext(), "Terms and conditions coming soon",
                Toast.LENGTH_SHORT).show()
        }
    }

// Validates all the required fields to be entered by the user
    private fun validateInput(name: String, surname: String, email: String, phone: String, password: String): Boolean {
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

        if (surname.isEmpty()) {
            binding.surnameInput.error = "Surname is required"
            isValid = false
        } else if (surname.length < 2) {
            binding.surnameInput.error = "Surname must be at least 2 characters"
            isValid = false
        } else {
            binding.surnameInput.error = null
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

        if (phone.isEmpty()) {
            binding.phoneInput.error = "Phone number is required"
            isValid = false
        } else if (phone.length < 9) {
            binding.phoneInput.error = "Please enter a valid phone number"
            isValid = false
        } else {
            binding.phoneInput.error = null
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

    // Clear binding to avoid memory leaks
    // Clear ViewModel results to avoid retaining old state
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        registerViewModel.clearResults()
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