package com.example.culturex.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.AuthModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = CultureXRepository()

    private val _userProfile = MutableLiveData<AuthModels.UserProfileDTO?>()
    val userProfile: LiveData<AuthModels.UserProfileDTO?> = _userProfile

    private val _favorites = MutableLiveData<List<AuthModels.FavoriteDTO>>()
    val favorites: LiveData<List<AuthModels.FavoriteDTO>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

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

    fun clearError() {
        _error.value = null
    }
}