package com.example.culturex

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.ItineraryEventAdapter
import com.example.culturex.data.models.ItineraryEvent
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ItineraryFragment : Fragment(R.layout.fragment_itinerary) {

    private lateinit var backArrow: ImageView
    private lateinit var addEventButton: Button
    private lateinit var saveButton: Button
    private lateinit var viewSavedButton: Button
    private lateinit var createEventCard: View
    private lateinit var calendarIcon: ImageView
    private lateinit var selectedDateText: TextView
    private lateinit var selectedTimeText: TextView
    private lateinit var eventDescriptionInput: TextInputEditText
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: ItineraryEventAdapter

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private val calendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadSavedEvents()
    }

    private fun initializeViews(view: View) {
        backArrow = view.findViewById(R.id.back_arrow)
        addEventButton = view.findViewById(R.id.add_event_button)
        saveButton = view.findViewById(R.id.save_button)
        viewSavedButton = view.findViewById(R.id.view_saved_button)
        createEventCard = view.findViewById(R.id.create_event_card)
        calendarIcon = view.findViewById(R.id.calendar_icon)
        selectedDateText = view.findViewById(R.id.selected_date_text)
        selectedTimeText = view.findViewById(R.id.selected_time_text)
        eventDescriptionInput = view.findViewById(R.id.event_description_input)
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view)
    }

    private fun setupRecyclerView() {
        eventAdapter = ItineraryEventAdapter()
        eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupClickListeners() {
        backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        addEventButton.setOnClickListener {
            createEventCard.visibility = if (createEventCard.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        calendarIcon.setOnClickListener {
            showDateTimePickers()
        }

        saveButton.setOnClickListener {
            saveEvent()
        }

        viewSavedButton.setOnClickListener {
            eventsRecyclerView.visibility = if (eventsRecyclerView.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun showDateTimePickers() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                selectedDateText.text = "Date: $selectedDate"

                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                selectedTimeText.text = "Time: $selectedTime"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveEvent() {
        val description = eventDescriptionInput.text.toString().trim()

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter event description", Toast.LENGTH_SHORT).show()
            return
        }

        val event = ItineraryEvent(
            date = selectedDate,
            time = selectedTime,
            description = description
        )

        saveEventToPreferences(event)

        // Clear form
        eventDescriptionInput.text?.clear()
        selectedDate = ""
        selectedTime = ""
        selectedDateText.text = "Date: Not selected"
        selectedTimeText.text = "Time: Not selected"
        createEventCard.visibility = View.GONE

        Toast.makeText(requireContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show()
        loadSavedEvents()
    }

    private fun saveEventToPreferences(event: ItineraryEvent) {
        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<MutableList<ItineraryEvent>>() {}.type
        val events: MutableList<ItineraryEvent> = gson.fromJson(eventsJson, type)

        events.add(event)

        prefs.edit().putString("events", gson.toJson(events)).apply()
    }

    private fun loadSavedEvents() {
        val prefs = requireContext().getSharedPreferences("itinerary_prefs", Context.MODE_PRIVATE)
        val gson = Gson()

        val eventsJson = prefs.getString("events", "[]")
        val type = object : TypeToken<List<ItineraryEvent>>() {}.type
        val events: List<ItineraryEvent> = gson.fromJson(eventsJson, type)

        eventAdapter.updateEvents(events)
    }
}