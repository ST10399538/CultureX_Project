package com.example.culturex

import com.example.culturex.data.models.ItineraryEvent
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ItineraryEventTest {

    private lateinit var testEvent: ItineraryEvent

    @Before
    fun setup() {
        testEvent = ItineraryEvent(
            id = "test_123",
            date = "15/12/2025",
            time = "14:30",
            description = "Safari at Kruger National Park"
        )
    }

    @Test
    fun testItineraryEventCreation() {
        assertEquals("test_123", testEvent.id)
        assertEquals("15/12/2025", testEvent.date)
        assertEquals("14:30", testEvent.time)
        assertEquals("Safari at Kruger National Park", testEvent.description)
        assertTrue(testEvent.timestamp > 0)
    }

    @Test
    fun testItineraryEventWithDefaultId() {
        val event = ItineraryEvent(
            date = "20/01/2025",
            time = "09:00",
            description = "Morning tour"
        )

        assertNotNull(event.id)
        assertFalse(event.id.isEmpty())
        assertTrue(event.id.length > 0)
    }

    @Test
    fun testMultipleEventsHaveUniqueIds() {
        val event1 = ItineraryEvent(date = "01/01/2025", time = "10:00", description = "Event 1")
        Thread.sleep(10) // Add small delay to ensure unique timestamps
        val event2 = ItineraryEvent(date = "01/01/2025", time = "11:00", description = "Event 2")

        assertNotEquals("Events should have unique IDs", event1.id, event2.id)
    }

    @Test
    fun testTimestampIsReasonable() {
        val currentTime = System.currentTimeMillis()
        val event = ItineraryEvent(date = "01/01/2025", time = "10:00", description = "Test")

        assertTrue("Timestamp should be close to current time",
            Math.abs(event.timestamp - currentTime) < 1000)
    }

    @Test
    fun testEventWithEmptyDescription() {
        val event = ItineraryEvent(
            date = "01/01/2025",
            time = "10:00",
            description = ""
        )

        assertEquals("", event.description)
        assertNotNull(event.id)
    }

    @Test
    fun testEventWithLongDescription() {
        val longDesc = "This is a very long description ".repeat(50)
        val event = ItineraryEvent(
            date = "01/01/2025",
            time = "10:00",
            description = longDesc
        )

        assertEquals(longDesc, event.description)
        assertTrue(event.description.length > 100)
    }

    @Test
    fun testEventDateFormats() {
        val event1 = ItineraryEvent(date = "25/12/2025", time = "10:00", description = "Christmas")
        val event2 = ItineraryEvent(date = "01/01/2026", time = "00:00", description = "New Year")

        assertTrue(event1.date.contains("/"))
        assertTrue(event2.date.contains("/"))
        assertEquals(10, event1.date.length)
        assertEquals(10, event2.date.length)
    }

    @Test
    fun testEventTimeFormats() {
        val event1 = ItineraryEvent(date = "01/01/2025", time = "09:30", description = "Morning")
        val event2 = ItineraryEvent(date = "01/01/2025", time = "23:59", description = "Night")

        assertTrue(event1.time.contains(":"))
        assertTrue(event2.time.contains(":"))
        assertEquals(5, event1.time.length)
        assertEquals(5, event2.time.length)
    }
}