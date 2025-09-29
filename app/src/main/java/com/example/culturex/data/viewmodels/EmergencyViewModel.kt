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

    private val repository = CultureXRepository()
    private val gson = Gson()

    private val _countryInfo = MutableLiveData<CountryModels.CountryDTO?>()
    val countryInfo: LiveData<CountryModels.CountryDTO?> = _countryInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCountryEmergencyInfo(countryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getCountry(countryId)
                if (response.isSuccessful) {
                    val country = response.body()

                    val parsedContacts: Map<String, String>? =
                        parseEmergencyContacts(country?.emergencyContacts)

                    val updatedCountry = country?.copy(
                        emergencyContacts = parsedContacts
                            ?: getDefaultEmergencyContacts(country?.name)
                    )
                    _countryInfo.value = updatedCountry

                } else {
                    _error.value = "Failed to load emergency information: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error loading emergency information: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

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
                                    if (value.isNotEmpty() && value.first() is String) {
                                        result[key] = value.first() as String
                                    }
                                }
                                else -> {
                                    value?.toString()?.let { result[key] = it }
                                }
                            }
                        }
                    }
                    result.ifEmpty { null }
                } catch (e: Exception) {
                    null
                }
            }
            is String -> {
                try {
                    val type = object : TypeToken<Map<String, String>>() {}.type
                    gson.fromJson<Map<String, String>>(contactsData, type)
                } catch (e: Exception) {
                    try {
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
        }
    }

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

    fun clearError() {
        _error.value = null
    }
}
