package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){


    private val repository = CultureXRepository()

    private val _countries = MutableLiveData<List<CountryModels.CountryDTO>>()
    val countries: LiveData<List<CountryModels.CountryDTO>> = _countries

    private val _categories = MutableLiveData<List<CountryModels.CulturalCategoryDTO>>()
    val categories: LiveData<List<CountryModels.CulturalCategoryDTO>> = _categories

    private val _selectedCountry = MutableLiveData<CountryModels.CountryDTO?>()
    val selectedCountry: LiveData<CountryModels.CountryDTO?> = _selectedCountry

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Add this to track if categories are loaded
    private val _areCategoriesLoaded = MutableLiveData<Boolean>()
    val areCategoriesLoaded: LiveData<Boolean> = _areCategoriesLoaded

    fun loadCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCountries()
                if (response.isSuccessful) {
                    val countriesList = response.body() ?: emptyList()
                    _countries.value = countriesList

                    // Auto-select first country if available
                    if (countriesList.isNotEmpty() && _selectedCountry.value == null) {
                        selectCountry(countriesList.first())
                    }
                } else {
                    _error.value = "Failed to load countries: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Remove the global loadCategories() method since we only want country-specific categories

    fun selectCountry(country: CountryModels.CountryDTO) {
        _selectedCountry.value = country
        loadCountryCategories(country.id)
    }

    private fun loadCountryCategories(countryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _areCategoriesLoaded.value = false
            try {
                val response = repository.getCountryCategories(countryId)
                if (response.isSuccessful) {
                    val categoriesList = response.body() ?: emptyList()
                    _categories.value = categoriesList
                    _areCategoriesLoaded.value = categoriesList.isNotEmpty()

                    if (categoriesList.isEmpty()) {
                        _error.value = "No categories available for this country"
                    }
                } else {
                    _error.value = "Failed to load country categories: ${response.message()}"
                    _categories.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Failed to load country categories: ${e.message}"
                _categories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add method to get category by name
    fun getCategoryByName(categoryName: String): CountryModels.CulturalCategoryDTO? {
        return _categories.value?.find { it.name == categoryName }
    }

    // Add method to check if category is available
    fun isCategoryAvailable(categoryName: String): Boolean {
        return getCategoryByName(categoryName) != null
    }

    fun clearError() {
        _error.value = null
    }

    // Add method to retry loading categories
    fun retryLoadCategories() {
        _selectedCountry.value?.let { country ->
            loadCountryCategories(country.id)
        }
    }
}

