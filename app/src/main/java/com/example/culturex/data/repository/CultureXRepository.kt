package com.example.culturex.data.repository

import com.example.culturex.data.api.NetworkConfig
import com.example.culturex.data.models.*
import retrofit2.Response
import android.util.Log

class CultureXRepository {
    private val apiService = NetworkConfig.apiService

    suspend fun login(email: String, password: String): Response<AuthModels.AuthResponseDTO> {
        return try {
            Log.d("CultureXRepository", "Making login request for email: $email")
            val response = apiService.login(AuthModels.LoginDTO(email, password))
            Log.d("CultureXRepository", "Login response received: ${response.code()}")
            response
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Login request failed", e)
            throw e
        }
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        preferredLanguage: String? = null
    ): Response<AuthModels.AuthResponseDTO> {
        return try {
            Log.d("CultureXRepository", "Making registration request for email: $email")
            val registerRequest = AuthModels.RegisterDTO(
                email = email,
                password = password,
                displayName = displayName,
                preferredLanguage = preferredLanguage
            )
            val response = apiService.register(registerRequest)
            Log.d("CultureXRepository", "Registration response received: ${response.code()}")
            response
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Registration request failed", e)
            throw e
        }
    }

    suspend fun getCountries(): Response<List<CountryModels.CountryDTO>> {
        return try {
            apiService.getCountries()
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get countries request failed", e)
            throw e
        }
    }

    suspend fun getCountryCategories(countryId: String): Response<List<CountryModels.CulturalCategoryDTO>> {
        return try {
            apiService.getCountryCategories(countryId)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get country categories request failed", e)
            throw e
        }
    }

    suspend fun getCulturalContent(countryId: String, categoryId: String): Response<CountryModels.CulturalContentDTO> {
        return try {
            apiService.getCulturalContent(countryId, categoryId)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get cultural content request failed", e)
            throw e
        }
    }

    suspend fun getAllCategories(): Response<List<CountryModels.CulturalCategoryDTO>> {
        return try {
            apiService.getAllCategories()
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get all categories request failed", e)
            throw e
        }
    }

    suspend fun searchCountries(query: String): Response<List<CountryModels.CountryDTO>> {
        return try {
            apiService.searchCountries(query)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Search countries request failed", e)
            throw e
        }
    }

    // User methods
    suspend fun getUserProfile(token: String): Response<AuthModels.UserProfileDTO> {
        return try {
            apiService.getUserProfile("Bearer $token")
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get user profile request failed", e)
            throw e
        }
    }

    suspend fun updateUserProfile(token: String, profile: AuthModels.UpdateUserProfileDTO): Response<AuthModels.UserProfileDTO> {
        return try {
            apiService.updateUserProfile("Bearer $token", profile)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Update user profile request failed", e)
            throw e
        }
    }

    suspend fun getUserFavorites(token: String): Response<List<AuthModels.FavoriteDTO>> {
        return try {
            apiService.getUserFavorites("Bearer $token")
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get user favorites request failed", e)
            throw e
        }
    }

    suspend fun addFavorite(token: String, favorite: AuthModels.AddFavoriteDTO): Response<Unit> {
        return try {
            apiService.addFavorite("Bearer $token", favorite)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Add favorite request failed", e)
            throw e
        }
    }
}