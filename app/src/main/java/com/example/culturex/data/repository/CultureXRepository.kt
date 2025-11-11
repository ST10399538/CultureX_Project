package com.example.culturex.data.repository

import com.example.culturex.data.api.NetworkConfig
import com.example.culturex.data.models.*
import retrofit2.Response
import android.util.Log
import com.example.culturex.data.api.NetworkConfig.apiService

class CultureXRepository {

    private val apiService = NetworkConfig.apiService

    // Regular login with email and password
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

    // NEW: Google Sign-In with ID token
    suspend fun googleLogin(
        idToken: String,
        displayName: String?,
        email: String?,
        profilePictureUrl: String?
    ): Response<AuthModels.AuthResponseDTO> {
        return try {
            Log.d("CultureXRepository", "Making Google login request")
            val response = apiService.googleLogin(
                AuthModels.GoogleLoginDTO(idToken, displayName, email, profilePictureUrl)
            )
            Log.d("CultureXRepository", "Google login response received: ${response.code()}")
            response
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Google login request failed", e)
            throw e
        }
    }

    // Register new user
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

    // Get details of a specific country by ID
    suspend fun getCountry(countryId: String): Response<CountryModels.CountryDTO> {
        return apiService.getCountry(countryId)
    }

    // Get a list of all countries
    suspend fun getCountries(): Response<List<CountryModels.CountryDTO>> {
        return try {
            apiService.getCountries()
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get countries request failed", e)
            throw e
        }
    }

    // Get cultural categories for a specific country
    suspend fun getCountryCategories(countryId: String): Response<List<CountryModels.CulturalCategoryDTO>> {
        return try {
            apiService.getCountryCategories(countryId)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get country categories request failed", e)
            throw e
        }
    }

    // Get cultural content for a specific country and category
    suspend fun getCulturalContent(countryId: String, categoryId: String): Response<CountryModels.CulturalContentDTO> {
        return try {
            apiService.getCulturalContent(countryId, categoryId)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get cultural content request failed", e)
            throw e
        }
    }

    // Get all available cultural categories
    suspend fun getAllCategories(): Response<List<CountryModels.CulturalCategoryDTO>> {
        return try {
            apiService.getAllCategories()
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get all categories request failed", e)
            throw e
        }
    }

    // Search for countries by query string
    suspend fun searchCountries(query: String): Response<List<CountryModels.CountryDTO>> {
        return try {
            apiService.searchCountries(query)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Search countries request failed", e)
            throw e
        }
    }

    // Get the profile of the logged-in user
    suspend fun getUserProfile(token: String): Response<AuthModels.UserProfileDTO> {
        return try {
            apiService.getUserProfile("Bearer $token")
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get user profile request failed", e)
            throw e
        }
    }

    // Update user profile
    suspend fun updateUserProfile(token: String, profile: AuthModels.UpdateUserProfileDTO): Response<AuthModels.UserProfileDTO> {
        return try {
            apiService.updateUserProfile("Bearer $token", profile)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Update user profile request failed", e)
            throw e
        }
    }

    // Get user's favorite items
    suspend fun getUserFavorites(token: String): Response<List<AuthModels.FavoriteDTO>> {
        return try {
            apiService.getUserFavorites("Bearer $token")
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Get user favorites request failed", e)
            throw e
        }
    }

    // Add a new favorite item
    suspend fun addFavorite(token: String, favorite: AuthModels.AddFavoriteDTO): Response<Unit> {
        return try {
            apiService.addFavorite("Bearer $token", favorite)
        } catch (e: Exception) {
            Log.e("CultureXRepository", "Add favorite request failed", e)
            throw e
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