package com.example.culturex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.example.culturex.data.models.TouristAttraction
import kotlin.math.roundToInt

class TouristAttractionsAdapter(
    private val onAttractionClick: (TouristAttraction) -> Unit
) : RecyclerView.Adapter<TouristAttractionsAdapter.AttractionViewHolder>() {

    // Original full list of attractions
    private var attractions: List<TouristAttraction> = emptyList()
    // Filtered list of attractions (based on search or sorting)
    private var filteredAttractions: List<TouristAttraction> = emptyList()

    // Updates the list of attractions and resets the filtered list
    fun updateAttractions(newAttractions: List<TouristAttraction>) {
        attractions = newAttractions
        filteredAttractions = newAttractions
        notifyDataSetChanged()
    }

    // Filters attractions based on a query (name, category, or description)
    fun filterAttractions(query: String) {
        filteredAttractions = if (query.isEmpty()) {
            attractions
        } else {
            attractions.filter {
                it.name.contains(query, ignoreCase = true) || // Match by name
                        it.category.contains(query, ignoreCase = true) || // Match by category
                        it.description.contains(query, ignoreCase = true) // Match by description
            }
        }
        // Refresh list after filtering
        notifyDataSetChanged()
    }

    // Sorts the filtered attractions by distance from the user
    fun sortByDistance() {
        filteredAttractions = filteredAttractions.sortedBy { it.distanceFromUser }
        notifyDataSetChanged()
    }
    // Creates and inflates a new ViewHolder (each row in RecyclerView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tourist_attraction, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(filteredAttractions[position])
    }

    // Returns number of items in the filtered list
    override fun getItemCount(): Int = filteredAttractions.size

    // ViewHolder class: holds references to views for each item
    inner class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.attraction_name)
        private val categoryText: TextView = itemView.findViewById(R.id.attraction_category)
        private val distanceText: TextView = itemView.findViewById(R.id.attraction_distance)
        private val imageView: ImageView = itemView.findViewById(R.id.attraction_image)

        fun bind(attraction: TouristAttraction) {
            nameText.text = attraction.name
            categoryText.text = attraction.category

            val distanceKm = attraction.distanceFromUser
            distanceText.text = if (distanceKm > 0) {
                if (distanceKm < 1) {
                    // Show distance in meters if less than 1 km
                    "${(distanceKm * 1000).roundToInt()}m away"
                } else {
                    // Show distance in kilometers with 1 decimal
                    "${String.format("%.1f", distanceKm)}km away"
                }
            } else {
                "Distance unavailable"
            }

            // Handle click on the entire item (row)
            itemView.setOnClickListener {
                onAttractionClick(attraction)
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