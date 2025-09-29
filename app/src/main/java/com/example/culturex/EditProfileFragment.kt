package com.example.culturex

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
    private lateinit var updateButton: Button

    private var selectedImageUri: Uri? = null

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(requireContext(),
                "Permission denied. Cannot access photos.",
                Toast.LENGTH_LONG).show()
        }
    }

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri

                // Take persistable URI permission
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    Log.e("EditProfileFragment", "Could not take persistable permission", e)
                }

                profileImage.setImageURI(uri)

                // Save the URI as string in SharedPreferences
                sharedPrefsManager.updateUserProfile(
                    displayName = null,
                    profilePictureUrl = uri.toString(),
                    phoneNumber = null
                )

                Toast.makeText(requireContext(), "Profile picture updated!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        updateButton = view.findViewById(R.id.update_button)
    }

    private fun setupObservers() {
        userViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                val displayName = it.displayName ?: ""
                val nameParts = displayName.split(" ", limit = 2)

                if (nameParts.isNotEmpty()) {
                    nameInput.setText(nameParts[0])
                }
                if (nameParts.size > 1) {
                    surnameInput.setText(nameParts[1])
                }

                emailInput.setText(it.email ?: "")
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
            checkPermissionAndOpenPicker()
        }

        updateButton.setOnClickListener {
            updateProfile()
        }
    }

    private fun checkPermissionAndOpenPicker() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(
                    requireContext(),
                    "Photo access is needed to change your profile picture",
                    Toast.LENGTH_LONG
                ).show()
                permissionLauncher.launch(permission)
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        imagePickerLauncher.launch(intent)
    }

    private fun loadUserData() {
        val displayName = sharedPrefsManager.getDisplayName()
        val email = sharedPrefsManager.getEmail()
        val phoneNumber = sharedPrefsManager.getPhoneNumber()
        val profilePictureUrl = sharedPrefsManager.getProfilePictureUrl()

        emailInput.setText(email ?: "")

        displayName?.let { fullName ->
            val nameParts = fullName.split(" ", limit = 2)
            if (nameParts.isNotEmpty()) {
                nameInput.setText(nameParts[0])
            }
            if (nameParts.size > 1) {
                surnameInput.setText(nameParts[1])
            }
        }

        phoneNumber?.let { phone ->
            val displayPhone = if (phone.startsWith("+27")) {
                phone.substring(3)
            } else {
                phone
            }
            phoneInput.setText(displayPhone)
        }

        // Load profile picture if exists
        profilePictureUrl?.let { urlString ->
            try {
                val uri = Uri.parse(urlString)
                profileImage.setImageURI(uri)
                selectedImageUri = uri
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Error loading profile picture", e)
            }
        }

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

        if (validateInput(name, surname, email, phone)) {
            val fullDisplayName = if (surname.isNotEmpty()) "$name $surname" else name
            val fullPhone = if (phone.isNotEmpty()) "+27$phone" else null

            sharedPrefsManager.updateUserProfile(
                displayName = fullDisplayName,
                profilePictureUrl = selectedImageUri?.toString(),
                preferredLanguage = null,
                phoneNumber = fullPhone
            )

            Toast.makeText(requireContext(), "Profile updated successfully!",
                Toast.LENGTH_SHORT).show()

            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("EditProfileFragment", "Navigation back failed", e)
            }
        }
    }

    private fun validateInput(name: String, surname: String, email: String, phone: String): Boolean {
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

        if (surname.isEmpty()) {
            surnameInput.error = "Surname is required"
            isValid = false
        } else if (surname.length < 2) {
            surnameInput.error = "Surname must be at least 2 characters"
            isValid = false
        } else {
            surnameInput.error = null
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

        if (phone.isNotEmpty() && phone.length < 9) {
            phoneInput.error = "Please enter a valid phone number"
            isValid = false
        } else {
            phoneInput.error = null
        }

        return isValid
    }
}