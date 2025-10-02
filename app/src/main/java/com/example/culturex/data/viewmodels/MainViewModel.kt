package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){


    // Repository instance to handle API calls
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

    // Function to load the list of countries from the repository
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

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]

