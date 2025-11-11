package com.example.culturex.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryModels
import com.example.culturex.data.repository.CultureXRepository
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class EmergencyViewModel : ViewModel() {

    // Repository to fetch data from API or database
    private val repository = CultureXRepository()
    private val gson = Gson()

    // LiveData to hold the country emergency information
    private val _countryInfo = MutableLiveData<CountryModels.CountryDTO?>()
    val countryInfo: LiveData<CountryModels.CountryDTO?> = _countryInfo

    // LiveData to indicate loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold any error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Function to load emergency information for a specific country
    fun loadCountryEmergencyInfo(countryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCountry(countryId)
                if (response.isSuccessful) {
                    val country = response.body()

                    // Parse emergency contacts from API response
                    val parsedContacts: Map<String, String>? =
                        parseEmergencyContacts(country?.emergencyContacts)

                    // Update the country object with parsed or default emergency contacts
                    val updatedCountry = country?.copy(
                        emergencyContacts = parsedContacts
                            ?: getDefaultEmergencyContacts(country?.name)
                    )
                    _countryInfo.value = updatedCountry

                } else {
                    // API returned error message
                    _error.value = "Failed to load emergency information: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading emergency information: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to parse emergency contacts from the API response
    private fun parseEmergencyContacts(contactsData: Any?): Map<String, String>? {
        return when (contactsData) {
            is Map<*, *> -> {
                try {
                    val result = mutableMapOf<String, String>()
                    contactsData.forEach { (key, value) ->
                        if (key is String) {
                            when (value) {
                                is String -> result[key] = value
                                is List<*> -> {
                                    // If value is a list, take the first string element
                                    if (value.isNotEmpty() && value.first() is String) {
                                        result[key] = value.first() as String
                                    }
                                }
                                else -> {
                                    // Convert other types to string
                                    value?.toString()?.let { result[key] = it }
                                }
                            }
                        }
                    }
                    result.ifEmpty { null }
                    // Return null if map is empty
                } catch (e: Exception) {
                    null
                }
            }
            is String -> {
                try {
                    // Try parsing as a simple Map<String, String>
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    gson.fromJson<Map<String, String>>(contactsData, type)
                } catch (e: Exception) {
                    try {
                        // Fallback: parse as JSON object manually
                        val jsonElement = JsonParser.parseString(contactsData)
                        if (jsonElement.isJsonObject) {
                            val jsonObject = jsonElement.asJsonObject
                            val result = mutableMapOf<String, String>()

                            jsonObject.entrySet().forEach { entry ->
                                val key = entry.key
                                val value = entry.value
                                when {
                                    value.isJsonPrimitive -> result[key] = value.asString
                                    value.isJsonArray -> {
                                        val array = value.asJsonArray
                                        if (array.size() > 0 && array[0].isJsonPrimitive) {
                                            result[key] = array[0].asString
                                        }
                                    }
                                }
                            }
                            result.ifEmpty { null }
                        } else null
                    } catch (e2: Exception) {
                        null
                    }
                }
            }
            else -> null
            // If data is neither map nor string, return null
        }
    }

    // Default emergency contacts for specific countries
    companion object {
        fun getDefaultEmergencyContacts(countryName: String?): Map<String, String> {
            return when (countryName) {
                "France" -> mapOf(
                    "emergency" to "112",
                    "police" to "17",
                    "medical" to "15",
                    "fire" to "18",
                    "tourist_helpline" to "3977"
                )
                "South Africa" -> mapOf(
                    "police" to "10111",
                    "medical" to "10177",
                    "fire" to "10111",
                    "tourist_helpline" to "083 123 2345"
                )
                "Japan" -> mapOf(
                    "emergency" to "119",
                    "police" to "110",
                    "medical" to "119",
                    "fire" to "119",
                    "tourist_helpline" to "050-3816-2787"
                )
                "India" -> mapOf(
                    "emergency" to "112",
                    "police" to "100",
                    "medical" to "108",
                    "fire" to "101",
                    "tourist_helpline" to "1363"
                )
                "United States" -> mapOf(
                    "emergency" to "911",
                    "police" to "911",
                    "medical" to "911",
                    "fire" to "911",
                    "tourist_helpline" to "1-877-USA-TRIP"
                )
                else -> mapOf(
                    "emergency" to "112",
                    "police" to "112",
                    "medical" to "112",
                    "fire" to "112"
                )
            }
        }
    }
    // Function to clear any existing error messages
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
