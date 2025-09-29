package com.example.culturex.data.models

import com.google.gson.annotations.SerializedName

class AuthModels {
    data class LoginDTO(
        val email: String,
        val password: String
    )

    data class RegisterDTO(
        val email: String,
        val password: String,
        val displayName: String,
        val preferredLanguage: String? = "en"
    )

    data class AuthResponseDTO(
        val accessToken: String?,
        val refreshToken: String?,
        val user: UserProfileDTO?
    )

    data class UserProfileDTO(
        val id: String,
        val email: String?,
        val displayName: String?,
        val profilePictureUrl: String?,
        val preferredLanguage: String?,
        val biometricEnabled: Boolean = false,
        val notificationPreferences: Any? = null
    )

    data class UpdateUserProfileDTO(
        val displayName: String,
        val profilePictureUrl: String? = null,
        val preferredLanguage: String? = null
    )

    data class UserSettingsDTO(
        val preferredLanguage: String?,
        val biometricEnabled: Boolean,
        val notificationPreferences: Any? = null
    )

    data class FavoriteDTO(
        val id: String,
        val countryId: String?,
        val contentId: String?,
        val countryName: String?,
        val contentTitle: String?,
        val categoryName: String?,
        val createdAt: String
    )

    data class AddFavoriteDTO(
        val countryId: String? = null,
        val contentId: String? = null
    )
}