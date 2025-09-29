package com.example.culturex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.example.culturex.data.models.ItineraryEvent

class ItineraryEventAdapter : RecyclerView.Adapter<ItineraryEventAdapter.EventViewHolder>() {

    private var events: List<ItineraryEvent> = emptyList()

    fun updateEvents(newEvents: List<ItineraryEvent>) {
        events = newEvents.sortedBy { it.timestamp }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_itinerary_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position], position + 1)
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeText: TextView = itemView.findViewById(R.id.event_time)
        private val titleText: TextView = itemView.findViewById(R.id.event_title)
        private val descriptionText: TextView = itemView.findViewById(R.id.event_description)

        fun bind(event: ItineraryEvent, position: Int) {
            timeText.text = event.time
            titleText.text = "Event - $position"
            descriptionText.text = event.description
        }
    }
}