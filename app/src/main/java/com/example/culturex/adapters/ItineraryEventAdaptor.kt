package com.example.culturex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.example.culturex.data.models.ItineraryEvent

class ItineraryEventAdapter(
    // Lambda functions for handling edit and delete actions; default to empty
    private val onEditClick: (ItineraryEvent) -> Unit = {},
    private val onDeleteClick: (ItineraryEvent) -> Unit = {}
) : RecyclerView.Adapter<ItineraryEventAdapter.EventViewHolder>() {

    // List holding the events to display
    private var events: List<ItineraryEvent> = emptyList()
    // Boolean flag to indicate if the RecyclerView is in edit mode
    private var isEditMode: Boolean = false

    // Update the list of events and refresh the RecyclerView
    fun updateEvents(newEvents: List<ItineraryEvent>) {
        events = newEvents.sortedBy { it.timestamp }
        notifyDataSetChanged()
    }

    // Enable or disable edit mode and refresh the RecyclerView
    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        notifyDataSetChanged()
    }

    // Inflate the item layout and create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], position + 1, isEditMode)
    }

    override fun getItemCount(): Int = events.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.event_time)
        private val titleText: TextView = itemView.findViewById(R.id.event_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.event_description)
        private val editIcon: ImageView = itemView.findViewById(R.id.edit_icon)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)

        // Bind data to UI elements
        fun bind(event: ItineraryEvent, position: Int, editMode: Boolean) {
            timeText.text = event.time
            titleText.text = "Event - $position"
            descriptionText.text = event.description
            // Show or hide edit and delete icons based on edit mode

            if (editMode) {
                editIcon.visibility = View.VISIBLE
                deleteIcon.visibility = View.VISIBLE
            } else {
                editIcon.visibility = View.GONE
                deleteIcon.visibility = View.GONE
            }

            editIcon.setOnClickListener {
                onEditClick(event)
            }

            deleteIcon.setOnClickListener {
                onDeleteClick(event)
            }

            // Clicking the item itself also triggers edit if in edit mode
            itemView.setOnClickListener {
                if (editMode) {
                    onEditClick(event)
                }
            }
        }
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