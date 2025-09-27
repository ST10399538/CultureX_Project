package com.example.culturex.utils

import android.content.Context
import android.content.SharedPreferences


class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CultureX_Prefs", Context.MODE_PRIVATE)

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val REFRESH_TOKEN = "refresh_token"
        private const val USER_ID = "user_id"
        private const val USER_EMAIL = "user_email"
        private const val USER_DISPLAY_NAME = "user_display_name"
    }

    fun saveAuthData(accessToken: String?, refreshToken: String?, userId: String?, email: String?, displayName: String?) {
        with(sharedPreferences.edit()) {
            putString(ACCESS_TOKEN, accessToken)
            putString(REFRESH_TOKEN, refreshToken)
            putString(USER_ID, userId)
            putString(USER_EMAIL, email)
            putString(USER_DISPLAY_NAME, displayName)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString(ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = sharedPreferences.getString(REFRESH_TOKEN, null)
    fun getUserId(): String? = sharedPreferences.getString(USER_ID, null)
    fun getUserEmail(): String? = sharedPreferences.getString(USER_EMAIL, null)
    fun getUserDisplayName(): String? = sharedPreferences.getString(USER_DISPLAY_NAME, null)

    fun clearAuthData() {
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN)
            remove(REFRESH_TOKEN)
            remove(USER_ID)
            remove(USER_EMAIL)
            remove(USER_DISPLAY_NAME)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrEmpty()
}
