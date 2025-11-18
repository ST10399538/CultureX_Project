package com.example.culturex.data.models

/**
 * Data class representing a greeting in a specific language
 * Used for displaying multilingual greetings in the app
 */
data class GreetingItem(
    val language: String,
    val greeting: String,
    val greetingTranslation: String,
    val goodbye: String,
    val goodbyeTranslation: String,
    val pronunciation: String? = null
)