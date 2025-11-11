package com.example.culturex


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.ItineraryEventAdapter
import com.example.culturex.data.models.ItineraryEvent
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import com.example.culturex.utils.ItineraryEventScheduler

class ItineraryFragment : Fragment(R.layout.fragment_itinerary) {

    // UI elements
    private lateinit var backArrow: ImageView
    private lateinit var addEventButton: Button
    private lateinit var saveButton: Button
    private lateinit var viewSavedButton: Button
    private lateinit var editButton: Button
    private lateinit var filterButton: Button
    private lateinit var createEventCard: View
    private lateinit var calendarIcon: ImageView
    private lateinit var selectedDateText: TextView
    private lateinit var selectedTimeText: TextView
    private lateinit var eventDescriptionInput: TextInputEditText
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: ItineraryEventAdapter

    // Variables to store current state
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedDateMillis: Long = 0
    private var editingEventId: String? = null
    private var isEditMode = false
    private val calendar = Calendar.getInstance()

    // Store the selected year, month, day, hour, and minute for notification scheduling
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0

    // Called when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup UI components and event listeners
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadSavedEvents()
    }

    // Finds and assigns all UI components from the layout
    private fun initializeViews(view: View) {
        backArrow = view.findViewById(R.id.back_arrow)
        addEventButton = view.findViewById(R.id.add_event_button)
        saveButton = view.findViewById(R.id.save_button)
        viewSavedButton = view.findViewById(R.id.view_saved_button)
        editButton = view.findViewById(R.id.edit_button)
        filterButton = view.findViewById(R.id.filter_button)
        createEventCard = view.findViewById(R.id.create_event_card)
        calendarIcon = view.findViewById(R.id.calendar_icon)
        selectedDateText = view.findViewById(R.id.selected_date_text)
        selectedTimeText = view.findViewById(R.id.selected_time_text)
        eventDescriptionInput = view.findViewById(R.id.event_description_input)
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view)
    }

    // Sets up RecyclerView with adapter for itinerary events
    private fun setupRecyclerView() {
        eventAdapter = ItineraryEventAdapter(
            onEditClick = { event ->
                editEvent(event)
            },
            onDeleteClick = { event ->
                deleteEvent(event)
            }
        )
        eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    // Attaches listeners to buttons and UI elements
    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Toggle visibility of event creation form
        addEventButton.setOnClickListener {
            createEventCard.visibility = if (createEventCard.visibility == View.VISIBLE) {
                clearForm()
                View.GONE
            } else {
                clearForm()
                View.VISIBLE
            }
        }
        // open date and time pickers
        calendarIcon.setOnClickListener {
            showDateTimePickers()
        }
        // If editing an event, update it; otherwise, save a new one
        saveButton.setOnClickListener {
            if (editingEventId != null) {
                updateEvent()
            } else {
                saveEvent()
            }
        }
        // Toggle visibility of saved events
        viewSavedButton.setOnClickListener {
            eventsRecyclerView.visibility = if (eventsRecyclerView.visibility == View.VISIBLE) {
                viewSavedButton.text = "View Saved Itinerary Events"
                View.GONE
            } else {
                viewSavedButton.text = "Hide Saved Itinerary Events"
                View.VISIBLE
            }
        }

        // Enable or disable edit mode on events
        editButton.setOnClickListener {
            isEditMode = !isEditMode
            eventAdapter.setEditMode(isEditMode)
            editButton.text = if (isEditMode) "Done" else "Edit"
        }
        // open filter dialog
        filterButton.setOnClickListener {
            showFilterDialog()
        }
    }

    // Show date picker and then time picker
    private fun showDateTimePickers() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                selectedDateMillis = cal.timeInMillis
                selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                selectedDateText.text = "Date: $selectedDate"

                // Store date components for notification scheduling
                selectedYear = year
                selectedMonth = month + 1 // Calendar months are 0-indexed
                selectedDay = dayOfMonth

                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Show time picker dialog
    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                selectedTimeText.text = "Time: $selectedTime"

                // Store time components for notification scheduling
                selectedHour = hourOfDay
                selectedMinute = minute
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Save a new event into preferences
    private fun saveEvent() {
        val description = eventDescriptionInput.text.toString().trim()

        // Validation checks for date and time
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        // Validation checks for description of event
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter event description", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate unique ID for the event
        val eventId = ItineraryEventScheduler.generateUniqueId()

        // Create event object
        val event = ItineraryEvent(
            id = eventId,
            date = selectedDate,
            time = selectedTime,
            description = description,
            timestamp = selectedDateMillis
        )

        // Persist event
        saveEventToPreferences(event)

        // Schedule notifications for this event
        try {
            val eventDateTime = LocalDateTime.of(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute
            )

            ItineraryEventScheduler.scheduleItineraryEvent(
                context = requireContext(),
                eventId = eventId,
                eventTitle = "Itinerary Event",
                eventDescription = description,
                eventDateTime = eventDateTime
            )

            Log.d("ItineraryFragment", "Scheduled notifications for event: $description at $eventDateTime")
        } catch (e: Exception) {
            Log.e("ItineraryFragment", "Error scheduling notifications: ${e.message}")
            Toast.makeText(requireContext(), "Event saved but notifications failed", Toast.LENGTH_SHORT).show()
        }

        clearForm()
        createEventCard.visibility = View.GONE
        Toast.makeText(requireContext(), "Event saved successfully with reminders!", Toast.LENGTH_SHORT).show()
        loadSavedEvents()
    }

    // Load event data into form for editing
    private fun editEvent(event: ItineraryEvent) {
        editingEventId = event.id
        selectedDate = event.date
        selectedTime = event.time
        selectedDateMillis = event.timestamp

        // Parse the date and time to extract components
        try {
            val dateParts = event.date.split("/")
            val timeParts = event.time.split(":")

            selectedDay = dateParts[0].toInt()
            selectedMonth = dateParts[1].toInt()
            selectedYear = dateParts[2].toInt()
            selectedHour = timeParts[0].toInt()
            selectedMinute = timeParts[1].toInt()
        } catch (e: Exception) {
            Log.e("ItineraryFragment", "Error parsing date/time: ${e.message}")
        }

        selectedDateText.text = "Date: $selectedDate"
        selectedTimeText.text = "Time: $selectedTime"
        eventDescriptionInput.setText(event.description)

        createEventCard.visibility = View.VISIBLE
        saveButton.text = "Update Event"

        // Hide list while editing
        eventsRecyclerView.visibility = View.GONE
        viewSavedButton.text = "View Saved Itinerary Events"
    }

    // Update an existing event in preferences
    private fun updateEvent() {
        val description = eventDescriptionInput.text.toString().trim()

        // Validation checks for date and time
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        // Validation checks for description
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter event description", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<MutableList<ItineraryEvent>>() {}.type
        val events: MutableList<ItineraryEvent> = gson.fromJson(eventsJson, type)

        // Replace event with updated values
        val index = events.indexOfFirst { it.id == editingEventId }
        if (index != -1) {
            events[index] = ItineraryEvent(
                id = editingEventId!!,
                date = selectedDate,
                time = selectedTime,
                description = description,
                timestamp = selectedDateMillis
            )
            prefs.edit().putString("events", gson.toJson(events)).apply()

            // Cancel old notifications and schedule new ones
            try {
                // Cancel old notifications
                ItineraryEventScheduler.cancelEvent(requireContext(), editingEventId!!)

                // Schedule new notifications with updated date/time
                val eventDateTime = LocalDateTime.of(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    selectedHour,
                    selectedMinute
                )

                ItineraryEventScheduler.scheduleItineraryEvent(
                    context = requireContext(),
                    eventId = editingEventId!!,
                    eventTitle = "Itinerary Event",
                    eventDescription = description,
                    eventDateTime = eventDateTime
                )

                Log.d("ItineraryFragment", "Updated notifications for event: $description")
            } catch (e: Exception) {
                Log.e("ItineraryFragment", "Error updating notifications: ${e.message}")
            }

            Toast.makeText(requireContext(), "Event updated successfully!", Toast.LENGTH_SHORT).show()
        }

        // Reset form
        clearForm()
        createEventCard.visibility = View.GONE
        saveButton.text = "Save"
        editingEventId = null
        loadSavedEvents()
    }

    // Delete event from preferences
    private fun deleteEvent(event: ItineraryEvent) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->
                val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
                val gson = Gson()
                val eventsJson = prefs.getString("events", "[]")
                val type = object : TypeToken<MutableList<ItineraryEvent>>() {}.type
                val events: MutableList<ItineraryEvent> = gson.fromJson(eventsJson, type)

                // Cancel notifications for this event
                try {
                    ItineraryEventScheduler.cancelEvent(requireContext(), event.id)
                    Log.d("ItineraryFragment", "Cancelled notifications for deleted event: ${event.description}")
                } catch (e: Exception) {
                    Log.e("ItineraryFragment", "Error cancelling notifications: ${e.message}")
                }

                // Removes by ID for deleted item
                events.removeAll { it.id == event.id }
                prefs.edit().putString("events", gson.toJson(events)).apply()

                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
                loadSavedEvents()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Show filter dialog (enter date to filter events)
    private fun showFilterDialog() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inputEditText = EditText(requireContext())
        inputEditText.hint = "DD/MM/YYYY"

        AlertDialog.Builder(requireContext())
            .setTitle("Filter Itinerary by Date")
            .setMessage("Enter date to filter events:")
            .setView(inputEditText)
            .setPositiveButton("Filter") { _, _ ->
                val filterDate = inputEditText.text.toString()
                filterEventsByDate(filterDate)
            }
            .setNegativeButton("Show All") { _, _ ->
                loadSavedEvents()
            }
            .show()
    }

    // Filters events by date entered in dialog
    private fun filterEventsByDate(date: String) {
        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<List<ItineraryEvent>>() {}.type
        val events: List<ItineraryEvent> = gson.fromJson(eventsJson, type)

        val filteredEvents = events.filter { it.date == date }
        eventAdapter.updateEvents(filteredEvents)

        if (filteredEvents.isEmpty()) {
            Toast.makeText(requireContext(), "No events found for this date", Toast.LENGTH_SHORT).show()
        }
    }

    // Clears the event creation form
    private fun clearForm() {
        eventDescriptionInput.text?.clear()
        selectedDate = ""
        selectedTime = ""
        selectedDateMillis = 0
        selectedYear = 0
        selectedMonth = 0
        selectedDay = 0
        selectedHour = 0
        selectedMinute = 0
        selectedDateText.text = "Date: Not selected"
        selectedTimeText.text = "Time: Not selected"
        editingEventId = null
        saveButton.text = "Save"
    }

    // Helper method to save event into SharedPreferences
    private fun saveEventToPreferences(event: ItineraryEvent) {
        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<MutableList<ItineraryEvent>>() {}.type
        val events: MutableList<ItineraryEvent> = gson.fromJson(eventsJson, type)
        events.add(event)
        prefs.edit().putString("events", gson.toJson(events)).apply()
    }

    // Loads saved events from SharedPreferences and updates RecyclerView
    private fun loadSavedEvents() {
        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<List<ItineraryEvent>>() {}.type
        val events: List<ItineraryEvent> = gson.fromJson(eventsJson, type)
        eventAdapter.updateEvents(events)
    }
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]
// Android Developers, 2025. Schedule tasks with WorkManager. [online]. Available at: https://developer.android.com/topic/libraries/architecture/workmanager [Accessed on 9 November 2025]