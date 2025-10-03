package com.example.culturex

import com.example.culturex.data.models.ItineraryEvent
import com.example.culturex.data.models.TouristAttraction
import org.junit.Test
import org.junit.Assert.*

class DataModelTest {

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

    @Test
    fun testAttractionFiltering() {
        val attractions = listOf(
            TouristAttraction("1", "Table Mountain", "Natural Wonder", "Mountain", -33.9628, 18.4098),
            TouristAttraction("2", "Robben Island", "Historical Site", "Island", -33.8070, 18.3704),
            TouristAttraction("3", "Kirstenbosch", "Garden", "Garden", -33.9880, 18.4325)
        )

        val historicalSites = attractions.filter { it.category == "Historical Site" }
        assertEquals(1, historicalSites.size)
        assertEquals("Robben Island", historicalSites[0].name)

        // Fixed: "Island" appears in name, not description
        val nameContainsIsland = attractions.filter { it.name.contains("Island", ignoreCase = true) }
        assertEquals(1, nameContainsIsland.size)
    }

    @Test
    fun testEventSortingByTime() {
        val events = listOf(
            ItineraryEvent(date = "15/12/2025", time = "14:00", description = "Lunch"),
            ItineraryEvent(date = "15/12/2025", time = "09:00", description = "Breakfast"),
            ItineraryEvent(date = "15/12/2025", time = "19:00", description = "Dinner")
        )

        val sorted = events.sortedBy { it.time }

        assertEquals("09:00", sorted[0].time)
        assertEquals("14:00", sorted[1].time)
        assertEquals("19:00", sorted[2].time)
    }

    @Test
    fun testAttractionDistanceCalculation() {
        val attraction = TouristAttraction(
            "1", "Test", "Category", "Desc", 0.0, 0.0
        )

        attraction.distanceFromUser = 12.345

        val roundedDistance = String.format("%.1f", attraction.distanceFromUser).toDouble()
        assertEquals(12.3, roundedDistance, 0.01)
    }

    @Test
    fun testEventWithSpecialCharacters() {
        val event = ItineraryEvent(
            date = "15/12/2025",
            time = "10:00",
            description = "Visit café & museum! #CultureX @2025"
        )

        assertTrue(event.description.contains("&"))
        assertTrue(event.description.contains("!"))
        assertTrue(event.description.contains("#"))
        assertTrue(event.description.contains("@"))
    }

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