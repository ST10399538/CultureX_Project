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

    private var attractions: List<TouristAttraction> = emptyList()
    private var filteredAttractions: List<TouristAttraction> = emptyList()

    fun updateAttractions(newAttractions: List<TouristAttraction>) {
        attractions = newAttractions
        filteredAttractions = newAttractions
        notifyDataSetChanged()
    }

    fun filterAttractions(query: String) {
        filteredAttractions = if (query.isEmpty()) {
            attractions
        } else {
            attractions.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun sortByDistance() {
        filteredAttractions = filteredAttractions.sortedBy { it.distanceFromUser }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tourist_attraction, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        holder.bind(filteredAttractions[position])
    }

    override fun getItemCount(): Int = filteredAttractions.size

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
                    "${(distanceKm * 1000).roundToInt()}m away"
                } else {
                    "${String.format("%.1f", distanceKm)}km away"
                }
            } else {
                "Distance unavailable"
            }

            itemView.setOnClickListener {
                onAttractionClick(attraction)
            }
        }
    }
}