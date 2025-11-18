package com.example.culturex.data.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CountryModels data classes
 */
class CountryModelsTest {

    @Test
    fun `test CountryDTO creation with all fields`() {
        // Arrange & Act
        val country = CountryModels.CountryDTO(
            id = "za",
            name = "South Africa",
            countryCode = "ZA",
            flagImageUrl = "https://example.com/flags/za.png",
            description = "Rainbow nation of Africa",
            timezone = "Africa/Johannesburg",
            currency = "ZAR",
            emergencyContacts = mapOf("police" to "10111", "ambulance" to "10177")
        )

        // Assert
        assertEquals("za", country.id)
        assertEquals("South Africa", country.name)
        assertEquals("ZA", country.countryCode)
        assertEquals("https://example.com/flags/za.png", country.flagImageUrl)
        assertEquals("Rainbow nation of Africa", country.description)
        assertEquals("Africa/Johannesburg", country.timezone)
        assertEquals("ZAR", country.currency)
        assertNotNull(country.emergencyContacts)
    }

    @Test
    fun `test CountryDTO with null optional fields`() {
        // Arrange & Act
        val country = CountryModels.CountryDTO(
            id = "us",
            name = null,
            countryCode = null,
            flagImageUrl = null,
            description = null,
            timezone = null,
            currency = null,
            emergencyContacts = null
        )

        // Assert
        assertEquals("us", country.id)
        assertNull(country.name)
        assertNull(country.countryCode)
        assertNull(country.flagImageUrl)
        assertNull(country.description)
        assertNull(country.timezone)
        assertNull(country.currency)
        assertNull(country.emergencyContacts)
    }

    @Test
    fun `test CulturalCategoryDTO with default sort order`() {
        // Arrange & Act
        val category = CountryModels.CulturalCategoryDTO(
            id = "cat1",
            name = "Dress Code",
            description = "Cultural dress guidelines",
            iconUrl = "https://example.com/icons/dress.png"
        )

        // Assert
        assertEquals("cat1", category.id)
        assertEquals("Dress Code", category.name)
        assertEquals("Cultural dress guidelines", category.description)
        assertEquals("https://example.com/icons/dress.png", category.iconUrl)
        assertEquals(0, category.sortOrder)
    }

    @Test
    fun `test CulturalCategoryDTO with custom sort order`() {
        // Arrange & Act
        val category = CountryModels.CulturalCategoryDTO(
            id = "cat2",
            name = "Etiquette",
            description = "Social customs and manners",
            iconUrl = "https://example.com/icons/etiquette.png",
            sortOrder = 5
        )

        // Assert
        assertEquals(5, category.sortOrder)
        assertEquals("Etiquette", category.name)
    }

    @Test
    fun `test CulturalCategoryDTO with null optional fields`() {
        // Arrange & Act
        val category = CountryModels.CulturalCategoryDTO(
            id = "cat3",
            name = null,
            description = null,
            iconUrl = null,
            sortOrder = 3
        )

        // Assert
        assertEquals("cat3", category.id)
        assertNull(category.name)
        assertNull(category.description)
        assertNull(category.iconUrl)
        assertEquals(3, category.sortOrder)
    }

    @Test
    fun `test CulturalContentDTO with complete data`() {
        // Arrange & Act
        val content = CountryModels.CulturalContentDTO(
            id = "content123",
            countryId = "za",
            categoryId = "dress_code",
            title = "South African Dress Code",
            content = "Detailed content about dress code...",
            dos = listOf("Dress modestly", "Wear comfortable shoes"),
            donts = listOf("Don't wear offensive clothing", "Avoid overly casual attire in formal settings"),
            examples = listOf("Smart casual for business", "Traditional attire for ceremonies"),
            countryName = "South Africa",
            categoryName = "Dress Code"
        )

        // Assert
        assertEquals("content123", content.id)
        assertEquals("za", content.countryId)
        assertEquals("dress_code", content.categoryId)
        assertEquals("South African Dress Code", content.title)
        assertNotNull(content.content)
        assertEquals(2, content.dos?.size)
        assertEquals(2, content.donts?.size)
        assertEquals(2, content.examples?.size)
        assertEquals("South Africa", content.countryName)
        assertEquals("Dress Code", content.categoryName)
    }

    @Test
    fun `test CulturalContentDTO with null lists`() {
        // Arrange & Act
        val content = CountryModels.CulturalContentDTO(
            id = "content456",
            countryId = "us",
            categoryId = "etiquette",
            title = "American Etiquette",
            content = "Content here...",
            dos = null,
            donts = null,
            examples = null,
            countryName = "United States",
            categoryName = "Etiquette"
        )

        // Assert
        assertEquals("content456", content.id)
        assertNull(content.dos)
        assertNull(content.donts)
        assertNull(content.examples)
        assertNotNull(content.title)
    }

    @Test
    fun `test CulturalContentDTO with empty lists`() {
        // Arrange & Act
        val content = CountryModels.CulturalContentDTO(
            id = "content789",
            countryId = "uk",
            categoryId = "communication",
            title = "UK Communication Style",
            content = "British communication norms...",
            dos = emptyList(),
            donts = emptyList(),
            examples = emptyList(),
            countryName = "United Kingdom",
            categoryName = "Communication"
        )

        // Assert
        assertEquals("content789", content.id)
        assertTrue(content.dos?.isEmpty() ?: false)
        assertTrue(content.donts?.isEmpty() ?: false)
        assertTrue(content.examples?.isEmpty() ?: false)
    }

    @Test
    fun `test CulturalContentDTO dos list contains correct items`() {
        // Arrange
        val dosList = listOf(
            "Be punctual for meetings",
            "Respect personal space",
            "Use formal titles when appropriate"
        )

        // Act
        val content = CountryModels.CulturalContentDTO(
            id = "content_test",
            countryId = "de",
            categoryId = "business",
            title = "German Business Etiquette",
            content = "German business culture...",
            dos = dosList,
            donts = null,
            examples = null,
            countryName = "Germany",
            categoryName = "Business Etiquette"
        )

        // Assert
        assertEquals(3, content.dos?.size)
        assertTrue(content.dos?.contains("Be punctual for meetings") ?: false)
        assertTrue(content.dos?.contains("Respect personal space") ?: false)
    }

    @Test
    fun `test CulturalContentDTO equality`() {
        // Arrange
        val content1 = CountryModels.CulturalContentDTO(
            id = "same_id",
            countryId = "fr",
            categoryId = "dining",
            title = "French Dining",
            content = "Content",
            dos = listOf("Do this"),
            donts = listOf("Don't do that"),
            examples = listOf("Example 1"),
            countryName = "France",
            categoryName = "Dining"
        )

        val content2 = CountryModels.CulturalContentDTO(
            id = "same_id",
            countryId = "fr",
            categoryId = "dining",
            title = "French Dining",
            content = "Content",
            dos = listOf("Do this"),
            donts = listOf("Don't do that"),
            examples = listOf("Example 1"),
            countryName = "France",
            categoryName = "Dining"
        )

        // Assert
        assertEquals(content1, content2)
        assertEquals(content1.hashCode(), content2.hashCode())
    }
}