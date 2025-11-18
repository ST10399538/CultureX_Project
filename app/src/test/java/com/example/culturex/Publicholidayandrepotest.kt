package com.example.culturex.api

import com.example.culturex.repository.PublicHolidayRepository
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PublicHoliday data class
 */
class PublicHolidayTest {

    @Test
    fun `test PublicHoliday creation with all fields`() {
        // Arrange & Act
        val holiday = PublicHoliday(
            date = "2024-12-25",
            localName = "Christmas Day",
            name = "Christmas Day",
            countryCode = "ZA",
            fixed = true,
            global = true,
            counties = null,
            launchYear = 1994,
            types = listOf("Public")
        )

        // Assert
        assertEquals("2024-12-25", holiday.date)
        assertEquals("Christmas Day", holiday.localName)
        assertEquals("Christmas Day", holiday.name)
        assertEquals("ZA", holiday.countryCode)
        assertTrue(holiday.fixed)
        assertTrue(holiday.global)
        assertNull(holiday.counties)
        assertEquals(1994, holiday.launchYear)
        assertEquals(1, holiday.types.size)
    }

    @Test
    fun `test PublicHoliday with non-global flag`() {
        // Arrange & Act
        val holiday = PublicHoliday(
            date = "2024-05-01",
            localName = "Workers' Day",
            name = "Labour Day",
            countryCode = "ZA",
            fixed = true,
            global = false,
            counties = listOf("Western Cape", "Gauteng"),
            launchYear = null,
            types = listOf("Public", "Bank")
        )

        // Assert
        assertFalse(holiday.global)
        assertNotNull(holiday.counties)
        assertEquals(2, holiday.counties?.size)
        assertEquals(2, holiday.types.size)
    }

    @Test
    fun `test PublicHoliday with non-fixed date`() {
        // Arrange & Act
        val holiday = PublicHoliday(
            date = "2024-04-21",
            localName = "Easter Sunday",
            name = "Easter Sunday",
            countryCode = "US",
            fixed = false,
            global = true,
            counties = null,
            launchYear = null,
            types = listOf("Public", "Religious")
        )

        // Assert
        assertFalse(holiday.fixed)
        assertTrue(holiday.types.contains("Religious"))
    }

    @Test
    fun `test PublicHoliday date format is ISO 8601`() {
        // Arrange & Act
        val holiday = PublicHoliday(
            date = "2024-01-01",
            localName = "New Year's Day",
            name = "New Year's Day",
            countryCode = "ZA",
            fixed = true,
            global = true,
            counties = null,
            launchYear = null,
            types = listOf("Public")
        )

        // Assert
        val dateRegex = "\\d{4}-\\d{2}-\\d{2}".toRegex()
        assertTrue(dateRegex.matches(holiday.date))
    }

    @Test
    fun `test PublicHoliday with multiple types`() {
        // Arrange & Act
        val holiday = PublicHoliday(
            date = "2024-12-26",
            localName = "Day of Goodwill",
            name = "Boxing Day",
            countryCode = "ZA",
            fixed = true,
            global = true,
            counties = null,
            launchYear = null,
            types = listOf("Public", "Bank", "School", "Authorities")
        )

        // Assert
        assertEquals(4, holiday.types.size)
        assertTrue(holiday.types.contains("Public"))
        assertTrue(holiday.types.contains("Bank"))
        assertTrue(holiday.types.contains("School"))
    }

    @Test
    fun `test PublicHoliday equality`() {
        // Arrange
        val holiday1 = PublicHoliday(
            date = "2024-07-04",
            localName = "Independence Day",
            name = "Independence Day",
            countryCode = "US",
            fixed = true,
            global = true,
            counties = null,
            launchYear = 1776,
            types = listOf("Public")
        )

        val holiday2 = PublicHoliday(
            date = "2024-07-04",
            localName = "Independence Day",
            name = "Independence Day",
            countryCode = "US",
            fixed = true,
            global = true,
            counties = null,
            launchYear = 1776,
            types = listOf("Public")
        )

        // Assert
        assertEquals(holiday1, holiday2)
    }
}

/**
 * Unit tests for PublicHolidayRepository
 */
class PublicHolidayRepositoryTest {

    @Test
    fun `test country code mapping for South Africa`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("South Africa")

        // Assert
        assertEquals("ZA", code)
    }

    @Test
    fun `test country code mapping for United States`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("United States")

        // Assert
        assertEquals("US", code)
    }

    @Test
    fun `test country code mapping for United Kingdom`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("United Kingdom")

        // Assert
        assertEquals("GB", code)
    }

    @Test
    fun `test country code mapping for Germany`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("Germany")

        // Assert
        assertEquals("DE", code)
    }

    @Test
    fun `test country code mapping for Japan`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("Japan")

        // Assert
        assertEquals("JP", code)
    }

    @Test
    fun `test country code mapping for India`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("India")

        // Assert
        assertEquals("IN", code)
    }

    @Test
    fun `test country code mapping defaults to ZA for unknown country`() {
        // Act
        val code = PublicHolidayRepository.getCountryCode("Unknown Country")

        // Assert
        assertEquals("ZA", code)
    }

    @Test
    fun `test country code mapping is case sensitive`() {
        // Act
        val code1 = PublicHolidayRepository.getCountryCode("south africa")
        val code2 = PublicHolidayRepository.getCountryCode("SOUTH AFRICA")

        // Assert
        // Both should default to ZA since exact match not found
        assertEquals("ZA", code1)
        assertEquals("ZA", code2)
    }

    @Test
    fun `test all country codes are two letters`() {
        // Arrange
        val countryCodes = PublicHolidayRepository.COUNTRY_CODES.values

        // Assert
        countryCodes.forEach { code ->
            assertEquals(2, code.length)
            assertTrue(code.all { it.isUpperCase() })
        }
    }

    @Test
    fun `test country codes mapping has expected entries`() {
        // Assert
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("South Africa"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("United States"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("Canada"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("Australia"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("France"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("Italy"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("Spain"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("Brazil"))
        assertTrue(PublicHolidayRepository.COUNTRY_CODES.containsKey("China"))
    }

    @Test
    fun `test country codes are valid ISO 3166-1 alpha-2 codes`() {
        // Arrange
        val validCodes = setOf(
            "ZA", "US", "GB", "CA", "AU", "DE", "FR", "JP",
            "CN", "IN", "BR", "MX", "IT", "ES", "NL"
        )

        // Act
        val actualCodes = PublicHolidayRepository.COUNTRY_CODES.values.toSet()

        // Assert
        assertEquals(validCodes, actualCodes)
    }
}

/**
 * Unit tests for list filtering and sorting operations
 */
class ListOperationsTest {

    @Test
    fun `test filter holidays by year`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2025-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public"))
        )

        // Act
        val filtered2024 = holidays.filter { it.date.startsWith("2024") }

        // Assert
        assertEquals(2, filtered2024.size)
    }

    @Test
    fun `test sort holidays by date`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-07-04", "Independence", "Independence", "US", true, true, null, null, listOf("Public"))
        )

        // Act
        val sorted = holidays.sortedBy { it.date }

        // Assert
        assertEquals("2024-01-01", sorted[0].date)
        assertEquals("2024-07-04", sorted[1].date)
        assertEquals("2024-12-25", sorted[2].date)
    }

    @Test
    fun `test filter global holidays only`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-03-21", "Human Rights", "Human Rights", "ZA", true, false, listOf("WC"), null, listOf("Public")),
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public"))
        )

        // Act
        val globalOnly = holidays.filter { it.global }

        // Assert
        assertEquals(2, globalOnly.size)
        assertTrue(globalOnly.all { it.global })
    }

    @Test
    fun `test filter holidays by country code`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-07-04", "Independence", "Independence", "US", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public"))
        )

        // Act
        val zaHolidays = holidays.filter { it.countryCode == "ZA" }

        // Assert
        assertEquals(2, zaHolidays.size)
        assertTrue(zaHolidays.all { it.countryCode == "ZA" })
    }

    @Test
    fun `test group holidays by month`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-01-15", "MLK Day", "MLK Day", "US", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public"))
        )

        // Act
        val groupedByMonth = holidays.groupBy { it.date.substring(0, 7) } // YYYY-MM

        // Assert
        assertEquals(2, groupedByMonth.size)
        assertEquals(2, groupedByMonth["2024-01"]?.size)
        assertEquals(1, groupedByMonth["2024-12"]?.size)
    }

    @Test
    fun `test count holidays by type`() {
        // Arrange
        val holidays = listOf(
            PublicHoliday("2024-01-01", "New Year", "New Year", "ZA", true, true, null, null, listOf("Public")),
            PublicHoliday("2024-04-21", "Easter", "Easter", "ZA", false, true, null, null, listOf("Public", "Religious")),
            PublicHoliday("2024-12-25", "Christmas", "Christmas", "ZA", true, true, null, null, listOf("Public", "Religious", "Bank"))
        )

        // Act
        val allTypes = holidays.flatMap { it.types }
        val typeCount = allTypes.groupingBy { it }.eachCount()

        // Assert
        assertEquals(3, typeCount["Public"])
        assertEquals(2, typeCount["Religious"])
        assertEquals(1, typeCount["Bank"])
    }
}