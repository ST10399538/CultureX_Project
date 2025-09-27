package com.example.culturex.data.repository

import com.example.culturex.data.api.NetworkConfig
import com.example.culturex.data.models.*
import retrofit2.Response

class CultureXRepository {

    private val apiService = NetworkConfig.apiService

    suspend fun login(email: String, password: String): Response<AuthModels.AuthResponseDTO> {
        return apiService.login(AuthModels.LoginDTO(email, password))
    }

    suspend fun register(email: String, password: String, displayName: String): Response<AuthModels.AuthResponseDTO> {
        return apiService.register(AuthModels.RegisterDTO(email, password, displayName))
    }

    suspend fun getCountries(): Response<List<CountryModels.CountryDTO>> {
        return apiService.getCountries()
    }

    suspend fun getCountryCategories(countryId: String): Response<List<CountryModels.CulturalCategoryDTO>> {
        return apiService.getCountryCategories(countryId)
    }

    suspend fun getCulturalContent(countryId: String, categoryId: String): Response<CountryModels.CulturalContentDTO> {
        return apiService.getCulturalContent(countryId, categoryId)
    }

    suspend fun getAllCategories(): Response<List<CountryModels.CulturalCategoryDTO>> {
        return apiService.getAllCategories()
    }

    suspend fun searchCountries(query: String): Response<List<CountryModels.CountryDTO>> {
        return apiService.searchCountries(query)
    }

    // User methods
    suspend fun getUserProfile(token: String): Response<AuthModels.UserProfileDTO> {
        return apiService.getUserProfile("Bearer $token")
    }

    suspend fun updateUserProfile(token: String, profile: AuthModels.UpdateUserProfileDTO): Response<AuthModels.UserProfileDTO> {
        return apiService.updateUserProfile("Bearer $token", profile)
    }

    suspend fun getUserFavorites(token: String): Response<List<AuthModels.FavoriteDTO>> {
        return apiService.getUserFavorites("Bearer $token")
    }

    suspend fun addFavorite(token: String, favorite: AuthModels.AddFavoriteDTO): Response<Unit> {
        return apiService.addFavorite("Bearer $token", favorite)
    }
}