package com.example.culturex.data.models

import com.google.gson.annotations.SerializedName

// DTO for login request
// Contains the email and password needed to authenticate a user
class AuthModels {

    data class LoginDTO(
        val email: String,
        val password: String
    )

    // DTO for Google Login request
    // Includes email, password, display name, and an optional preferred language
    data class GoogleLoginDTO(
        @SerializedName("idToken") val idToken: String,
        @SerializedName("displayName") val displayName: String?,
        @SerializedName("email") val email: String?,
        @SerializedName("profilePictureUrl") val profilePictureUrl: String?
    )

    // DTO for user registration request
    // Includes email, password, display name, and an optional preferred language (default is "en")
    data class RegisterDTO(
        val email: String,
        val password: String,
        val displayName: String,
        val preferredLanguage: String? = "en"
    )

    // DTO for authentication response
    // Returns access and refresh tokens along with the user's profile information
    data class AuthResponseDTO(
        val accessToken: String?,
        val refreshToken: String?,
        val user: UserProfileDTO?
    )

    // DTO representing a user's profile
    // Contains user ID, email, display name, profile picture URL, preferred language,
    // whether biometric authentication is enabled, and notification preferences
    data class UserProfileDTO(
        val id: String,
        val email: String?,
        val displayName: String?,
        val profilePictureUrl: String?,
        val preferredLanguage: String?,
        val biometricEnabled: Boolean = false,
        val notificationPreferences: Any? = null
    )

    // DTO for updating a user's profile
    // Allows changing the display name, profile picture, and preferred language
    data class UpdateUserProfileDTO(
        val displayName: String,
        val profilePictureUrl: String? = null,
        val preferredLanguage: String? = null
    )

    // DTO for user settings
    // Contains the user's preferred language, biometric authentication status, and notification preferences
    data class UserSettingsDTO(
        val preferredLanguage: String?,
        val biometricEnabled: Boolean,
        val notificationPreferences: Any? = null
    )

    // DTO representing a favorite item
    // Can be a country or content, includes IDs, names/titles, category, and creation timestamp
    data class FavoriteDTO(
        val id: String,
        val countryId: String?,
        val contentId: String?,
        val countryName: String?,
        val contentTitle: String?,
        val categoryName: String?,
        val createdAt: String
    )

    // DTO for adding a favorite item
    // Only requires either a countryId or contentId to add a favorite
    data class AddFavoriteDTO(
        val countryId: String? = null,
        val contentId: String? = null
    )
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]