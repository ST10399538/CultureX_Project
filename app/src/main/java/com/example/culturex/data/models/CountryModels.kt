package com.example.culturex.data.models

import com.google.gson.annotations.SerializedName

class CountryModels {

    data class CountryDTO(
        val id: String,
        val name: String?,
        val countryCode: String?,
        val flagImageUrl: String?,
        val description: String?,
        val timezone: String?,
        val currency: String?,
        val emergencyContacts: Any? = null
    )

    data class CulturalCategoryDTO(
        val id: String,
        val name: String?,
        val description: String?,
        val iconUrl: String?,
        val sortOrder: Int = 0
    )

    data class CulturalContentDTO(
        val id: String,
        val countryId: String,
        val categoryId: String,
        val title: String?,
        val content: String?,
        val dos: List<String>?,
        val donts: List<String>?,
        val examples: List<String>?,
        val countryName: String?,
        val categoryName: String?
    )
}