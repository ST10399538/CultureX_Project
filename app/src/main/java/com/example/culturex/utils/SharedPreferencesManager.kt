package com.example.culturex.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesManager(context: Context) {

    companion object {
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
        private const val KEY_PHONE_NUMBER = "phone_number" // NEW
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthData(
        accessToken: String?,
        refreshToken: String?,
        userId: String?,
        email: String?,
        displayName: String?,
        profilePictureUrl: String? = null,
        preferredLanguage: String? = null,
        biometricEnabled: Boolean = false,
        phoneNumber: String? = null // NEW
    ) {
        Log.d("SharedPreferencesManager", "Saving auth data for user: $email")
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_DISPLAY_NAME, displayName)
            putString(KEY_PROFILE_PICTURE_URL, profilePictureUrl)
            putString(KEY_PREFERRED_LANGUAGE, preferredLanguage)
            putBoolean(KEY_BIOMETRIC_ENABLED, biometricEnabled)
            putString(KEY_PHONE_NUMBER, phoneNumber) // NEW
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        Log.d("SharedPreferencesManager", "Auth data saved successfully")
    }

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

    fun getPhoneNumber(): String? { // NEW
        return sharedPreferences.getString(KEY_PHONE_NUMBER, null)
    }

    fun isLoggedIn(): Boolean {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val hasAccessToken = getAccessToken() != null
        val result = isLoggedIn && hasAccessToken
        Log.d("SharedPreferencesManager", "Checking login status: isLoggedIn=$isLoggedIn, hasToken=$hasAccessToken, result=$result")
        return result
    }

    fun updateUserProfile(
        displayName: String?,
        profilePictureUrl: String? = null,
        preferredLanguage: String? = null,
        phoneNumber: String? = null // NEW
    ) {
        with(sharedPreferences.edit()) {
            displayName?.let { putString(KEY_DISPLAY_NAME, it) }
            profilePictureUrl?.let { putString(KEY_PROFILE_PICTURE_URL, it) }
            preferredLanguage?.let { putString(KEY_PREFERRED_LANGUAGE, it) }
            phoneNumber?.let { putString(KEY_PHONE_NUMBER, it) } // NEW
            apply()
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_BIOMETRIC_ENABLED, enabled)
            apply()
        }
    }

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
            remove(KEY_PHONE_NUMBER) // NEW
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        Log.d("SharedPreferencesManager", "Auth data cleared")
    }

    fun logout() {
        clearAuthData()
    }

    fun hasCompleteUserData(): Boolean {
        return !getEmail().isNullOrEmpty() &&
                !getDisplayName().isNullOrEmpty() &&
                !getUserId().isNullOrEmpty()
    }

    fun logCurrentState() {
        Log.d("SharedPreferencesManager", """
            Current auth state:
            - Is logged in: ${isLoggedIn()}
            - Has access token: ${!getAccessToken().isNullOrEmpty()}
            - User ID: ${getUserId()}
            - Email: ${getEmail()}
            - Display name: ${getDisplayName()}
            - Phone number: ${getPhoneNumber()}
            - Preferred language: ${getPreferredLanguage()}
            - Biometric enabled: ${isBiometricEnabled()}
        """.trimIndent())
    }
}