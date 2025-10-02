package com.example.culturex.data.api

import com.example.culturex.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth endpoints
    @POST("api/Auth/login")
    suspend fun login(@Body loginRequest: AuthModels.LoginDTO): Response<AuthModels.AuthResponseDTO>

    @POST("api/Auth/register")
    suspend fun register(@Body registerRequest: AuthModels.RegisterDTO): Response<AuthModels.AuthResponseDTO>

    // Countries endpoints
    @GET("api/Countries")
    suspend fun getCountries(): Response<List<CountryModels.CountryDTO>>

    @GET("api/Countries/{id}")
    suspend fun getCountry(@Path("id") countryId: String): Response<CountryModels.CountryDTO>

    @GET("api/Countries/{id}/categories")
    suspend fun getCountryCategories(@Path("id") countryId: String): Response<List<CountryModels.CulturalCategoryDTO>>

    @GET("api/Countries/search")
    suspend fun searchCountries(@Query("query") query: String): Response<List<CountryModels.CountryDTO>>

    // Cultural Content endpoints
    @GET("api/CulturalContent/countries/{countryId}/content/{categoryId}")
    suspend fun getCulturalContent(
        @Path("countryId") countryId: String,
        @Path("categoryId") categoryId: String
    ): Response<CountryModels.CulturalContentDTO>

    @GET("api/CulturalContent/countries/{countryId}")
    suspend fun getCountryCulturalContent(@Path("countryId") countryId: String): Response<List<CountryModels.CulturalContentDTO>>

    @GET("api/CulturalContent/categories")
    suspend fun getAllCategories(): Response<List<CountryModels.CulturalCategoryDTO>>

    @GET("api/CulturalContent/search")
    suspend fun searchContent(@Query("query") query: String): Response<List<CountryModels.CulturalContentDTO>>

    // User Profile endpoints
    @GET("api/Users/profile")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<AuthModels.UserProfileDTO>

    @PUT("api/Users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body profile: AuthModels.UpdateUserProfileDTO
    ): Response<AuthModels.UserProfileDTO>

    @GET("api/Users/settings")
    suspend fun getUserSettings(@Header("Authorization") token: String): Response<AuthModels.UserSettingsDTO>

    @GET("api/Users/favorites")
    suspend fun getUserFavorites(@Header("Authorization") token: String): Response<List<AuthModels.FavoriteDTO>>


    @POST("api/Users/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body favorite: AuthModels.AddFavoriteDTO
    ): Response<Unit>

    @DELETE("api/Users/favorites/{id}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("id") favoriteId: String
    ): Response<Unit>
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]
