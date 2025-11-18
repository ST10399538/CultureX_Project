package com.example.culturex.data.models

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ItineraryEvent data class
 */
class ItineraryEventTest {

    @Test
    fun `test ItineraryEvent creation with all fields`() {
        // Arrange & Act
        val event = ItineraryEvent(
            id = "event_123",
            date = "2024-12-25",
            time = "14:30",
            description = "Visit Museum",
            timestamp = 1703512800000L
        )

        // Assert
        assertEquals("event_123", event.id)
        assertEquals("2024-12-25", event.date)
        assertEquals("14:30", event.time)
        assertEquals("Visit Museum", event.description)
        assertEquals(1703512800000L, event.timestamp)
    }

    @Test
    fun `test ItineraryEvent with default id generated from timestamp`() {
        // Arrange
        val currentTime = System.currentTimeMillis()

        // Act
        val event = ItineraryEvent(
            date = "2024-11-20",
            time = "10:00",
            description = "Morning meeting"
        )

        // Assert
        assertNotNull(event.id)
        assertTrue(event.id.isNotEmpty())
        // ID should be close to current timestamp
        val idAsLong = event.id.toLongOrNull()
        assertNotNull(idAsLong)
        assertTrue(idAsLong!! <= currentTime + 1000) // Allow 1 second tolerance
    }

    @Test
    fun `test ItineraryEvent timestamp default is current time`() {
        // Arrange
        val beforeTime = System.currentTimeMillis()

        // Act
        val event = ItineraryEvent(
            date = "2024-11-20",
            time = "15:00",
            description = "Afternoon tour"
        )

        val afterTime = System.currentTimeMillis()

        // Assert
        assertTrue(event.timestamp >= beforeTime)
        assertTrue(event.timestamp <= afterTime)
    }

    @Test
    fun `test ItineraryEvent with empty description`() {
        // Arrange & Act
        val event = ItineraryEvent(
            date = "2024-11-21",
            time = "09:00",
            description = ""
        )

        // Assert
        assertTrue(event.description.isEmpty())
        assertNotNull(event.date)
        assertNotNull(event.time)
    }

    @Test
    fun `test ItineraryEvent sorting by timestamp`() {
        // Arrange
        val events = listOf(
            ItineraryEvent("1", "2024-11-20", "10:00", "Event 1", 1000000L),
            ItineraryEvent("2", "2024-11-20", "14:00", "Event 2", 1003000L),
            ItineraryEvent("3", "2024-11-20", "12:00", "Event 3", 1001000L)
        )

        // Act
        val sortedEvents = events.sortedBy { it.timestamp }

        // Assert
        assertEquals("Event 1", sortedEvents[0].description)
        assertEquals("Event 3", sortedEvents[1].description)
        assertEquals("Event 2", sortedEvents[2].description)
    }

    @Test
    fun `test ItineraryEvent equality`() {
        // Arrange
        val event1 = ItineraryEvent(
            id = "same_id",
            date = "2024-11-20",
            time = "10:00",
            description = "Same event",
            timestamp = 1000000L
        )

        val event2 = ItineraryEvent(
            id = "same_id",
            date = "2024-11-20",
            time = "10:00",
            description = "Same event",
            timestamp = 1000000L
        )

        // Assert
        assertEquals(event1, event2)
    }

    @Test
    fun `test ItineraryEvent with special characters in description`() {
        // Arrange & Act
        val event = ItineraryEvent(
            date = "2024-11-20",
            time = "16:00",
            description = "Meeting @ Café with João & María - discuss project #1!"
        )

        // Assert
        assertTrue(event.description.contains("@"))
        assertTrue(event.description.contains("&"))
        assertTrue(event.description.contains("#"))
    }
}

/**
 * Unit tests for InAppNotification data class
 */
class InAppNotificationTest {

    @Test
    fun `test InAppNotification creation with default values`() {
        // Arrange & Act
        val notification = InAppNotification()

        // Assert
        assertEquals("", notification.id)
        assertEquals("", notification.title)
        assertEquals("", notification.message)
        assertNull(notification.eventId)
        assertFalse(notification.isRead)
        assertEquals("event", notification.type)
        assertTrue(notification.timestamp > 0)
    }

    @Test
    fun `test InAppNotification with all fields specified`() {
        // Arrange & Act
        val notification = InAppNotification(
            id = "notif_123",
            title = "Upcoming Event",
            message = "Your meeting starts in 1 hour",
            timestamp = 1700000000000L,
            eventId = "event_456",
            isRead = true,
            type = "reminder"
        )

        // Assert
        assertEquals("notif_123", notification.id)
        assertEquals("Upcoming Event", notification.title)
        assertEquals("Your meeting starts in 1 hour", notification.message)
        assertEquals(1700000000000L, notification.timestamp)
        assertEquals("event_456", notification.eventId)
        assertTrue(notification.isRead)
        assertEquals("reminder", notification.type)
    }

    @Test
    fun `test InAppNotification with holiday type`() {
        // Arrange & Act
        val notification = InAppNotification(
            id = "holiday_1",
            title = "Public Holiday",
            message = "Tomorrow is Christmas Day",
            type = "holiday"
        )

        // Assert
        assertEquals("holiday", notification.type)
        assertEquals("Public Holiday", notification.title)
    }

    @Test
    fun `test InAppNotification mark as read`() {
        // Arrange
        val notification = InAppNotification(
            id = "notif_789",
            title = "New Message",
            message = "You have a new message",
            isRead = false
        )

        // Act
        val updatedNotification = notification.copy(isRead = true)

        // Assert
        assertFalse(notification.isRead)
        assertTrue(updatedNotification.isRead)
    }

    @Test
    fun `test InAppNotification with null eventId`() {
        // Arrange & Act
        val notification = InAppNotification(
            id = "notif_999",
            title = "General Notification",
            message = "System update available",
            eventId = null
        )

        // Assert
        assertNull(notification.eventId)
    }

    @Test
    fun `test InAppNotification timestamp is recent`() {
        // Arrange
        val beforeTime = System.currentTimeMillis()

        // Act
        val notification = InAppNotification(
            title = "Test",
            message = "Test message"
        )

        val afterTime = System.currentTimeMillis()

        // Assert
        assertTrue(notification.timestamp >= beforeTime)
        assertTrue(notification.timestamp <= afterTime)
    }

    @Test
    fun `test InAppNotification sorting by timestamp`() {
        // Arrange
        val notifications = listOf(
            InAppNotification(id = "1", title = "Old", message = "Old notification", timestamp = 1000000L),
            InAppNotification(id = "2", title = "Recent", message = "Recent notification", timestamp = 3000000L),
            InAppNotification(id = "3", title = "Medium", message = "Medium notification", timestamp = 2000000L)
        )

        // Act
        val sortedNotifications = notifications.sortedByDescending { it.timestamp }

        // Assert
        assertEquals("Recent", sortedNotifications[0].title)
        assertEquals("Medium", sortedNotifications[1].title)
        assertEquals("Old", sortedNotifications[2].title)
    }
}

/**
 * Unit tests for CountryInfo data class
 */
class CountryInfoTest {

    @Test
    fun `test CountryInfo creation with all fields`() {
        // Arrange & Act
        val countryInfo = CountryInfo(
            geography = "Diverse landscapes including mountains and beaches",
            location = "Southern tip of Africa",
            history = "Rich and complex history",
            culture = "Diverse cultural heritage",
            demographics = "Population of 60 million",
            capital = "Pretoria (executive)",
            population = "60 million",
            language = "11 official languages",
            currency = "South African Rand (ZAR)"
        )

        // Assert
        assertEquals("Diverse landscapes including mountains and beaches", countryInfo.geography)
        assertEquals("Southern tip of Africa", countryInfo.location)
        assertEquals("Rich and complex history", countryInfo.history)
        assertEquals("Diverse cultural heritage", countryInfo.culture)
        assertEquals("Population of 60 million", countryInfo.demographics)
        assertEquals("Pretoria (executive)", countryInfo.capital)
        assertEquals("60 million", countryInfo.population)
        assertEquals("11 official languages", countryInfo.language)
        assertEquals("South African Rand (ZAR)", countryInfo.currency)
    }

    @Test
    fun `test CountryInfo with all null fields`() {
        // Arrange & Act
        val countryInfo = CountryInfo()

        // Assert
        assertNull(countryInfo.geography)
        assertNull(countryInfo.location)
        assertNull(countryInfo.history)
        assertNull(countryInfo.culture)
        assertNull(countryInfo.demographics)
        assertNull(countryInfo.capital)
        assertNull(countryInfo.population)
        assertNull(countryInfo.language)
        assertNull(countryInfo.currency)
    }

    @Test
    fun `test CountryInfo with partial data`() {
        // Arrange & Act
        val countryInfo = CountryInfo(
            capital = "Paris",
            population = "67 million",
            language = "French",
            currency = "Euro (EUR)"
        )

        // Assert
        assertEquals("Paris", countryInfo.capital)
        assertEquals("67 million", countryInfo.population)
        assertEquals("French", countryInfo.language)
        assertEquals("Euro (EUR)", countryInfo.currency)
        assertNull(countryInfo.geography)
        assertNull(countryInfo.history)
    }

    @Test
    fun `test CountryInfo with very long text fields`() {
        // Arrange
        val longText = "This is a very detailed description. ".repeat(100)

        // Act
        val countryInfo = CountryInfo(
            geography = longText,
            history = longText,
            culture = longText
        )

        // Assert
        assertTrue(countryInfo.geography!!.length > 3000)
        assertTrue(countryInfo.history!!.length > 3000)
        assertTrue(countryInfo.culture!!.length > 3000)
    }

    @Test
    fun `test CountryInfo copy with modifications`() {
        // Arrange
        val original = CountryInfo(
            capital = "London",
            population = "67 million",
            currency = "Pound Sterling"
        )

        // Act
        val modified = original.copy(population = "68 million")

        // Assert
        assertEquals("London", modified.capital)
        assertEquals("68 million", modified.population)
        assertEquals("Pound Sterling", modified.currency)
        assertEquals("67 million", original.population) // Original unchanged
    }
}