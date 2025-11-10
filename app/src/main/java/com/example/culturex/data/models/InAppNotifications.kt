package com.example.culturex.data.models

data class InAppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val eventId: String? = null,
    val isRead: Boolean = false,
    val type: String = "event" // "event" or "holiday"
)