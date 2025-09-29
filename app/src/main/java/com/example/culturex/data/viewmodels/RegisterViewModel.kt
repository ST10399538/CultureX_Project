package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log

class RegisterViewModel : ViewModel() {
    private val repository = CultureXRepository()

    private val _registerResult = MutableLiveData<Result<String>>() // Just return success message
    val registerResult: LiveData<Result<String>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(email: String, password: String, displayName: String, preferredLanguage: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("RegisterViewModel", "Attempting registration for email: $email")
                val response = repository.register(email, password, displayName, preferredLanguage)

                Log.d("RegisterViewModel", "Registration response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    Log.d("RegisterViewModel", "Registration successful")
                    _registerResult.value = Result.success("Registration successful! Please login to continue.")
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid registration data. Please check your input"
                        409 -> "Email already exists. Please use a different email"
                        422 -> "Password doesn't meet requirements"
                        500 -> "Server error. Please try again later"
                        else -> "Registration failed: ${response.message()}"
                    }
                    Log.e("RegisterViewModel", "Registration failed: $errorMessage")
                    _registerResult.value = Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
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

    fun clearResults() {
        _registerResult.value = null
    }
}