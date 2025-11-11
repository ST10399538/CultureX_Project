package com.example.culturex.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesManager(context: Context) {

    // Name of the SharedPreferences file
    companion object {
        // Keys for storing different types of data
        private const val PREF_NAME = "culturex_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_PROFILE_PICTURE_URL = "profile_picture_url"
        private const val KEY_PREFERRED_LANGUAGE = "preferred_language"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_PHONE_NUMBER = "phone_number"

        // Additional settings keys
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_MODE_ENABLED = "dark_mode_enabled"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
        private const val KEY_APP_VERSION = "app_version"
    }

    // Obtain SharedPreferences instance for reading and writing
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Save all authentication and profile-related data
    fun saveAuthData(
        accessToken: String?,
        refreshToken: String?,
        userId: String?,
        email: String?,
        displayName: String?,
        profilePictureUrl: String? = null,
        preferredLanguage: String? = null,
        biometricEnabled: Boolean = false,
        phoneNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null
    ) {
        Log.d("SharedPreferencesManager", "Saving auth data for user: $email")
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_DISPLAY_NAME, displayName)
            putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl)
            putString(KEY_PREFERRED_LANGUAGE, preferredLanguage ?: "en")
            putBoolean(KEY_BIOMETRIC_ENABLED, biometricEnabled)
            putString(KEY_PHONE_NUMBER, phoneNumber)
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_LAST_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
        Log.d("SharedPreferencesManager", "Auth data saved successfully")
    }

    // Getter functions to retrieve stored data

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun getDisplayName(): String? {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, null)
    }

    fun getProfilePictureUrl(): String? {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE_URL, null)
    }

    fun getPreferredLanguage(): String? {
        return sharedPreferences.getString(KEY_PREFERRED_LANGUAGE, "en")
    }

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun getPhoneNumber(): String? {
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null)
    }

    fun getFirstName(): String? {
        return sharedPreferences.getString(KEY_FIRST_NAME, null)
    }

    fun getLastName(): String? {
        return sharedPreferences.getString(KEY_LAST_NAME, null)
    }

    fun getLastLoginTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_LOGIN_TIME, 0L)
    }

    // NEW: Notifications settings
    fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            apply()
        }
        Log.d("SharedPreferencesManager", "Notifications enabled: $enabled")
    }

    // Dark mode settings
    fun isDarkModeEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE_ENABLED, false)
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_DARK_MODE_ENABLED, enabled)
            apply()
        }
        Log.d("SharedPreferencesManager", "Dark mode enabled: $enabled")
    }

    // App version tracking
    fun getAppVersion(): String? {
        return sharedPreferences.getString(KEY_APP_VERSION, null)
    }

    fun setAppVersion(version: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_APP_VERSION, version)
            apply()
        }
    }

    // Check if the user is logged in by verifying flag and token
    fun isLoggedIn(): Boolean {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val hasAccessToken = getAccessToken() != null
        val result = isLoggedIn && hasAccessToken
        Log.d("SharedPreferencesManager", "Checking login status: isLoggedIn=$isLoggedIn, hasToken=$hasAccessToken, result=$result")
        return result
    }

    // Update only user profile-related information
    fun updateUserProfile(
        displayName: String? = null,
        profilePictureUrl: String? = null,
        preferredLanguage: String? = null,
        phoneNumber: String? = null,
        firstName: String? = null,
        lastName: String? = null
    ) {
        with(sharedPreferences.edit()) {
            displayName?.let { putString(KEY_DISPLAY_NAME, it) }
            profilePictureUrl?.let { putString(KEY_PROFILE_PICTURE_URL, it) }
            preferredLanguage?.let { putString(KEY_PREFERRED_LANGUAGE, it) }
            phoneNumber?.let { putString(KEY_PHONE_NUMBER, it) }
            firstName?.let { putString(KEY_FIRST_NAME, it) }
            lastName?.let { putString(KEY_LAST_NAME, it) }
            apply()
        }
        Log.d("SharedPreferencesManager", "User profile updated")
    }

    // Enable or disable biometric login
    fun setBiometricEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            apply()
        }
        Log.d("SharedPreferencesManager", "Biometric enabled: $enabled")
    }

    // Clear all authentication-related data from SharedPreferences
    fun clearAuthData() {
        Log.d("SharedPreferencesManager", "Clearing auth data")
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_EMAIL)
            remove(KEY_DISPLAY_NAME)
            remove(KEY_PROFILE_PICTURE_URL)
            remove(KEY_PREFERRED_LANGUAGE)
            remove(KEY_BIOMETRIC_ENABLED)
            remove(KEY_PHONE_NUMBER)
            remove(KEY_FIRST_NAME)
            remove(KEY_LAST_NAME)
            remove(KEY_LAST_LOGIN_TIME)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        Log.d("SharedPreferencesManager", "Auth data cleared")
    }

    // Clear all settings (including theme and notifications)
    fun clearAllSettings() {
        Log.d("SharedPreferencesManager", "Clearing all settings")
        sharedPreferences.edit().clear().apply()
        Log.d("SharedPreferencesManager", "All settings cleared")
    }

    // Convenience method to logout user
    fun logout() {
        clearAuthData()
    }

    // Check if all essential user data is available
    fun hasCompleteUserData(): Boolean {
        return !getEmail().isNullOrEmpty() &&
                !getDisplayName().isNullOrEmpty() &&
                !getUserId().isNullOrEmpty()
    }

    // Get formatted last login time
    fun getFormattedLastLoginTime(): String {
        val lastLogin = getLastLoginTime()
        if (lastLogin == 0L) return "Never"

        val currentTime = System.currentTimeMillis()
        val diff = currentTime - lastLogin

        return when {
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} minutes ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            else -> "${diff / 86400000} days ago"
        }
    }

    // Debugging helper to log current stored state
    fun logCurrentState() {
        Log.d("SharedPreferencesManager", """
            Current auth state:
            - Is logged in: ${isLoggedIn()}
            - Has access token: ${!getAccessToken().isNullOrEmpty()}
            - User ID: ${getUserId()}
            - Email: ${getEmail()}
            - Display name: ${getDisplayName()}
            - First name: ${getFirstName()}
            - Last name: ${getLastName()}
            - Phone number: ${getPhoneNumber()}
            - Preferred language: ${getPreferredLanguage()}
            - Biometric enabled: ${isBiometricEnabled()}
            - Notifications enabled: ${isNotificationsEnabled()}
            - Dark mode enabled: ${isDarkModeEnabled()}
            - Last login: ${getFormattedLastLoginTime()}
            - App version: ${getAppVersion()}
        """.trimIndent())
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