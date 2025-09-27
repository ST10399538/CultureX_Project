package com.example.culturex.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class ContentViewModel: ViewModel() {


    private val repository = CultureXRepository()

    private val _content = MutableLiveData<CountryModels.CulturalContentDTO?>()
    val content: LiveData<CountryModels.CulturalContentDTO?> = _content

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadContent(countryId: String, categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCulturalContent(countryId, categoryId)
                if (response.isSuccessful) {
                    _content.value = response.body()
                } else {
                    _error.value = "Failed to load content: ${response.message()}"
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