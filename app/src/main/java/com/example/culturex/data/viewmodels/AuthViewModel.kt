package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.culturex.data.models.AuthModels
import android.util.Log

class AuthViewModel : ViewModel() {
    // Repository instance for making API calls
    private val repository = CultureXRepository()

    // LiveData for login result (success or failure)
    private val _loginResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val loginResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _loginResult

    // LiveData for registration result
    private val _registerResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val registerResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _registerResult

    // LiveData for Google login result
    private val _googleLoginResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val googleLoginResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _googleLoginResult

    // LiveData to track loading state (true = loading, false = done)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to store and display error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    // Function to handle user login using email and password
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Call repository to make login API request
                Log.d("AuthViewModel", "Attempting login for email: $email")
                val response = repository.login(email, password)

                Log.d("AuthViewModel", "Login response code: ${response.code()}")
                Log.d("AuthViewModel", "Login response message: ${response.message()}")

                // If response is successful and contains a body
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Login successful for user: ${authResponse.user?.email}")
                    _loginResult.value = Result.success(authResponse)
                } else {
                    // Handle error responses with specific messages based on status code
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid email or password format"
                        401 -> "Invalid email or password"
                        404 -> "User not found"
                        500 -> "Server error. Please try again later"
                        else -> "Login failed: ${response.message()}"
                    }
                    Log.e("AuthViewModel", "Login failed: $errorMessage. Error body: $errorBody")
                    _loginResult.value = Result.failure(Exception(errorMessage))
                }
                // Handle network or unexpected exceptions
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login exception: ${e.message}", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> "No internet connection. Please check your network"
                    e.message?.contains("timeout") == true -> "Connection timeout. Please try again"
                    else -> "Network error: ${e.message}"
                }
                _loginResult.value = Result.failure(Exception(errorMessage))
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Function to handle Google Sign-In authentication
    fun googleLogin(idToken: String, displayName: String?, email: String?, profilePictureUrl: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Call repository to handle Google authentication API request
                Log.d("AuthViewModel", "Attempting Google login")
                val response = repository.googleLogin(idToken, displayName, email, profilePictureUrl)

                // Check if Google login response is successful
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Google login successful")
                    _googleLoginResult.value = Result.success(authResponse)
                } else {
                    // Provide specific error messages for known failure codes
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid Google token"
                        401 -> "Google authentication failed"
                        500 -> "Server error. Please try again later"
                        else -> "Google login failed: ${response.message()}"
                    }
                    Log.e("AuthViewModel", "Google login failed: $errorMessage")
                    _googleLoginResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Catch network or unexpected exceptions
                Log.e("AuthViewModel", "Google login exception: ${e.message}", e)
                _googleLoginResult.value = Result.failure(Exception("Network error: ${e.message}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to handle user registration
    fun register(email: String, password: String, displayName: String, preferredLanguage: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Make API call to register the user
                Log.d("AuthViewModel", "Attempting registration for email: $email")
                val response = repository.register(email, password, displayName, preferredLanguage)

                Log.d("AuthViewModel", "Registration response code: ${response.code()}")
                Log.d("AuthViewModel", "Registration response message: ${response.message()}")

                // Handle successful registration
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Registration successful for user: ${authResponse.user?.email}")
                    _registerResult.value = Result.success(authResponse)
                } else {

                    // Handle various server response codes with user-friendly messages
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid registration data. Please check your input"
                        409 -> "Email already exists. Please use a different email"
                        422 -> "Password doesn't meet requirements"
                        500 -> "Server error. Please try again later"
                        else -> "Registration failed: ${response.message()}"
                    }
                    Log.e("AuthViewModel", "Registration failed: $errorMessage. Error body: $errorBody")
                    _registerResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Catch and process exceptions (network, parsing, etc.)
                Log.e("AuthViewModel", "Registration exception: ${e.message}", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> "No internet connection. Please check your network"
                    e.message?.contains("timeout") == true -> "Connection timeout. Please try again"
                    else -> "Network error: ${e.message}"
                }
                _registerResult.value = Result.failure(Exception(errorMessage))
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Clears any current error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Clears stored login and registration results (useful when navigating away or retrying)
    fun clearResults() {
        _loginResult.value = null
        _registerResult.value = null
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