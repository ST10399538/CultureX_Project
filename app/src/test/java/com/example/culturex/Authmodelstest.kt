package com.example.culturex.data.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AuthModels data classes
 */
class AuthModelsTest {

    @Test
    fun `test LoginDTO creation with valid credentials`() {
        // Arrange & Act
        val loginDTO = AuthModels.LoginDTO(
            email = "test@example.com",
            password = "password123"
        )

        // Assert
        assertEquals("test@example.com", loginDTO.email)
        assertEquals("password123", loginDTO.password)
    }

    @Test
    fun `test RegisterDTO with all fields`() {
        // Arrange & Act
        val registerDTO = AuthModels.RegisterDTO(
            email = "newuser@example.com",
            password = "SecurePass123!",
            displayName = "John Doe",
            preferredLanguage = "en"
        )

        // Assert
        assertEquals("newuser@example.com", registerDTO.email)
        assertEquals("SecurePass123!", registerDTO.password)
        assertEquals("John Doe", registerDTO.displayName)
        assertEquals("en", registerDTO.preferredLanguage)
    }

    @Test
    fun `test RegisterDTO with default preferred language`() {
        // Arrange & Act
        val registerDTO = AuthModels.RegisterDTO(
            email = "user@example.com",
            password = "pass123",
            displayName = "Jane Smith"
        )

        // Assert
        assertEquals("en", registerDTO.preferredLanguage)
    }

    @Test
    fun `test GoogleLoginDTO creation`() {
        // Arrange & Act
        val googleLoginDTO = AuthModels.GoogleLoginDTO(
            idToken = "mock_id_token_12345",
            displayName = "Google User",
            email = "googleuser@gmail.com",
            profilePictureUrl = "https://example.com/profile.jpg"
        )

        // Assert
        assertEquals("mock_id_token_12345", googleLoginDTO.idToken)
        assertEquals("Google User", googleLoginDTO.displayName)
        assertEquals("googleuser@gmail.com", googleLoginDTO.email)
        assertEquals("https://example.com/profile.jpg", googleLoginDTO.profilePictureUrl)
    }

    @Test
    fun `test GoogleLoginDTO with null values`() {
        // Arrange & Act
        val googleLoginDTO = AuthModels.GoogleLoginDTO(
            idToken = "token123",
            displayName = null,
            email = null,
            profilePictureUrl = null
        )

        // Assert
        assertEquals("token123", googleLoginDTO.idToken)
        assertNull(googleLoginDTO.displayName)
        assertNull(googleLoginDTO.email)
        assertNull(googleLoginDTO.profilePictureUrl)
    }

    @Test
    fun `test AuthResponseDTO with all fields`() {
        // Arrange
        val userProfile = AuthModels.UserProfileDTO(
            id = "user123",
            email = "test@example.com",
            displayName = "Test User",
            profilePictureUrl = "https://example.com/pic.jpg",
            preferredLanguage = "en",
            biometricEnabled = true,
            notificationPreferences = null
        )

        // Act
        val authResponse = AuthModels.AuthResponseDTO(
            accessToken = "access_token_xyz",
            refreshToken = "refresh_token_abc",
            user = userProfile
        )

        // Assert
        assertEquals("access_token_xyz", authResponse.accessToken)
        assertEquals("refresh_token_abc", authResponse.refreshToken)
        assertNotNull(authResponse.user)
        assertEquals("user123", authResponse.user?.id)
    }

    @Test
    fun `test AuthResponseDTO with null tokens`() {
        // Arrange & Act
        val authResponse = AuthModels.AuthResponseDTO(
            accessToken = null,
            refreshToken = null,
            user = null
        )

        // Assert
        assertNull(authResponse.accessToken)
        assertNull(authResponse.refreshToken)
        assertNull(authResponse.user)
    }

    @Test
    fun `test UserProfileDTO with biometric disabled by default`() {
        // Arrange & Act
        val userProfile = AuthModels.UserProfileDTO(
            id = "user456",
            email = "user@test.com",
            displayName = "Regular User",
            profilePictureUrl = null,
            preferredLanguage = "es"
        )

        // Assert
        assertFalse(userProfile.biometricEnabled)
        assertEquals("user456", userProfile.id)
        assertEquals("es", userProfile.preferredLanguage)
    }

    @Test
    fun `test UpdateUserProfileDTO creation`() {
        // Arrange & Act
        val updateProfile = AuthModels.UpdateUserProfileDTO(
            displayName = "Updated Name",
            profilePictureUrl = "https://newpic.com/image.png",
            preferredLanguage = "fr"
        )

        // Assert
        assertEquals("Updated Name", updateProfile.displayName)
        assertEquals("https://newpic.com/image.png", updateProfile.profilePictureUrl)
        assertEquals("fr", updateProfile.preferredLanguage)
    }

    @Test
    fun `test UpdateUserProfileDTO with optional null values`() {
        // Arrange & Act
        val updateProfile = AuthModels.UpdateUserProfileDTO(
            displayName = "Name Only"
        )

        // Assert
        assertEquals("Name Only", updateProfile.displayName)
        assertNull(updateProfile.profilePictureUrl)
        assertNull(updateProfile.preferredLanguage)
    }

    @Test
    fun `test UserSettingsDTO creation`() {
        // Arrange & Act
        val settings = AuthModels.UserSettingsDTO(
            preferredLanguage = "de",
            biometricEnabled = true,
            notificationPreferences = mapOf("email" to true, "push" to false)
        )

        // Assert
        assertEquals("de", settings.preferredLanguage)
        assertTrue(settings.biometricEnabled)
        assertNotNull(settings.notificationPreferences)
    }

    @Test
    fun `test FavoriteDTO for country favorite`() {
        // Arrange & Act
        val favorite = AuthModels.FavoriteDTO(
            id = "fav123",
            countryId = "country_za",
            contentId = null,
            countryName = "South Africa",
            contentTitle = null,
            categoryName = null,
            createdAt = "2024-01-15T10:30:00Z"
        )

        // Assert
        assertEquals("fav123", favorite.id)
        assertEquals("country_za", favorite.countryId)
        assertNull(favorite.contentId)
        assertEquals("South Africa", favorite.countryName)
        assertEquals("2024-01-15T10:30:00Z", favorite.createdAt)
    }

    @Test
    fun `test FavoriteDTO for content favorite`() {
        // Arrange & Act
        val favorite = AuthModels.FavoriteDTO(
            id = "fav456",
            countryId = null,
            contentId = "content_123",
            countryName = null,
            contentTitle = "Dress Code Guide",
            categoryName = "Dress Code",
            createdAt = "2024-02-20T14:45:00Z"
        )

        // Assert
        assertEquals("content_123", favorite.contentId)
        assertEquals("Dress Code Guide", favorite.contentTitle)
        assertEquals("Dress Code", favorite.categoryName)
    }

    @Test
    fun `test AddFavoriteDTO with country ID`() {
        // Arrange & Act
        val addFavorite = AuthModels.AddFavoriteDTO(
            countryId = "country_us"
        )

        // Assert
        assertEquals("country_us", addFavorite.countryId)
        assertNull(addFavorite.contentId)
    }

    @Test
    fun `test AddFavoriteDTO with content ID`() {
        // Arrange & Act
        val addFavorite = AuthModels.AddFavoriteDTO(
            contentId = "content_789"
        )

        // Assert
        assertNull(addFavorite.countryId)
        assertEquals("content_789", addFavorite.contentId)
    }

    @Test
    fun `test AddFavoriteDTO with both IDs null by default`() {
        // Arrange & Act
        val addFavorite = AuthModels.AddFavoriteDTO()

        // Assert
        assertNull(addFavorite.countryId)
        assertNull(addFavorite.contentId)
    }
}