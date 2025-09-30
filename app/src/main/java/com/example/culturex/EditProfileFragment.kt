package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.culturex.data.viewmodels.UserViewModel
import com.example.culturex.utils.SharedPreferencesManager
import com.google.android.material.textfield.TextInputEditText
import android.util.Log

class EditProfileFragment : Fragment(R.layout.fragment_editprofile) {

    private lateinit var sharedPrefsManager: SharedPreferencesManager
    private val userViewModel: UserViewModel by viewModels()

    private lateinit var backArrow: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var changePictureButton: Button
    private lateinit var nameInput: TextInputEditText
    private lateinit var surnameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var updateButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefsManager = SharedPreferencesManager(requireContext())

        initializeViews(view)
        setupObservers()
        setupClickListeners()
        loadUserData()
    }

    private fun initializeViews(view: View) {
        backArrow = view.findViewById(R.id.back_arrow)
        profileImage = view.findViewById(R.id.profile_image)
        changePictureButton = view.findViewById(R.id.change_picture_button)
        nameInput = view.findViewById(R.id.name_input)
        surnameInput = view.findViewById(R.id.surname_input)
        emailInput = view.findViewById(R.id.email_input)
        phoneInput = view.findViewById(R.id.phone_input)
        passwordInput = view.findViewById(R.id.password_input)
        updateButton = view.findViewById(R.id.update_button)
    }

    private fun setupObservers() {
        userViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                // Update form fields with user data
                nameInput.setText(it.displayName ?: "")
                emailInput.setText(it.email ?: "")
                // You can add more fields as needed
            }
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            updateButton.isEnabled = !isLoading
            updateButton.text = if (isLoading) "Updating..." else "Update"

            if (isLoading) {
                updateButton.alpha = 0.6f
            } else {
                updateButton.alpha = 1.0f
            }
        }

        userViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                userViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }

        changePictureButton.setOnClickListener {
            Toast.makeText(requireContext(), "Change picture functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        updateButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserData() {
        // Load saved user data from SharedPreferences
        val displayName = sharedPrefsManager.getDisplayName()
        val email = sharedPrefsManager.getEmail()

        // Pre-populate form with saved data
        nameInput.setText(displayName ?: "")
        emailInput.setText(email ?: "")

        // Split display name into name and surname if needed
        displayName?.let { fullName ->
            val nameParts = fullName.split(" ")
            if (nameParts.size >= 2) {
                nameInput.setText(nameParts[0])
                surnameInput.setText(nameParts.drop(1).joinToString(" "))
            } else {
                nameInput.setText(fullName)
            }
        }

        // Load additional user data from API if token is available
        val token = sharedPrefsManager.getAccessToken()
        if (token != null) {
            userViewModel.loadUserProfile(token)
        }
    }

    private fun updateProfile() {
        val name = nameInput.text.toString().trim()
        val surname = surnameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (validateInput(name, email)) {
            // Create full display name
            val fullDisplayName = if (surname.isNotEmpty()) "$name $surname" else name

            // Update SharedPreferences immediately
            sharedPrefsManager.updateUserProfile(
                displayName = fullDisplayName,
                profilePictureUrl = null, // You can add this later
                preferredLanguage = null // You can add this later
            )

            // TODO: Make API call to update profile on server
            // For now, just show success message
            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()

            // Navigate back
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Navigation back failed", e)
            }
        }
    }

    private fun validateInput(name: String, email: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            isValid = false
        } else if (name.length < 2) {
            nameInput.error = "Name must be at least 2 characters"
            isValid = false
        } else {
            nameInput.error = null
        }

        if (email.isEmpty()) {
            emailInput.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Please enter a valid email"
            isValid = false
        } else {
            emailInput.error = null
        }

        return isValid
    }
}