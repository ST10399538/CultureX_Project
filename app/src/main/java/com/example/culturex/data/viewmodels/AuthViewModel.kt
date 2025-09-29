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
    private val repository = CultureXRepository()

    private val _loginResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val loginResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _loginResult

    private val _registerResult = MutableLiveData<Result<AuthModels.AuthResponseDTO>>()
    val registerResult: LiveData<Result<AuthModels.AuthResponseDTO>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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

    fun register(email: String, password: String, displayName: String, preferredLanguage: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("AuthViewModel", "Attempting registration for email: $email")
                val response = repository.register(email, password, displayName, preferredLanguage)

                Log.d("AuthViewModel", "Registration response code: ${response.code()}")
                Log.d("AuthViewModel", "Registration response message: ${response.message()}")

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    Log.d("AuthViewModel", "Registration successful for user: ${authResponse.user?.email}")
                    _registerResult.value = Result.success(authResponse)
                } else {
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

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearResults() {
        _loginResult.value = null
        _registerResult.value = null
    }
}