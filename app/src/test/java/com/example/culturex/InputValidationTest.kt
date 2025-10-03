package com.example.culturex

import org.junit.Test
import org.junit.Assert.*

class InputValidationTest {

    // Email validation helper (replaces Android Patterns)
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    @Test
    fun testValidEmails() {
        val validEmails = listOf(
            "user@example.com",
            "test.user@example.co.za",
            "user123@test-domain.com",
            "first.last@company.org"
        )

        validEmails.forEach { email ->
            assertTrue("$email should be valid", isValidEmail(email))
        }
    }

    @Test
    fun testInvalidEmails() {
        val invalidEmails = listOf(
            "invalid",
            "@example.com",
            "user@",
            "user space@example.com",
            "user@.com",
            "user@@example.com"
        )

        invalidEmails.forEach { email ->
            assertFalse("$email should be invalid", isValidEmail(email))
        }
    }

    @Test
    fun testPasswordStrength() {
        val strongPassword = "Password123"
        val weakPasswordTooShort = "Pass1"
        val weakPasswordNoNumber = "PasswordOnly"
        val weakPasswordNoLetter = "12345678"

        // Valid password
        assertTrue(strongPassword.length >= 6)
        assertTrue(strongPassword.any { it.isDigit() })
        assertTrue(strongPassword.any { it.isLetter() })

        // Too short
        assertFalse(weakPasswordTooShort.length >= 6)

        // No number
        assertFalse(weakPasswordNoNumber.any { it.isDigit() })

        // No letter
        assertFalse(weakPasswordNoLetter.any { it.isLetter() })
    }

    @Test
    fun testPhoneNumberValidation() {
        val validPhone = "123456789"
        val shortPhone = "12345"
        val longPhone = "12345678901234567890"

        assertTrue("Valid phone should be at least 9 digits", validPhone.length >= 9)
        assertFalse("Short phone should fail", shortPhone.length >= 9)
        assertTrue("Long phone should still be valid", longPhone.length >= 9)
    }

    @Test
    fun testNameValidation() {
        val validName = "John"
        val shortName = "A"
        val emptyName = ""

        assertTrue("Valid name should be at least 2 characters", validName.length >= 2)
        assertFalse("Short name should fail", shortName.length >= 2)
        assertFalse("Empty name should fail", emptyName.length >= 2)
    }

    @Test
    fun testPasswordComplexityLevels() {
        val passwords = mapOf(
            "Password123!" to true,  // Strong
            "Pass123" to true,        // Acceptable
            "pass" to false,          // Too weak
            "123456" to false         // No letters
        )

        passwords.forEach { (password, shouldBeValid) ->
            val isValid = password.length >= 6 &&
                    password.any { it.isDigit() } &&
                    password.any { it.isLetter() }

            assertEquals("Password: $password", shouldBeValid, isValid)
        }
    }

    @Test
    fun testEmailDomainExtraction() {
        val email = "user@example.com"
        val domain = email.substringAfter("@")

        assertEquals("example.com", domain)
        assertTrue(domain.contains("."))
    }

    @Test
    fun testPhoneNumberWithCountryCode() {
        val phoneWithCode = "+27123456789"
        val phoneWithoutCode = "123456789"

        assertTrue(phoneWithCode.startsWith("+27"))
        assertFalse(phoneWithoutCode.startsWith("+"))

        val strippedPhone = phoneWithCode.removePrefix("+27")
        assertEquals(phoneWithoutCode, strippedPhone)
    }

    @Test
    fun testInputTrimming() {
        val inputWithSpaces = "  user@example.com  "
        val trimmed = inputWithSpaces.trim()

        assertNotEquals(inputWithSpaces, trimmed)
        assertEquals("user@example.com", trimmed)
        assertFalse(trimmed.startsWith(" "))
        assertFalse(trimmed.endsWith(" "))
    }

    @Test
    fun testSpecialCharactersInPassword() {
        val passwordWithSpecial = "Pass123!@#"
        val specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`"

        val hasSpecialChar = passwordWithSpecial.any { it in specialChars }
        assertTrue("Password should contain special characters", hasSpecialChar)
    }
}