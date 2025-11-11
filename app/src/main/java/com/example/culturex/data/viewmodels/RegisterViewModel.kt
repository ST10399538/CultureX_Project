package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log

class RegisterViewModel : ViewModel() {
    // Repository instance to handle API calls
    private val repository = CultureXRepository()

    // LiveData to hold the result of the registration process
    private val _registerResult = MutableLiveData<Result<String>>() // Just return success message
    val registerResult: LiveData<Result<String>> = _registerResult

    // LiveData to indicate if a network operation is in progress
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    // Function to perform user registration
    // Accepts email, password, display name, and an optional preferred language
    fun register(email: String, password: String, displayName: String, preferredLanguage: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("RegisterViewModel", "Attempting registration for email: $email")
                val response = repository.register(email, password, displayName, preferredLanguage)

                Log.d("RegisterViewModel", "Registration response code: ${response.code()}")

                // Check if the API response is successful
                if (response.isSuccessful && response.body() != null) {
                    Log.d("RegisterViewModel", "Registration successful")
                    _registerResult.value = Result.success("Registration successful! Please login to continue.")
                } else {
                    // Map HTTP response codes to specific error messages
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid registration data. Please check your input"
                        409 -> "Email already exists. Please use a different email"
                        422 -> "Password doesn't meet requirements"
                        500 -> "Server error. Please try again later"
                        else -> "Registration failed: ${response.message()}"
                    }
                    // Post failure result with the mapped error message
                    Log.e("RegisterViewModel", "Registration failed: $errorMessage")
                    _registerResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                Log.e("RegisterViewModel", "Registration exception: ${e.message}", e)
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

    // Function to clear the registration result (useful when leaving the screen)
    fun clearResults() {
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