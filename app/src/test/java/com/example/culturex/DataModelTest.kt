package com.example.culturex

import com.example.culturex.data.models.ItineraryEvent
import com.example.culturex.data.models.TouristAttraction
import org.junit.Test
import org.junit.Assert.*

class DataModelTest {

    // Create a mutable list of itinerary events
    @Test
    fun testItineraryEventListOperations() {
        val events = mutableListOf(
            ItineraryEvent(date = "15/12/2025", time = "10:00", description = "Event 1"),
            ItineraryEvent(date = "15/12/2025", time = "14:00", description = "Event 2"),
            ItineraryEvent(date = "15/12/2025", time = "18:00", description = "Event 3")
        )

        assertEquals(3, events.size)
        assertEquals("Event 1", events[0].description)

        events.add(ItineraryEvent(date = "16/12/2025", time = "09:00", description = "Event 4"))
        assertEquals(4, events.size)

        events.removeAt(0)
        assertEquals(3, events.size)
        assertEquals("Event 2", events[0].description)
    }
    // Check the description of the first event
    // Verify list contains 3 events initially
    // Verify list size increased to 4

    // Create a list of tourist attractions
    @Test
    fun testAttractionFiltering() {
        val attractions = listOf(
            TouristAttraction("1", "Table Mountain", "Natural Wonder", "Mountain", -33.9628, 18.4098),
            TouristAttraction("2", "Robben Island", "Historical Site", "Island", -33.8070, 18.3704),
            TouristAttraction("3", "Kirstenbosch", "Garden", "Garden", -33.9880, 18.4325)
        )

        // Filter only historical sites
        val historicalSites = attractions.filter { it.category == "Historical Site" }
        assertEquals(1, historicalSites.size)
        assertEquals("Robben Island", historicalSites[0].name)

        // Filter attractions whose name contains "Island"
        val nameContainsIsland = attractions.filter { it.name.contains("Island", ignoreCase = true) }
        assertEquals(1, nameContainsIsland.size)
    }

    // Create unsorted events by time
    @Test
    fun testEventSortingByTime() {
        val events = listOf( // Verify correct order after sorting
            ItineraryEvent(date = "15/12/2025", time = "14:00", description = "Lunch"),
            ItineraryEvent(date = "15/12/2025", time = "09:00", description = "Breakfast"),
            ItineraryEvent(date = "15/12/2025", time = "19:00", description = "Dinner")
        )

        val sorted = events.sortedBy { it.time }
// Verify correct order after sorting
        assertEquals("09:00", sorted[0].time)
        assertEquals("14:00", sorted[1].time)
        assertEquals("19:00", sorted[2].time)
    }

    // Create a tourist attraction
    @Test
    fun testAttractionDistanceCalculation() {
        val attraction = TouristAttraction(
            "1", "Test", "Category", "Desc", 0.0, 0.0
        )

        attraction.distanceFromUser = 12.345 // Assign distance from user

        val roundedDistance = String.format("%.1f", attraction.distanceFromUser).toDouble() // Round distance to one decimal place
        assertEquals(12.3, roundedDistance, 0.01) // Verify rounding works correctly
    }

    // Create an event with special characters in the description
    @Test
    fun testEventWithSpecialCharacters() {
        val event = ItineraryEvent(
            date = "15/12/2025",
            time = "10:00",
            description = "Visit café & museum! #CultureX @2025"
        )
// Verify special characters exist in the description
        assertTrue(event.description.contains("&"))
        assertTrue(event.description.contains("!"))
        assertTrue(event.description.contains("#"))
        assertTrue(event.description.contains("@"))
    }

    // Create a list of attractions
    @Test
    fun testAttractionSearchFunctionality() {
        val attractions = listOf(
            TouristAttraction("1", "Table Mountain", "Natural", "Mountain view", 0.0, 0.0),
            TouristAttraction("2", "Kruger Park", "Wildlife", "Safari park", 0.0, 0.0),
            TouristAttraction("3", "Table Bay", "Water", "Bay area", 0.0, 0.0)
        )

        val searchQuery = "Table"
        val results = attractions.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }

        assertEquals(2, results.size)
        assertTrue(results.any { it.name == "Table Mountain" })
        assertTrue(results.any { it.name == "Table Bay" })
    }

    // Verify collections are empty
    @Test
    fun testEmptyCollections() {
        val emptyEvents = emptyList<ItineraryEvent>()
        val emptyAttractions = emptyList<TouristAttraction>()

        assertTrue(emptyEvents.isEmpty())
        assertTrue(emptyAttractions.isEmpty())
        assertEquals(0, emptyEvents.size)
        assertEquals(0, emptyAttractions.size)
    }
}

// Reference List
// Android Developers. (2025a). Test apps on Android. [online] Available at: https://developer.android.com/training/testing.
// Android Developers. (2025b). (Deprecated) Advanced Android in Kotlin 05.1: Testing Basics  |  Android Developers. [online] Available at: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics?index=..%2F..index#0 [Accessed 25 Aug. 2025].
// Sproviero, F. (2018). Android Unit Testing with Mockito. [online] kodeco.com. Available at: https://www.kodeco.com/195-android-unit-testing-with-mockito [Accessed 25 Aug. 2025].
// Bechtold, S. (2016). JUnit 5 User Guide. [online] Junit.org. Available at: https://docs.junit.org/current/user-guide/.
// henrymbuguakiarie (2025). Call a web API in a sample Android mobile app - Microsoft identity platform. [online] Microsoft.com. Available at: https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-native-authentication-android-call-api [Accessed 25 Aug. 2025].
// Android Developers. (2025). Token  |  Android Developers. [online] Available at: https://developer.android.com/reference/androidx/browser/trusted/Token.