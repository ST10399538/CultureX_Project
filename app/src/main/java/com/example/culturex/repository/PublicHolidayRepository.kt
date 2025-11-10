package com.example.culturex.repository

import android.util.Log
import com.example.culturex.api.PublicHoliday
import com.example.culturex.api.PublicHolidayService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PublicHolidayRepository {

    private val api = PublicHolidayService.api
    private val TAG = "PublicHolidayRepo"

    suspend fun getUpcomingHolidays(
        countryCode: String,
        year: Int = LocalDate.now().year
    ): List<Pair<String, LocalDateTime>> = withContext(Dispatchers.IO) {
        try {
            val holidays = api.getPublicHolidays(year, countryCode)
            val today = LocalDate.now()

            holidays
                .filter { holiday ->
                    val holidayDate = LocalDate.parse(holiday.date)
                    !holidayDate.isBefore(today)
                }
                .map { holiday ->
                    val localDateTime = LocalDate.parse(holiday.date).atStartOfDay()
                    Pair(holiday.localName, localDateTime)
                }
                .also {
                    Log.d(TAG, "Retrieved ${it.size} upcoming holidays for $countryCode")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching holidays: ${e.message}")
            emptyList()
        }
    }

    // Country code mappings (ISO 3166-1 alpha-2)
    companion object {
        val COUNTRY_CODES = mapOf(
            "South Africa" to "ZA",
            "United States" to "US",
            "United Kingdom" to "GB",
            "Canada" to "CA",
            "Australia" to "AU",
            "Germany" to "DE",
            "France" to "FR",
            "Japan" to "JP",
            "China" to "CN",
            "India" to "IN",
            "Brazil" to "BR",
            "Mexico" to "MX",
            "Italy" to "IT",
            "Spain" to "ES",
            "Netherlands" to "NL"
        )

        fun getCountryCode(countryName: String): String {
            return COUNTRY_CODES[countryName] ?: "ZA" // Default to South Africa
        }
    }
}