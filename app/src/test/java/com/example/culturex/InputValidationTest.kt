package com.example.culturex

import org.junit.Test
import org.junit.Assert.*

class InputValidationTest {

    // Email validation helper (replaces Android Patterns)
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    // List of emails that should pass validation
    @Test
    fun testValidEmails() {
        val validEmails = listOf(
            "user@example.com",
            "test.user@example.co.za",
            "user123@test-domain.com",
            "first.last@company.org"
        )
// Check that each email matches the regex pattern
        validEmails.forEach { email ->
            assertTrue("$email should be valid", isValidEmail(email))
        }
    }

    // List of emails that should fail validation
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
// Verify each invalid email fails the regex
        invalidEmails.forEach { email ->
            assertFalse("$email should be invalid", isValidEmail(email))
        }
    }

    // Sample passwords with varying strength
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

    // Phone number validation
    @Test
    fun testPhoneNumberValidation() {
        val validPhone = "123456789"
        val shortPhone = "12345"
        val longPhone = "12345678901234567890"

// Validation is based on minimum length
        assertTrue("Valid phone should be at least 9 digits", validPhone.length >= 9)
        assertFalse("Short phone should fail", shortPhone.length >= 9)
        assertTrue("Long phone should still be valid", longPhone.length >= 9)
    }

    // Name validation
    @Test
    fun testNameValidation() {
        val validName = "John"
        val shortName = "A"
        val emptyName = ""

        // Validation based on minimum length of 2 characters
        assertTrue("Valid name should be at least 2 characters", validName.length >= 2)
        assertFalse("Short name should fail", shortName.length >= 2)
        assertFalse("Empty name should fail", emptyName.length >= 2)
    }

    // Map of passwords with expected validity (true = acceptable, false = invalid)
    @Test
    fun testPasswordComplexityLevels() {
        val passwords = mapOf(
            "Password123!" to true,  // Strong
            "Pass123" to true,        // Acceptable
            "pass" to false,          // Too weak
            "123456" to false         // No letters
        )

        // Evaluate each password against rules (min length, contains digit, contains letter)
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
        val domain = email.substringAfter("@") // Extract everything after '@'

        assertEquals("example.com", domain) // Domain must contain a '.' (TLD check)
        assertTrue(domain.contains(".")) // Domain should equal "example.com"
    }

    @Test
    fun testPhoneNumberWithCountryCode() {
        val phoneWithCode = "+27123456789"
        val phoneWithoutCode = "123456789"

        // Check prefix
        assertTrue(phoneWithCode.startsWith("+27"))
        assertFalse(phoneWithoutCode.startsWith("+"))

        // Remove country code and check equality
        val strippedPhone = phoneWithCode.removePrefix("+27")
        assertEquals(phoneWithoutCode, strippedPhone)
    }

    @Test
    fun testInputTrimming() {
        val inputWithSpaces = "  user@example.com  "
        val trimmed = inputWithSpaces.trim()

        // Remove leading and trailing spaces
        assertNotEquals(inputWithSpaces, trimmed)
        assertEquals("user@example.com", trimmed)
        assertFalse(trimmed.startsWith(" "))
        assertFalse(trimmed.endsWith(" "))
    } // Ensure spaces were removed correctly

    @Test
    fun testSpecialCharactersInPassword() {
        // Set of accepted special characters
        val passwordWithSpecial = "Pass123!@#"
        val specialChars = "!@#$%^&*()_+-=[]{}|;:',.<>?/~`"

        // Check if password contains at least one special character
        val hasSpecialChar = passwordWithSpecial.any { it in specialChars }
        assertTrue("Password should contain special characters", hasSpecialChar)
    }
}

// Reference List
// Android Developers. (2025a). Test apps on Android. [online] Available at: https://developer.android.com/training/testing.
// Android Developers. (2025b). (Deprecated) Advanced Android in Kotlin 05.1: Testing Basics  |  Android Developers. [online] Available at: https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics?index=..%2F..index#0 [Accessed 25 Aug. 2025].
// Sproviero, F. (2018). Android Unit Testing with Mockito. [online] kodeco.com. Available at: https://www.kodeco.com/195-android-unit-testing-with-mockito [Accessed 25 Aug. 2025].
// Bechtold, S. (2016). JUnit 5 User Guide. [online] Junit.org. Available at: https://docs.junit.org/current/user-guide/.
// henrymbuguakiarie (2025). Call a web API in a sample Android mobile app - Microsoft identity platform. [online] Microsoft.com. Available at: https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-native-authentication-android-call-api [Accessed 25 Aug. 2025].
// Android Developers. (2025). Token  |  Android Developers. [online] Available at: https://developer.android.com/reference/androidx/browser/trusted/Token.