package com.example.culturex.utils

import android.content.Context
import android.util.Log
import com.example.culturex.repository.PublicHolidayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

object ItineraryEventScheduler {

    private val holidayRepository = PublicHolidayRepository()
    private const val TAG = "ItineraryScheduler"

    fun generateUniqueId(): String {
        return UUID.randomUUID().toString()
    }

    fun scheduleItineraryEvent(
        context: Context,
        eventId: String,
        eventTitle: String,
        eventDescription: String,
        eventDateTime: LocalDateTime
    ) {
        NotificationScheduler.scheduleEventNotifications(
            context = context,
            eventId = eventId,
            eventTitle = eventTitle,
            eventDescription = eventDescription,
            eventDateTime = eventDateTime,
            isHoliday = false
        )
    }

    fun schedulePublicHolidays(
        context: Context,
        countryName: String,
        scope: CoroutineScope
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val countryCode = PublicHolidayRepository.getCountryCode(countryName)
                val holidays = holidayRepository.getUpcomingHolidays(countryCode)

                if (holidays.isNotEmpty()) {
                    NotificationScheduler.schedulePublicHolidayNotifications(context, holidays)
                    Log.d(TAG, "Scheduled ${holidays.size} public holiday notifications for $countryName")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error scheduling public holidays: ${e.message}")
            }
        }
    }

    fun cancelEvent(context: Context, eventId: String) {
        NotificationScheduler.cancelEventNotifications(context, eventId)
    }
}
