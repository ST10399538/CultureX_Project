package com.example.culturex.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.AuthModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    // Repository instance to handle API calls
    private val repository = CultureXRepository()

    // LiveData to hold the user profile data
    private val _userProfile = MutableLiveData<AuthModels.UserProfileDTO?>()
    val userProfile: LiveData<AuthModels.UserProfileDTO?> = _userProfile

    // LiveData to hold the list of user's favorite items
    private val _favorites = MutableLiveData<List<AuthModels.FavoriteDTO>>()
    val favorites: LiveData<List<AuthModels.FavoriteDTO>> = _favorites

    // LiveData to track loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to load user profile from the API
    fun loadUserProfile(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getUserProfile(token)
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    _error.value = "Failed to load profile: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to load user's favorite items from the API
    fun loadFavorites(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getUserFavorites(token)
                if (response.isSuccessful) {
                    _favorites.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load favorites: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to clear the current error message
    fun clearError() {
        _error.value = null
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