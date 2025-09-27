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
