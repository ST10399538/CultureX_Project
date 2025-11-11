package com.example.culturex.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.culturex.data.models.InAppNotification
import com.example.culturex.utils.NotificationHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val eventTitle = inputData.getString("EVENT_TITLE") ?: "Itinerary Event"
            val eventDescription = inputData.getString("EVENT_DESCRIPTION") ?: ""
            val eventId = inputData.getString("EVENT_ID") ?: ""
            val timeType = inputData.getString("TIME_TYPE") ?: "1 hour"
            val eventType = inputData.getString("EVENT_TYPE") ?: "event"

            val notificationId = eventId.hashCode() + timeType.hashCode()

            val title = when {
                eventType == "holiday" -> "🎉 Public Holiday Reminder"
                else -> "📅 Upcoming Event Reminder"
            }

            val message = when (timeType) {
                "24 hours" -> "$eventTitle is tomorrow! $eventDescription"
                "2 hours" -> "$eventTitle starts in 2 hours! $eventDescription"
                "1 hour" -> "$eventTitle starts in 1 hour! $eventDescription"
                else -> "$eventTitle is coming up! $eventDescription"
            }

            // Send system notification
            NotificationHelper.sendNotification(
                applicationContext,
                notificationId,
                title,
                message,
                eventId
            )

            // Save to in-app notifications
            saveInAppNotification(
                applicationContext,
                title,
                message,
                eventId,
                eventType
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun saveInAppNotification(
        context: Context,
        title: String,
        message: String,
        eventId: String,
        type: String
    ) {
        val prefs = context.getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = prefs.getString("notifications", "[]")
        val typeToken = object : TypeToken<MutableList<InAppNotification>>() {}.type
        val notifications: MutableList<InAppNotification> = gson.fromJson(notificationsJson, typeToken)

        val newNotification = InAppNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            eventId = eventId,
            isRead = false,
            type = type
        )

        notifications.add(0, newNotification) // Add to beginning of list

        // Keep only last 50 notifications
        if (notifications.size > 50) {
            notifications.subList(50, notifications.size).clear()
        }

        prefs.edit().putString("notifications", gson.toJson(notifications)).apply()
    }
}