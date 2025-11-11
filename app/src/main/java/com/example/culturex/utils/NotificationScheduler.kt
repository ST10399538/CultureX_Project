package com.example.culturex.utils

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.culturex.workers.NotificationWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val TAG = "NotificationScheduler"

    fun scheduleEventNotifications(
        context: Context,
        eventId: String,
        eventTitle: String,
        eventDescription: String,
        eventDateTime: LocalDateTime,
        isHoliday: Boolean = false
    ) {
        val now = LocalDateTime.now()

        // Cancel any existing notifications for this event
        cancelEventNotifications(context, eventId)

        val eventType = if (isHoliday) "holiday" else "event"

        // Calculate time differences
        val hoursUntilEvent = Duration.between(now, eventDateTime).toHours()

        // Schedule 24 hours before (if applicable)
        if (hoursUntilEvent >= 24) {
            val delay24Hours = Duration.between(now, eventDateTime.minusHours(24)).toMinutes()
            if (delay24Hours > 0) {
                scheduleNotification(
                    context,
                    eventId,
                    eventTitle,
                    eventDescription,
                    delay24Hours,
                    "24 hours",
                    eventType,
                    "${eventId}_24h"
                )
            }
        }

        // Schedule 2 hours before
        if (hoursUntilEvent >= 2) {
            val delay2Hours = Duration.between(now, eventDateTime.minusHours(2)).toMinutes()
            if (delay2Hours > 0) {
                scheduleNotification(
                    context,
                    eventId,
                    eventTitle,
                    eventDescription,
                    delay2Hours,
                    "2 hours",
                    eventType,
                    "${eventId}_2h"
                )
            }
        }

        // Schedule 1 hour before
        if (hoursUntilEvent >= 1) {
            val delay1Hour = Duration.between(now, eventDateTime.minusHours(1)).toMinutes()
            if (delay1Hour > 0) {
                scheduleNotification(
                    context,
                    eventId,
                    eventTitle,
                    eventDescription,
                    delay1Hour,
                    "1 hour",
                    eventType,
                    "${eventId}_1h"
                )
            }
        }

        Log.d(TAG, "Scheduled notifications for event: $eventTitle at $eventDateTime")
    }

    private fun scheduleNotification(
        context: Context,
        eventId: String,
        eventTitle: String,
        eventDescription: String,
        delayInMinutes: Long,
        timeType: String,
        eventType: String,
        workTag: String
    ) {
        val inputData = workDataOf(
            "EVENT_ID" to eventId,
            "EVENT_TITLE" to eventTitle,
            "EVENT_DESCRIPTION" to eventDescription,
            "TIME_TYPE" to timeType,
            "EVENT_TYPE" to eventType
        )

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .addTag(workTag)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            workTag,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )

        Log.d(TAG, "Scheduled $timeType notification for $eventTitle (delay: $delayInMinutes minutes)")
    }

    fun cancelEventNotifications(context: Context, eventId: String) {
        val tags = listOf("${eventId}_24h", "${eventId}_2h", "${eventId}_1h")
        tags.forEach { tag ->
            WorkManager.getInstance(context).cancelAllWorkByTag(tag)
        }
        Log.d(TAG, "Cancelled all notifications for event: $eventId")
    }

    fun schedulePublicHolidayNotifications(
        context: Context,
        holidays: List<Pair<String, LocalDateTime>>
    ) {
        holidays.forEach { (holidayName, holidayDate) ->
            scheduleEventNotifications(
                context = context,
                eventId = "holiday_${holidayName.replace(" ", "_")}_${holidayDate.toLocalDate()}",
                eventTitle = holidayName,
                eventDescription = "Public Holiday",
                eventDateTime = holidayDate.withHour(9).withMinute(0),
                isHoliday = true
            )
        }
    }
}