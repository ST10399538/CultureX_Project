package com.example.culturex.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.repository.CultureXRepository
import kotlinx.coroutines.launch

class ContentViewModel: ViewModel() {

    // Repository instance used to fetch data from the API or data source
    private val repository = CultureXRepository()

    // LiveData to hold the cultural content retrieved from the API
    private val _content = MutableLiveData<CountryModels.CulturalContentDTO?>()
    val content: LiveData<CountryModels.CulturalContentDTO?> = _content

    // LiveData to track the loading state (true when fetching data, false otherwise)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold any error messages encountered during data fetching
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to fetch cultural content for a specific country and category
    fun loadContent(countryId: String, categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCulturalContent(countryId, categoryId)
                if (response.isSuccessful) {
                    // If the response is successful, update the content LiveData
                    _content.value = response.body()
                } else {
                    // If the response failed, update the error LiveData with a message
                    _error.value = "Failed to load content: ${response.message()}"
                }
            } catch (e: Exception) {
                // Catch any exceptions (like network errors) and update the error LiveData
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Indicate that data fetching has finished
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