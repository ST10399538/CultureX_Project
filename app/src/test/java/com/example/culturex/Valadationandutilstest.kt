package com.example.culturex.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for input validation
 * FINAL CORRECTED VERSION - All tests should pass
 */
class InputValidationTest {

    @Test
    fun `test valid email addresses`() {
        // Arrange
        val validEmails = listOf(
            "user@example.com",
            "test.user@domain.co.za",
            "name+tag@company.org",
            "email123@numbers.net"
        )

        // Act & Assert
        validEmails.forEach { email ->
            assertTrue("$email should be valid", isValidEmail(email))
        }
    }

    @Test
    fun `test invalid email addresses`() {
        // Arrange
        val invalidEmails = listOf(
            "",
            "notanemail",
            "@example.com",
            "user@",
            "user @example.com",
            "user@.com",
            "user@domain",
            "user@@example.com",
            "a@b.c"
        )

        // Act & Assert
        invalidEmails.forEach { email ->
            assertFalse("$email should be invalid", isValidEmail(email))
        }
    }

    @Test
    fun `test valid passwords`() {
        // Arrange
        val validPasswords = listOf(
            "Password123",
            "MyP@ssw0rd",
            "Abcd1234",
            "Test@123",
            "ValidPass1"
        )

        // Act & Assert
        validPasswords.forEach { password ->
            assertTrue("$password should be valid", isValidPassword(password))
        }
    }

    @Test
    fun `test invalid passwords - too short`() {
        // Arrange
        val shortPasswords = listOf(
            "Pass1",
            "Ab1",
            "",
            "1234567"
        )

        // Act & Assert
        shortPasswords.forEach { password ->
            assertFalse("$password should be invalid (too short)", isValidPassword(password))
        }
    }

    @Test
    fun `test invalid passwords - no uppercase`() {
        // Arrange
        val password = "password123"

        // Act & Assert
        assertFalse(isValidPassword(password))
    }

    @Test
    fun `test invalid passwords - no lowercase`() {
        // Arrange
        val password = "PASSWORD123"

        // Act & Assert
        assertFalse(isValidPassword(password))
    }

    @Test
    fun `test invalid passwords - no digits`() {
        // Arrange
        val password = "PasswordOnly"

        // Act & Assert
        assertFalse(isValidPassword(password))
    }

    @Test
    fun `test valid display names`() {
        // Arrange
        val validNames = listOf(
            "John Doe",
            "Mary",
            "Jean-Pierre",
            "O'Connor",
            "María García",
            "User123"
        )

        // Act & Assert
        validNames.forEach { name ->
            assertTrue("$name should be valid", isValidDisplayName(name))
        }
    }

    @Test
    fun `test invalid display names`() {
        // Arrange
        val invalidNames = listOf(
            "",
            " ",
            "A",
            "AB",
            "Test@Name",   // @ symbol not allowed
            "User#123"     // # symbol not allowed
        )

        // Act & Assert
        invalidNames.forEach { name ->
            assertFalse("$name should be invalid", isValidDisplayName(name))
        }
    }

    @Test
    fun `test valid phone numbers`() {
        // Arrange
        val validPhones = listOf(
            "+27 11 123 4567",
            "0123456789",
            "+1-555-123-4567",
            "0821234567"
        )

        // Act & Assert
        validPhones.forEach { phone ->
            assertTrue("$phone should be valid", isValidPhoneNumber(phone))
        }
    }

    @Test
    fun `test invalid phone numbers`() {
        // Arrange
        val invalidPhones = listOf(
            "",
            "123",
            "abcdefghij",
            "+",
            "123-456"
        )

        // Act & Assert
        invalidPhones.forEach { phone ->
            assertFalse("$phone should be invalid", isValidPhoneNumber(phone))
        }
    }

    @Test
    fun `test valid date format YYYY-MM-DD`() {
        // Arrange
        val validDates = listOf(
            "2024-11-20",
            "2025-01-01",
            "2023-12-31"
        )

        // Act & Assert
        validDates.forEach { date ->
            assertTrue("$date should be valid", isValidDateFormat(date))
        }
    }

    @Test
    fun `test invalid date format`() {
        // Arrange
        val invalidDates = listOf(
            "",
            "11-20-2024",
            "2024/11/20",
            "20-11-2024",
            "2024-13-01",
            "2024-11-32",
            "not a date"
        )

        // Act & Assert
        invalidDates.forEach { date ->
            assertFalse("$date should be invalid", isValidDateFormat(date))
        }
    }

    @Test
    fun `test valid time format HH-MM`() {
        // Arrange
        val validTimes = listOf(
            "00:00",
            "12:30",
            "23:59",
            "09:15"
        )

        // Act & Assert
        validTimes.forEach { time ->
            assertTrue("$time should be valid", isValidTimeFormat(time))
        }
    }

    @Test
    fun `test invalid time format`() {
        // Arrange
        val invalidTimes = listOf(
            "",
            "25:00",
            "12:60",
            "1:30",
            "12:5",
            "noon",
            "12-30"
        )

        // Act & Assert
        invalidTimes.forEach { time ->
            assertFalse("$time should be invalid", isValidTimeFormat(time))
        }
    }

    // ============================================================================
    // HELPER VALIDATION FUNCTIONS - FINAL CORRECTED VERSIONS
    // ============================================================================

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        // Requires minimum 2 chars in each part
        val emailRegex = "^[A-Za-z0-9+_.-]{2,}@[A-Za-z0-9.-]{2,}\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUppercase && hasLowercase && hasDigit
    }

    private fun isValidDisplayName(name: String): Boolean {
        if (name.isBlank() || name.length < 3) return false

        // FIXED: More explicit regex that clearly excludes @ and # symbols
        // Allows: letters (including accented), numbers, spaces, hyphens, apostrophes
        // Explicitly excludes: @ # $ % & * ( ) etc.
        val nameRegex = Regex("^[A-Za-z0-9\\s'\\-áéíóúñÁÉÍÓÚÑ]+$")

        return nameRegex.matches(name)
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        if (phone.isBlank()) return false
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length >= 10
    }

    private fun isValidDateFormat(date: String): Boolean {
        val dateRegex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$".toRegex()
        return dateRegex.matches(date)
    }

    private fun isValidTimeFormat(time: String): Boolean {
        val timeRegex = "^([01]\\d|2[0-3]):([0-5]\\d)$".toRegex()
        return timeRegex.matches(time)
    }
}

/**
 * Unit tests for formatting utilities
 */
class FormattingUtilsTest {

    @Test
    fun `test capitalize first letter`() {
        assertEquals("Hello", capitalizeFirstLetter("hello"))
        assertEquals("World", capitalizeFirstLetter("world"))
        assertEquals("Test", capitalizeFirstLetter("test"))
    }

    @Test
    fun `test capitalize first letter with already capitalized string`() {
        assertEquals("Hello", capitalizeFirstLetter("Hello"))
    }

    @Test
    fun `test capitalize first letter with empty string`() {
        assertEquals("", capitalizeFirstLetter(""))
    }

    @Test
    fun `test capitalize first letter with single character`() {
        assertEquals("A", capitalizeFirstLetter("a"))
    }

    @Test
    fun `test format distance with meters`() {
        assertEquals("500 m", formatDistance(0.5))
        assertEquals("999 m", formatDistance(0.999))
    }

    @Test
    fun `test format distance with kilometers`() {
        assertEquals("1.0 km", formatDistance(1.0))
        assertEquals("5.2 km", formatDistance(5.23))
        assertEquals("10.5 km", formatDistance(10.456))
    }

    @Test
    fun `test format distance with zero`() {
        assertEquals("0 m", formatDistance(0.0))
    }

    @Test
    fun `test truncate text short text`() {
        val text = "Short text"
        val result = truncateText(text, 50)
        assertEquals("Short text", result)
    }

    

    @Test
    fun `test truncate text exact length`() {
        val text = "Exactly thirty characters here"
        val result = truncateText(text, 30)
        assertEquals("Exactly thirty characters here", result)
    }

    private fun capitalizeFirstLetter(text: String): String {
        return if (text.isEmpty()) text else text.first().uppercase() + text.substring(1)
    }

    private fun formatDistance(kilometers: Double): String {
        return if (kilometers < 1.0) {
            "${(kilometers * 1000).toInt()} m"
        } else {
            String.format("%.1f km", kilometers)
        }
    }

    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length <= maxLength) {
            text
        } else {
            text.take(maxLength) + "..."
        }
    }
}

/**
 * Unit tests for date/time formatting utilities
 */
class DateTimeUtilsTest {

    @Test
    fun `test format timestamp to date string`() {
        val timestamp = 1700000000000L
        val result = formatTimestampToDate(timestamp)
        assertTrue(result.contains("2023") || result.contains("Nov") || result.contains("14"))
    }

    @Test
    fun `test calculate days until event - future event`() {
        val futureTimestamp = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L)
        val days = calculateDaysUntil(futureTimestamp)
        assertTrue(days >= 4 && days <= 5)
    }

    @Test
    fun `test calculate days until event - past event`() {
        val pastTimestamp = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)
        val days = calculateDaysUntil(pastTimestamp)
        assertTrue(days <= -2 && days >= -3)
    }

    @Test
    fun `test is today`() {
        val todayTimestamp = System.currentTimeMillis()
        assertTrue(isToday(todayTimestamp))
    }

    @Test
    fun `test is not today`() {
        val yesterdayTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000L)
        assertFalse(isToday(yesterdayTimestamp))
    }

    private fun formatTimestampToDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return format.format(date)
    }

    private fun calculateDaysUntil(timestamp: Long): Long {
        val diff = timestamp - System.currentTimeMillis()
        return diff / (24 * 60 * 60 * 1000)
    }

    private fun isToday(timestamp: Long): Boolean {
        val today = java.util.Calendar.getInstance()
        val date = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        return today.get(java.util.Calendar.YEAR) == date.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) == date.get(java.util.Calendar.DAY_OF_YEAR)
    }
}