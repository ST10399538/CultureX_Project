package com.example.culturex.data.entities

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for CachedContent entity
 */
class CachedContentTest {

    @Test
    fun `test CachedContent creation with minimal required fields`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "za_dress_code",
            countryId = "za",
            categoryId = "dress_code",
            countryName = "South Africa",
            categoryName = "Dress Code",
            title = "South African Dress Code",
            content = "Dress code information...",
            dos = null,
            donts = null,
            examples = null,
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null
        )

        // Assert
        assertEquals("za_dress_code", cachedContent.id)
        assertEquals("za", cachedContent.countryId)
        assertEquals("dress_code", cachedContent.categoryId)
        assertEquals("South Africa", cachedContent.countryName)
        assertEquals("Dress Code", cachedContent.categoryName)
        assertFalse(cachedContent.isSynced)
        assertFalse(cachedContent.isBookmarked)
        assertFalse(cachedContent.isSavedForOffline)
    }

    @Test
    fun `test CachedContent with lists populated`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "us_etiquette",
            countryId = "us",
            categoryId = "etiquette",
            countryName = "United States",
            categoryName = "Etiquette",
            title = "American Etiquette",
            content = "Etiquette guidelines...",
            dos = listOf("Be punctual", "Tip service workers", "Respect personal space"),
            donts = listOf("Don't discuss salary", "Don't be late", "Avoid political debates"),
            examples = listOf("Handshake greeting", "Thank you notes", "Table manners"),
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null
        )

        // Assert
        assertEquals(3, cachedContent.dos?.size)
        assertEquals(3, cachedContent.donts?.size)
        assertEquals(3, cachedContent.examples?.size)
        assertTrue(cachedContent.dos?.contains("Be punctual") ?: false)
    }

    @Test
    fun `test CachedContent with etiquette specific fields`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "uk_etiquette",
            countryId = "uk",
            categoryId = "etiquette",
            countryName = "United Kingdom",
            categoryName = "Etiquette",
            title = "British Etiquette",
            content = "General etiquette info",
            dos = null,
            donts = null,
            examples = null,
            formalDescription = "Formal British etiquette rules",
            formalKeyPoints = "• Always say please and thank you\n• Queue properly",
            businessDescription = "Business etiquette in the UK",
            businessKeyPoints = "• Be punctual\n• Formal dress code",
            socialDescription = "Social customs in Britain",
            socialKeyPoints = "• Respect privacy\n• Mind your manners"
        )

        // Assert
        assertNotNull(cachedContent.formalDescription)
        assertNotNull(cachedContent.formalKeyPoints)
        assertNotNull(cachedContent.businessDescription)
        assertNotNull(cachedContent.businessKeyPoints)
        assertNotNull(cachedContent.socialDescription)
        assertNotNull(cachedContent.socialKeyPoints)
    }

    @Test
    fun `test CachedContent lastUpdated default is current time`() {
        // Arrange
        val beforeTime = System.currentTimeMillis()

        // Act
        val cachedContent = CachedContent(
            id = "test_id",
            countryId = "test",
            categoryId = "test",
            countryName = "Test",
            categoryName = "Test",
            title = "Test",
            content = "Test",
            dos = null,
            donts = null,
            examples = null,
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null
        )

        val afterTime = System.currentTimeMillis()

        // Assert
        assertTrue(cachedContent.lastUpdated >= beforeTime)
        assertTrue(cachedContent.lastUpdated <= afterTime)
    }

    @Test
    fun `test CachedContent with bookmarked and saved offline flags`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "favorite_content",
            countryId = "fr",
            categoryId = "culture",
            countryName = "France",
            categoryName = "Culture",
            title = "French Culture",
            content = "Cultural information",
            dos = null,
            donts = null,
            examples = null,
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null,
            isBookmarked = true,
            isSavedForOffline = true,
            isSynced = true
        )

        // Assert
        assertTrue(cachedContent.isBookmarked)
        assertTrue(cachedContent.isSavedForOffline)
        assertTrue(cachedContent.isSynced)
    }

    @Test
    fun `test CachedContent id format`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "de_business_etiquette",
            countryId = "de",
            categoryId = "business_etiquette",
            countryName = "Germany",
            categoryName = "Business Etiquette",
            title = "German Business Etiquette",
            content = "Content",
            dos = null,
            donts = null,
            examples = null,
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null
        )

        // Assert
        assertTrue(cachedContent.id.contains("_"))
        assertTrue(cachedContent.id.startsWith("de_"))
    }

    @Test
    fun `test CachedContent with empty lists`() {
        // Arrange & Act
        val cachedContent = CachedContent(
            id = "empty_lists",
            countryId = "test",
            categoryId = "test",
            countryName = "Test",
            categoryName = "Test",
            title = "Test",
            content = "Content",
            dos = emptyList(),
            donts = emptyList(),
            examples = emptyList(),
            formalDescription = null,
            formalKeyPoints = null,
            businessDescription = null,
            businessKeyPoints = null,
            socialDescription = null,
            socialKeyPoints = null
        )

        // Assert
        assertTrue(cachedContent.dos?.isEmpty() ?: false)
        assertTrue(cachedContent.donts?.isEmpty() ?: false)
        assertTrue(cachedContent.examples?.isEmpty() ?: false)
    }
}

/**
 * Unit tests for PendingSyncOperation entity
 */
class PendingSyncOperationTest {

    @Test
    fun `test PendingSyncOperation creation for bookmark add`() {
        // Arrange & Act
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.BOOKMARK_ADD,
            contentId = "za_dress_code",
            bookmarked = true
        )

        // Assert
        assertEquals(0L, operation.id) // Auto-generated ID starts at 0
        assertEquals(SyncOperationType.BOOKMARK_ADD, operation.operationType)
        assertEquals("za_dress_code", operation.contentId)
        assertTrue(operation.bookmarked ?: false)
        assertNull(operation.savedOffline)
        assertEquals(0, operation.retryCount)
        assertNull(operation.lastAttempt)
        assertNull(operation.errorMessage)
    }

    @Test
    fun `test PendingSyncOperation creation for bookmark remove`() {
        // Arrange & Act
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.BOOKMARK_REMOVE,
            contentId = "us_etiquette",
            bookmarked = false
        )

        // Assert
        assertEquals(SyncOperationType.BOOKMARK_REMOVE, operation.operationType)
        assertFalse(operation.bookmarked ?: true)
    }

    @Test
    fun `test PendingSyncOperation creation for save offline`() {
        // Arrange & Act
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.SAVE_OFFLINE_ADD,
            contentId = "uk_culture",
            savedOffline = true
        )

        // Assert
        assertEquals(SyncOperationType.SAVE_OFFLINE_ADD, operation.operationType)
        assertTrue(operation.savedOffline ?: false)
        assertNull(operation.bookmarked)
    }

    @Test
    fun `test PendingSyncOperation creation for content update`() {
        // Arrange & Act
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.CONTENT_UPDATE,
            contentId = "fr_communication"
        )

        // Assert
        assertEquals(SyncOperationType.CONTENT_UPDATE, operation.operationType)
        assertNull(operation.bookmarked)
        assertNull(operation.savedOffline)
    }

    @Test
    fun `test PendingSyncOperation timestamp is current time`() {
        // Arrange
        val beforeTime = System.currentTimeMillis()

        // Act
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.BOOKMARK_ADD,
            contentId = "test_content"
        )

        val afterTime = System.currentTimeMillis()

        // Assert
        assertTrue(operation.timestamp >= beforeTime)
        assertTrue(operation.timestamp <= afterTime)
    }

    @Test
    fun `test PendingSyncOperation with retry information`() {
        // Arrange & Act
        val operation = PendingSyncOperation(
            id = 123L,
            operationType = SyncOperationType.BOOKMARK_ADD,
            contentId = "test_content",
            retryCount = 3,
            lastAttempt = 1700000000000L,
            errorMessage = "Network timeout"
        )

        // Assert
        assertEquals(123L, operation.id)
        assertEquals(3, operation.retryCount)
        assertEquals(1700000000000L, operation.lastAttempt)
        assertEquals("Network timeout", operation.errorMessage)
    }

    @Test
    fun `test PendingSyncOperation retry count increments`() {
        // Arrange
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.SAVE_OFFLINE_ADD,
            contentId = "test_id",
            retryCount = 0
        )

        // Act
        val retriedOperation = operation.copy(retryCount = operation.retryCount + 1)

        // Assert
        assertEquals(0, operation.retryCount)
        assertEquals(1, retriedOperation.retryCount)
    }

    @Test
    fun `test SyncOperationType enum values`() {
        // Assert
        assertEquals(5, SyncOperationType.values().size)
        assertTrue(SyncOperationType.values().contains(SyncOperationType.BOOKMARK_ADD))
        assertTrue(SyncOperationType.values().contains(SyncOperationType.BOOKMARK_REMOVE))
        assertTrue(SyncOperationType.values().contains(SyncOperationType.SAVE_OFFLINE_ADD))
        assertTrue(SyncOperationType.values().contains(SyncOperationType.SAVE_OFFLINE_REMOVE))
        assertTrue(SyncOperationType.values().contains(SyncOperationType.CONTENT_UPDATE))
    }

    @Test
    fun `test PendingSyncOperation with custom timestamp`() {
        // Arrange & Act
        val customTimestamp = 1600000000000L
        val operation = PendingSyncOperation(
            operationType = SyncOperationType.CONTENT_UPDATE,
            contentId = "custom_content",
            timestamp = customTimestamp
        )

        // Assert
        assertEquals(customTimestamp, operation.timestamp)
    }

    @Test
    fun `test PendingSyncOperation equality`() {
        // Arrange
        val operation1 = PendingSyncOperation(
            id = 1L,
            operationType = SyncOperationType.BOOKMARK_ADD,
            contentId = "same_content",
            timestamp = 1000000L
        )

        val operation2 = PendingSyncOperation(
            id = 1L,
            operationType = SyncOperationType.BOOKMARK_ADD,
            contentId = "same_content",
            timestamp = 1000000L
        )

        // Assert
        assertEquals(operation1, operation2)
    }
}