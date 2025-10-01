package com.example.culturex.data.models

import com.google.gson.annotations.SerializedName

class CountryModels {

    // Data Transfer Object (DTO) representing a country
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

    // Data Transfer Object (DTO) representing a cultural category within a country
    data class CulturalCategoryDTO(
        val id: String,
        val name: String?,
        val description: String?,
        val iconUrl: String?,
        val sortOrder: Int = 0
    )

    // Data Transfer Object (DTO) representing specific cultural content
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

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]