package com.example.culturex.data.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for GreetingItem data class
 */
class GreetingItemTest {

    @Test
    fun `test GreetingItem creation with all fields`() {
        // Arrange & Act
        val greeting = GreetingItem(
            language = "Zulu",
            greeting = "Sawubona",
            greetingTranslation = "Hello",
            goodbye = "Hamba kahle",
            goodbyeTranslation = "Goodbye",
            pronunciation = "sah-woo-BOH-nah"
        )

        // Assert
        assertEquals("Zulu", greeting.language)
        assertEquals("Sawubona", greeting.greeting)
        assertEquals("Hello", greeting.greetingTranslation)
        assertEquals("Hamba kahle", greeting.goodbye)
        assertEquals("Goodbye", greeting.goodbyeTranslation)
        assertEquals("sah-woo-BOH-nah", greeting.pronunciation)
    }

    @Test
    fun `test GreetingItem with null pronunciation`() {
        // Arrange & Act
        val greeting = GreetingItem(
            language = "English",
            greeting = "Hello",
            greetingTranslation = "Hello",
            goodbye = "Goodbye",
            goodbyeTranslation = "Goodbye",
            pronunciation = null
        )

        // Assert
        assertNull(greeting.pronunciation)
        assertNotNull(greeting.language)
    }

    @Test
    fun `test GreetingItem with default pronunciation`() {
        // Arrange & Act
        val greeting = GreetingItem(
            language = "French",
            greeting = "Bonjour",
            greetingTranslation = "Good day",
            goodbye = "Au revoir",
            goodbyeTranslation = "Goodbye"
        )

        // Assert
        assertNull(greeting.pronunciation)
    }

    @Test
    fun `test GreetingItem with empty goodbye strings`() {
        // Arrange & Act
        val greeting = GreetingItem(
            language = "Japanese",
            greeting = "Konnichiwa",
            greetingTranslation = "Hello",
            goodbye = "",
            goodbyeTranslation = "",
            pronunciation = "kohn-nee-chee-wah"
        )

        // Assert
        assertTrue(greeting.goodbye.isEmpty())
        assertTrue(greeting.goodbyeTranslation.isEmpty())
    }

    @Test
    fun `test GreetingItem equality`() {
        // Arrange
        val greeting1 = GreetingItem(
            language = "Spanish",
            greeting = "Hola",
            greetingTranslation = "Hello",
            goodbye = "Adiós",
            goodbyeTranslation = "Goodbye"
        )

        val greeting2 = GreetingItem(
            language = "Spanish",
            greeting = "Hola",
            greetingTranslation = "Hello",
            goodbye = "Adiós",
            goodbyeTranslation = "Goodbye"
        )

        // Assert
        assertEquals(greeting1, greeting2)
    }

    @Test
    fun `test GreetingItem inequality when language differs`() {
        // Arrange
        val greeting1 = GreetingItem(
            language = "German",
            greeting = "Guten Tag",
            greetingTranslation = "Good day",
            goodbye = "Auf Wiedersehen",
            goodbyeTranslation = "Goodbye"
        )

        val greeting2 = GreetingItem(
            language = "German (Austria)",
            greeting = "Guten Tag",
            greetingTranslation = "Good day",
            goodbye = "Auf Wiedersehen",
            goodbyeTranslation = "Goodbye"
        )

        // Assert
        assertNotEquals(greeting1, greeting2)
    }
}

/**
 * Unit tests for TouristAttraction data class
 */
class TouristAttractionTest {

    @Test
    fun `test TouristAttraction creation with all required fields`() {
        // Arrange & Act
        val attraction = TouristAttraction(
            id = "attr_001",
            name = "Table Mountain",
            category = "Natural Wonder",
            description = "Iconic flat-topped mountain overlooking Cape Town",
            latitude = -33.9628,
            longitude = 18.4098
        )

        // Assert
        assertEquals("attr_001", attraction.id)
        assertEquals("Table Mountain", attraction.name)
        assertEquals("Natural Wonder", attraction.category)
        assertEquals("Iconic flat-topped mountain overlooking Cape Town", attraction.description)
        assertEquals(-33.9628, attraction.latitude, 0.0001)
        assertEquals(18.4098, attraction.longitude, 0.0001)
        assertEquals(0.0, attraction.distanceFromUser, 0.0)
        assertNull(attraction.imageUrl)
    }

    @Test
    fun `test TouristAttraction with image URL`() {
        // Arrange & Act
        val attraction = TouristAttraction(
            id = "attr_002",
            name = "Eiffel Tower",
            category = "Monument",
            description = "Famous iron tower in Paris",
            latitude = 48.8584,
            longitude = 2.2945,
            distanceFromUser = 5.2,
            imageUrl = "https://example.com/eiffel.jpg"
        )

        // Assert
        assertEquals(5.2, attraction.distanceFromUser, 0.001)
        assertEquals("https://example.com/eiffel.jpg", attraction.imageUrl)
    }

    @Test
    fun `test TouristAttraction distance can be updated`() {
        // Arrange
        val attraction = TouristAttraction(
            id = "attr_003",
            name = "Statue of Liberty",
            category = "Monument",
            description = "Symbol of freedom in New York",
            latitude = 40.6892,
            longitude = -74.0445
        )

        // Act
        attraction.distanceFromUser = 12.5

        // Assert
        assertEquals(12.5, attraction.distanceFromUser, 0.001)
    }

    @Test
    fun `test TouristAttraction with negative coordinates`() {
        // Arrange & Act
        val attraction = TouristAttraction(
            id = "attr_004",
            name = "Machu Picchu",
            category = "Historical Site",
            description = "Ancient Incan citadel in Peru",
            latitude = -13.1631,
            longitude = -72.5450
        )

        // Assert
        assertTrue(attraction.latitude < 0)
        assertTrue(attraction.longitude < 0)
    }

    @Test
    fun `test TouristAttraction equality`() {
        // Arrange
        val attraction1 = TouristAttraction(
            id = "same_id",
            name = "Big Ben",
            category = "Landmark",
            description = "Clock tower in London",
            latitude = 51.5007,
            longitude = -0.1246
        )

        val attraction2 = TouristAttraction(
            id = "same_id",
            name = "Big Ben",
            category = "Landmark",
            description = "Clock tower in London",
            latitude = 51.5007,
            longitude = -0.1246
        )

        // Assert
        assertEquals(attraction1, attraction2)
    }

    @Test
    fun `test TouristAttraction with zero distance`() {
        // Arrange & Act
        val attraction = TouristAttraction(
            id = "attr_005",
            name = "Local Museum",
            category = "Museum",
            description = "Museum in current location",
            latitude = 0.0,
            longitude = 0.0,
            distanceFromUser = 0.0
        )

        // Assert
        assertEquals(0.0, attraction.distanceFromUser, 0.0)
    }

    @Test
    fun `test TouristAttraction category validation`() {
        // Arrange & Act
        val categories = listOf(
            TouristAttraction("1", "Name1", "Museum", "Desc", 0.0, 0.0),
            TouristAttraction("2", "Name2", "Park", "Desc", 0.0, 0.0),
            TouristAttraction("3", "Name3", "Monument", "Desc", 0.0, 0.0),
            TouristAttraction("4", "Name4", "Restaurant", "Desc", 0.0, 0.0)
        )

        // Assert
        assertEquals(4, categories.size)
        assertTrue(categories.map { it.category }.contains("Museum"))
        assertTrue(categories.map { it.category }.contains("Restaurant"))
    }

    @Test
    fun `test TouristAttraction distance sorting`() {
        // Arrange
        val attractions = mutableListOf(
            TouristAttraction("1", "Far", "Type", "Desc", 0.0, 0.0, distanceFromUser = 10.0),
            TouristAttraction("2", "Near", "Type", "Desc", 0.0, 0.0, distanceFromUser = 2.0),
            TouristAttraction("3", "Medium", "Type", "Desc", 0.0, 0.0, distanceFromUser = 5.0)
        )

        // Act
        val sortedAttractions = attractions.sortedBy { it.distanceFromUser }

        // Assert
        assertEquals("Near", sortedAttractions[0].name)
        assertEquals("Medium", sortedAttractions[1].name)
        assertEquals("Far", sortedAttractions[2].name)
    }

    @Test
    fun `test TouristAttraction with very long description`() {
        // Arrange
        val longDescription = "This is a very long description ".repeat(50)

        // Act
        val attraction = TouristAttraction(
            id = "attr_006",
            name = "Historical Site",
            category = "History",
            description = longDescription,
            latitude = 0.0,
            longitude = 0.0
        )

        // Assert
        assertTrue(attraction.description.length > 1000)
        assertNotNull(attraction.description)
    }
}