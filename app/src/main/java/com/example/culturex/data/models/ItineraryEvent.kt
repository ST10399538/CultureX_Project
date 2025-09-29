package com.example.culturex.data.models

data class ItineraryEvent(
    val id: String = System.currentTimeMillis().toString(),
    val date: String,
    val time: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)