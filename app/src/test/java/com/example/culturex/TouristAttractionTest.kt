package com.example.culturex

import com.example.culturex.data.models.TouristAttraction
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class TouristAttractionTest {

    // Two TouristAttraction objects used for testing
    private lateinit var tableMountain: TouristAttraction
    private lateinit var krugerPark: TouristAttraction

    // Initialize sample tourist attractions before each test
    @Before
    fun setup() {
        tableMountain = TouristAttraction(
            id = "1",
            name = "Table Mountain",
            category = "Natural Wonder",
            description = "Iconic flat-topped mountain overlooking Cape Town",
            latitude = -33.9628,
            longitude = 18.4098
        )

        krugerPark = TouristAttraction(
            id = "2",
            name = "Kruger National Park",
            category = "Wildlife Reserve",
            description = "One of Africa's largest game reserves",
            latitude = -23.9884,
            longitude = 31.5547
        )
    }

    // Test that attributes of the Table Mountain attraction are set correctly
    @Test
    fun testTouristAttractionCreation() {
        assertEquals("Table Mountain", tableMountain.name)
        assertEquals("Natural Wonder", tableMountain.category)
        assertEquals(-33.9628, tableMountain.latitude, 0.0001)
        assertEquals(18.4098, tableMountain.longitude, 0.0001)
        assertEquals(0.0, tableMountain.distanceFromUser, 0.0001)
    }

    // Test that distanceFromUser can be updated correctly
    @Test
    fun testDistanceCalculation() {
        tableMountain.distanceFromUser = 15.5
        assertEquals(15.5, tableMountain.distanceFromUser, 0.0001)
    }

    // Ensure Table Mountain and Kruger Park are distinct in all key attributes
    @Test
    fun testMultipleAttractions() {
        assertNotEquals(tableMountain.id, krugerPark.id)
        assertNotEquals(tableMountain.name, krugerPark.name)
        assertNotEquals(tableMountain.latitude, krugerPark.latitude, 0.0001)
        assertNotEquals(tableMountain.longitude, krugerPark.longitude, 0.0001)
    }

    // Latitude should always be between -90 and 90
    @Test
    fun testLatitudeValidRange() {
        assertTrue("Latitude should be between -90 and 90",
            tableMountain.latitude >= -90 && tableMountain.latitude <= 90)
        assertTrue("Latitude should be between -90 and 90",
            krugerPark.latitude >= -90 && krugerPark.latitude <= 90)
    }

    // Longitude should always be between -180 and 180
    @Test
    fun testLongitudeValidRange() {
        assertTrue("Longitude should be between -180 and 180",
            tableMountain.longitude >= -180 && tableMountain.longitude <= 180)
        assertTrue("Longitude should be between -180 and 180",
            krugerPark.longitude >= -180 && krugerPark.longitude <= 180)
    }

    // Distance from user should not be negative
    @Test
    fun testDistanceIsNonNegative() {
        tableMountain.distanceFromUser = 100.5
        assertTrue("Distance should be non-negative", tableMountain.distanceFromUser >= 0)
    }

    // Test that a TouristAttraction can have a null imageUrl
    @Test
    fun testAttractionWithNullImageUrl() {
        val attraction = TouristAttraction(
            id = "3",
            name = "Test Attraction",
            category = "Test",
            description = "Test description",
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = null
        )

        assertNull(attraction.imageUrl)
    }

    // Test that a TouristAttraction can have a valid imageUrl
    @Test
    fun testAttractionWithImageUrl() {
        val attraction = TouristAttraction(
            id = "3",
            name = "Test Attraction",
            category = "Test",
            description = "Test description",
            latitude = 0.0,
            longitude = 0.0,
            imageUrl = "https://example.com/image.jpg"
        )

        assertNotNull(attraction.imageUrl)
        assertTrue(attraction.imageUrl!!.startsWith("https://"))
    }

    // Ensure attractions belong to one of the allowed categories
    @Test
    fun testCategoryTypes() {
        val categories = listOf("Natural Wonder", "Wildlife Reserve", "Museum",
            "Historical Site", "Beach", "Mountain")

        assertTrue(categories.contains(tableMountain.category))
        assertTrue(categories.contains(krugerPark.category))
    }

    // Description should not be empty and should have meaningful content
    @Test
    fun testDescriptionNotEmpty() {
        assertFalse("Description should not be empty", tableMountain.description.isEmpty())
        assertTrue("Description should have content", tableMountain.description.length > 10)
    }

    // Test sorting a list of attractions by distance from user
    @Test
    fun testDistanceSorting() {
        val attractions = mutableListOf(
            TouristAttraction("1", "A", "Cat", "Desc", 0.0, 0.0).apply { distanceFromUser = 50.0 },
            TouristAttraction("2", "B", "Cat", "Desc", 0.0, 0.0).apply { distanceFromUser = 10.0 },
            TouristAttraction("3", "C", "Cat", "Desc", 0.0, 0.0).apply { distanceFromUser = 30.0 }
        )
// Sort attractions by ascending distance
        val sorted = attractions.sortedBy { it.distanceFromUser }

        // Verify correct order: 10.0 -> 30.0 -> 50.0
        assertEquals(10.0, sorted[0].distanceFromUser, 0.0001)
        assertEquals(30.0, sorted[1].distanceFromUser, 0.0001)
        assertEquals(50.0, sorted[2].distanceFromUser, 0.0001)
    }
}

// Reference List
// Android Developers. (2025a). Test apps on Android. [online] Available at: https://developer.android.com/training/testing.
// Android Developers. (2025b). (Deprecated) Advanced Android in Kotlin 05.1: Testing Basics  |  Android Developers. [online] Available at: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics?index=..%2F..index#0 [Accessed 25 Aug. 2025].
// Sproviero, F. (2018). Android Unit Testing with Mockito. [online] kodeco.com. Available at: https://www.kodeco.com/195-android-unit-testing-with-mockito [Accessed 25 Aug. 2025].
// Bechtold, S. (2016). JUnit 5 User Guide. [online] Junit.org. Available at: https://docs.junit.org/current/user-guide/.
// henrymbuguakiarie (2025). Call a web API in a sample Android mobile app - Microsoft identity platform. [online] Microsoft.com. Available at: https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-native-authentication-android-call-api [Accessed 25 Aug. 2025].
// Android Developers. (2025). Token  |  Android Developers. [online] Available at: https://developer.android.com/reference/androidx/browser/trusted/Token.