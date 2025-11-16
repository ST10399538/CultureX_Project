package com.example.culturex.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.example.culturex.data.models.InAppNotification
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val onNotificationClick: (InAppNotification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var notifications = listOf<InAppNotification>()

    fun updateNotifications(newNotifications: List<InAppNotification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val notificationCard: MaterialCardView = itemView.findViewById(R.id.notification_card)
        private val iconContainer: MaterialCardView = itemView.findViewById(R.id.icon_container)
        private val iconImageView: ImageView = itemView.findViewById(R.id.notification_icon)
        private val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        private val messageTextView: TextView = itemView.findViewById(R.id.notification_message)
        private val timeTextView: TextView = itemView.findViewById(R.id.notification_time)
        private val unreadIndicator: View = itemView.findViewById(R.id.unread_indicator)
        private val typeLabel: TextView = itemView.findViewById(R.id.type_label)

        fun bind(notification: InAppNotification) {
            titleTextView.text = notification.title
            messageTextView.text = notification.message
            timeTextView.text = formatTime(notification.timestamp)

            // Set type label and colors
            when (notification.type) {
                "holiday" -> {
                    typeLabel.text = "Holiday"
                    typeLabel.setBackgroundResource(R.drawable.bg_label_holiday)
                    iconImageView.setImageResource(R.drawable.ic_notifications)
                    iconContainer.setCardBackgroundColor(itemView.context.getColor(android.R.color.holo_orange_light))
                }
                "event" -> {
                    typeLabel.text = "Event"
                    typeLabel.setBackgroundResource(R.drawable.bg_label_event)
                    iconImageView.setImageResource(R.drawable.ic_notifications)
                    iconContainer.setCardBackgroundColor(itemView.context.getColor(android.R.color.holo_blue_light))
                }
                else -> {
                    typeLabel.text = "Notification"
                    typeLabel.setBackgroundResource(R.drawable.bg_label_default)
                    iconImageView.setImageResource(R.drawable.ic_notifications)
                    iconContainer.setCardBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                }
            }

            // Style based on read status
            if (!notification.isRead) {
                unreadIndicator.visibility = View.VISIBLE
                notificationCard.strokeWidth = 2
                notificationCard.strokeColor = itemView.context.getColor(R.color.primary_color)
                titleTextView.setTypeface(null, Typeface.BOLD)
                messageTextView.setTypeface(null, Typeface.BOLD)
                notificationCard.elevation = 4f
            } else {
                unreadIndicator.visibility = View.GONE
                notificationCard.strokeWidth = 0
                titleTextView.setTypeface(null, Typeface.NORMAL)
                messageTextView.setTypeface(null, Typeface.NORMAL)
                notificationCard.elevation = 2f
                notificationCard.alpha = 0.7f
            }

            itemView.setOnClickListener {
                onNotificationClick(notification)
            }
        }

        private fun formatTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                diff < 604800000 -> "${diff / 86400000}d ago"
                else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
            }
        }
    }
}