package com.example.culturex

import com.example.culturex.data.models.ItineraryEvent
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class ItineraryEventTest {

    // This variable will hold a sample event used in tests
    private lateinit var testEvent: ItineraryEvent

    // Initialize a test event before each test runs
    @Before
    fun setup() {
        testEvent = ItineraryEvent(
            id = "test_123",
            date = "15/12/2025",
            time = "14:30",
            description = "Safari at Kruger National Park"
        )
    }

    // Verify that the event is created with the expected values
    @Test
    fun testItineraryEventCreation() {
        assertEquals("test_123", testEvent.id)
        assertEquals("15/12/2025", testEvent.date)
        assertEquals("14:30", testEvent.time)
        assertEquals("Safari at Kruger National Park", testEvent.description)
        assertTrue(testEvent.timestamp > 0)
    } // Ensure the timestamp is generated (greater than 0)

    // Create an event without providing an ID (should generate automatically)
    @Test
    fun testItineraryEventWithDefaultId() {
        val event = ItineraryEvent(
            date = "20/01/2025",
            time = "09:00",
            description = "Morning tour"
        )
// Verify that a non-empty ID was generated
        assertNotNull(event.id)
        assertFalse(event.id.isEmpty())
        assertTrue(event.id.length > 0)
    }

    // Create two events with a slight delay to ensure unique IDs
    @Test
    fun testMultipleEventsHaveUniqueIds() {
        val event1 = ItineraryEvent(date = "01/01/2025", time = "10:00", description = "Event 1")
        Thread.sleep(10) // Add small delay to ensure unique timestamps
        val event2 = ItineraryEvent(date = "01/01/2025", time = "11:00", description = "Event 2")

        // IDs must be different
        assertNotEquals("Events should have unique IDs", event1.id, event2.id)
    }

    // Capture the current system time
    @Test
    fun testTimestampIsReasonable() {
        val currentTime = System.currentTimeMillis()
        val event = ItineraryEvent(date = "01/01/2025", time = "10:00", description = "Test")

        // Ensure timestamp is within 1 second of the current system time
        assertTrue("Timestamp should be close to current time",
            Math.abs(event.timestamp - currentTime) < 1000)
    }

    // Create event with an empty description
    @Test
    fun testEventWithEmptyDescription() {
        val event = ItineraryEvent(
            date = "01/01/2025",
            time = "10:00",
            description = ""
        )

        // Verify description is empty but ID is still valid
        assertEquals("", event.description)
        assertNotNull(event.id)
    }

    // Generate a long description by repeating text
    @Test
    fun testEventWithLongDescription() {
        val longDesc = "This is a very long description ".repeat(50)
        val event = ItineraryEvent(
            date = "01/01/2025",
            time = "10:00",
            description = longDesc
        )

        // Ensure the description is stored correctly and is long
        assertEquals(longDesc, event.description)
        assertTrue(event.description.length > 100)
    }

    // Create two events with valid date formats
    @Test
    fun testEventDateFormats() {
        val event1 = ItineraryEvent(date = "25/12/2025", time = "10:00", description = "Christmas")
        val event2 = ItineraryEvent(date = "01/01/2026", time = "00:00", description = "New Year")

        // Validate date format consistency
        assertTrue(event1.date.contains("/"))
        assertTrue(event2.date.contains("/"))
        assertEquals(10, event1.date.length)
        assertEquals(10, event2.date.length)
    }

    // Create two events with valid time formats
    @Test
    fun testEventTimeFormats() {
        val event1 = ItineraryEvent(date = "01/01/2025", time = "09:30", description = "Morning")
        val event2 = ItineraryEvent(date = "01/01/2025", time = "23:59", description = "Night")

        // Validate time format consistency
        assertTrue(event1.time.contains(":"))
        assertTrue(event2.time.contains(":"))
        assertEquals(5, event1.time.length)
        assertEquals(5, event2.time.length)
    }
}

// Reference List
// Android Developers. (2025a). Test apps on Android. [online] Available at: https://developer.android.com/training/testing.
// Android Developers. (2025b). (Deprecated) Advanced Android in Kotlin 05.1: Testing Basics  |  Android Developers. [online] Available at: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics?index=..%2F..index#0 [Accessed 25 Aug. 2025].
// Sproviero, F. (2018). Android Unit Testing with Mockito. [online] kodeco.com. Available at: https://www.kodeco.com/195-android-unit-testing-with-mockito [Accessed 25 Aug. 2025].
// Bechtold, S. (2016). JUnit 5 User Guide. [online] Junit.org. Available at: https://docs.junit.org/current/user-guide/.
// henrymbuguakiarie (2025). Call a web API in a sample Android mobile app - Microsoft identity platform. [online] Microsoft.com. Available at: https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-native-authentication-android-call-api [Accessed 25 Aug. 2025].
// Android Developers. (2025). Token  |  Android Developers. [online] Available at: https://developer.android.com/reference/androidx/browser/trusted/Token.
