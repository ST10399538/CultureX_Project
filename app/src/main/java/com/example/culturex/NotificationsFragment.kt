package com.example.culturex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.NotificationAdapter
import com.example.culturex.data.models.InAppNotification
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationsFragment : Fragment() {

    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var notificationCountBadge: TextView
    private lateinit var notificationBadgeCard: MaterialCardView
    private lateinit var markAllReadCard: MaterialCardView
    private lateinit var profileIcon: ImageView
    private lateinit var notificationAdapter: NotificationAdapter

    // Navigation buttons
    private lateinit var navHome: LinearLayout
    private lateinit var navEmergency: LinearLayout
    private lateinit var navSaved: LinearLayout
    private lateinit var navNotifications: LinearLayout

    // Filter chips
    private lateinit var chipAll: Chip
    private lateinit var chipTravelAlerts: Chip
    private lateinit var chipUpdates: Chip
    private lateinit var chipTips: Chip

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        setupFilterChips()
        loadNotifications()
    }

    private fun initializeViews(view: View) {
        notificationsRecyclerView = view.findViewById(R.id.notifications_recycler_view)
        emptyStateLayout = view.findViewById(R.id.empty_state_notifications)
        notificationCountBadge = view.findViewById(R.id.notification_count)
        notificationBadgeCard = view.findViewById(R.id.notification_badge)
        markAllReadCard = view.findViewById(R.id.mark_all_read_card)
        profileIcon = view.findViewById(R.id.profile_icon)

        // Bottom nav
        navHome = view.findViewById(R.id.nav_home)
        navEmergency = view.findViewById(R.id.nav_emergency)
        navSaved = view.findViewById(R.id.nav_saved)
        navNotifications = view.findViewById(R.id.nav_notifications)

        // Filter chips
        chipAll = view.findViewById(R.id.chip_all_notifications)
        chipTravelAlerts = view.findViewById(R.id.chip_travel_alerts)
        chipUpdates = view.findViewById(R.id.chip_updates)
        chipTips = view.findViewById(R.id.chip_tips)
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { notification ->
            markAsRead(notification)
        }

        notificationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
    }

    private fun setupClickListeners() {
        markAllReadCard.setOnClickListener {
            markAllAsRead()
        }

        profileIcon.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                // Profile navigation not set up yet
            }
        }

        // Bottom navigation - FIXED HOME NAVIGATION
        navHome.setOnClickListener {
            try {
                // Try to navigate to MainFragment directly
                findNavController().navigate(R.id.mainFragment)
            } catch (e: Exception) {
                // If that doesn't work, try popping to mainFragment
                try {
                    findNavController().popBackStack(R.id.mainFragment, false)
                } catch (ex: Exception) {
                    // Last resort - just pop back once
                    findNavController().popBackStack()
                }
            }
        }

        navEmergency.setOnClickListener {
            try {
                findNavController().navigate(R.id.emergencyFragment)
            } catch (e: Exception) {
                // Emergency navigation not set up
            }
        }

        navSaved.setOnClickListener {
            try {
                findNavController().navigate(R.id.savedFragment)
            } catch (e: Exception) {
                // Saved navigation not set up
            }
        }

        navNotifications.setOnClickListener {
            // Already on notifications - scroll to top
            notificationsRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun setupFilterChips() {
        chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) filterNotifications("all")
        }

        chipTravelAlerts.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) filterNotifications("event")
        }

        chipUpdates.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) filterNotifications("holiday")
        }

        chipTips.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) filterNotifications("tips")
        }
    }

    private fun loadNotifications(filter: String = "all") {
        val prefs = requireContext().getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = prefs.getString("notifications", "[]")
        val type = object : TypeToken<List<InAppNotification>>() {}.type
        val allNotifications: List<InAppNotification> = gson.fromJson(notificationsJson, type)

        val filteredNotifications = when (filter) {
            "event" -> allNotifications.filter { it.type == "event" }
            "holiday" -> allNotifications.filter { it.type == "holiday" }
            "tips" -> emptyList()
            else -> allNotifications
        }

        if (filteredNotifications.isEmpty()) {
            notificationsRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            notificationBadgeCard.visibility = View.GONE
        } else {
            notificationsRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
            notificationAdapter.updateNotifications(filteredNotifications)

            val unreadCount = filteredNotifications.count { !it.isRead }
            if (unreadCount > 0) {
                notificationBadgeCard.visibility = View.VISIBLE
                notificationCountBadge.text = unreadCount.toString()
            } else {
                notificationBadgeCard.visibility = View.GONE
            }
        }
    }

    private fun filterNotifications(filter: String) {
        loadNotifications(filter)
    }

    private fun markAsRead(notification: InAppNotification) {
        val prefs = requireContext().getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = prefs.getString("notifications", "[]")
        val type = object : TypeToken<MutableList<InAppNotification>>() {}.type
        val notifications: MutableList<InAppNotification> = gson.fromJson(notificationsJson, type)

        val index = notifications.indexOfFirst { it.id == notification.id }
        if (index != -1) {
            notifications[index] = notification.copy(isRead = true)
            prefs.edit().putString("notifications", gson.toJson(notifications)).apply()
            loadNotifications()
        }
    }

    private fun markAllAsRead() {
        val prefs = requireContext().getSharedPreferences("notifications_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val notificationsJson = prefs.getString("notifications", "[]")
        val type = object : TypeToken<MutableList<InAppNotification>>() {}.type
        val notifications: MutableList<InAppNotification> = gson.fromJson(notificationsJson, type)

        if (notifications.isEmpty()) {
            return
        }

        val updatedNotifications = notifications.map { it.copy(isRead = true) }
        prefs.edit().putString("notifications", gson.toJson(updatedNotifications)).apply()

        loadNotifications()

        // Show success message with checkmark
        view?.let {
            Snackbar.make(it, "✓ All notifications marked as read", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(requireContext().getColor(android.R.color.holo_green_dark))
                .setTextColor(requireContext().getColor(android.R.color.white))
                .setActionTextColor(requireContext().getColor(android.R.color.white))
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }
}

// Reference List:
// Android Developers, 2025. Schedule tasks with WorkManager. [online]. Available at: https://developer.android.com/topic/libraries/architecture/workmanager [Accessed on 9 November 2025]
// Android Developers, 2025. Create and manage notification channels. [online]. Available at: https://developer.android.com/develop/ui/views/notifications/channels [Accessed on 9 November 2025]
// Material Design, 2025. Snackbars. [online]. Available at: https://m3.material.io/components/snackbar/overview [Accessed on 9 November 2025]
// Android Developers, 2025. Navigate with the Navigation component. [online]. Available at: https://developer.android.com/guide/navigation [Accessed on 9 November 2025]