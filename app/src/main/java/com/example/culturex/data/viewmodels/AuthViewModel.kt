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
    // Repository instance to handle API calls
    private val repository = CultureXRepository()

    // LiveData to expose login results to the UI
    private val _loginResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val loginResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _loginResult

    // LiveData to expose registration results to the UI
    private val _registerResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val registerResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _registerResult

    // LiveData to show loading state during API calls
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Function to perform login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("AuthViewModel", "Attempting login for email: $email")
                val response = repository.login(email, password)

                Log.d("AuthViewModel", "Login response code: ${response.code()}")
                Log.d("AuthViewModel", "Login response message: ${response.message()}")

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Login successful for user: ${authResponse.user?.email}")
                    _loginResult.value = Result.success(authResponse)
                } else {
                    // Handle API errors based on HTTP status code
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


    // Function to perform user registration

    fun register(email: String, password: String, displayName: String, preferredLanguage: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("AuthViewModel", "Attempting registration for email: $email")
                val response = repository.register(email, password, displayName, preferredLanguage)

                Log.d("AuthViewModel", "Registration response code: ${response.code()}")
                Log.d("AuthViewModel", "Registration response message: ${response.message()}")

                // If response is successful, update registerResult LiveData
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Registration successful for user: ${authResponse.user?.email}")
                    _registerResult.value = Result.success(authResponse)
                } else {
                    // Handle API errors based on HTTP status code
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

    // Function to clear any existing error messages
    fun clearError() {
        _errorMessage.value = null
    }

    // Function to clear previous login and registration results
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