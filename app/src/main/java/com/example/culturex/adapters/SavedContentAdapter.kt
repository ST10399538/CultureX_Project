package com.example.culturex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.example.culturex.data.entities.CachedContent
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.graphics.Color



class SavedContentAdapter(private val onItemClick: (CachedContent) -> Unit,
                          private val onBookmarkClick: (CachedContent) -> Unit,
                          private val onDeleteClick: (CachedContent) -> Unit,
                          private val onShareClick: ((CachedContent) -> Unit)? = null
) : ListAdapter<CachedContent, SavedContentAdapter.SavedContentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedContentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_content, parent, false)
        return SavedContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardContainer: MaterialCardView = itemView.findViewById(R.id.card_container)
        private val titleText: TextView = itemView.findViewById(R.id.text_title)
        private val categoryText: TextView = itemView.findViewById(R.id.text_category)
        private val countryText: TextView = itemView.findViewById(R.id.text_country)
        private val dateText: TextView = itemView.findViewById(R.id.text_date)
        private val categoryIcon: ImageView = itemView.findViewById(R.id.icon_category)
        private val iconBackground: MaterialCardView = itemView.findViewById(R.id.icon_background)
        private val bookmarkIcon: ImageView = itemView.findViewById(R.id.icon_bookmark)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.icon_delete)
        private val shareButton: MaterialCardView = itemView.findViewById(R.id.btn_share)
        private val deleteButton: MaterialCardView = itemView.findViewById(R.id.btn_delete)
        private val offlineBadge: View = itemView.findViewById(R.id.offline_badge)

        fun bind(content: CachedContent) {
            // Set title with fallback
            titleText.text = content.title
                ?: content.categoryName
                        ?: "Saved Content"

            // Set category
            categoryText.text = when {
                content.categoryName?.contains("Dress", ignoreCase = true) == true ->
                    "Dress Code & Customs"
                content.categoryName?.contains("Etiquette", ignoreCase = true) == true ->
                    "Cultural Etiquette"
                content.categoryName?.contains("Emergency", ignoreCase = true) == true ->
                    "Emergency Services"
                content.categoryName?.contains("Custom", ignoreCase = true) == true ->
                    "Local Customs"
                content.categoryName?.contains("Attraction", ignoreCase = true) == true ->
                    "Tourist Attractions"
                else -> content.categoryName ?: "Cultural Content"
            }

            // Set country
            countryText.text = content.countryName ?: "Unknown"

            // Format and set date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateText.text = "Saved ${dateFormat.format(Date(content.lastUpdated))}"

            // Show offline badge if saved for offline
            offlineBadge.visibility = if (content.isSavedForOffline) View.VISIBLE else View.GONE

            // Set category-specific icon and background color
            val (iconRes, bgColor, iconTint) = getCategoryStyle(content.categoryName)
            categoryIcon.setImageResource(iconRes)
            iconBackground.setCardBackgroundColor(Color.parseColor(bgColor))
            categoryIcon.setColorFilter(Color.WHITE)

            // Set bookmark icon (always filled since this is saved content)
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark_filled)

            // Click listeners
            cardContainer.setOnClickListener {
                onItemClick(content)
            }

            bookmarkIcon.setOnClickListener {
                onBookmarkClick(content)
            }

            deleteButton.setOnClickListener {
                onDeleteClick(content)
            }

            shareButton.setOnClickListener {
                if (onShareClick != null) {
                    onShareClick.invoke(content)
                } else {
                    // Default share functionality
                    shareContent(content)
                }
            }
        }

        private fun getCategoryStyle(categoryName: String?): Triple<Int, String, String> {
            return when {
                categoryName?.contains("Dress", ignoreCase = true) == true ->
                    Triple(R.drawable.ic_dress, "#8B5CF6", "#FFFFFF") // Purple

                categoryName?.contains("Etiquette", ignoreCase = true) == true ->
                    Triple(R.drawable.ic_etiquette, "#3B82F6", "#FFFFFF") // Blue

                categoryName?.contains("Emergency", ignoreCase = true) == true ->
                    Triple(R.drawable.ic_emergency, "#EF4444", "#FFFFFF") // Red

                categoryName?.contains("Custom", ignoreCase = true) == true ->
                    Triple(R.drawable.ic_customs, "#10B981", "#FFFFFF") // Green

                categoryName?.contains("Attraction", ignoreCase = true) == true ->
                    Triple(R.drawable.ic_attractions, "#F59E0B", "#FFFFFF") // Amber



                else -> Triple(R.drawable.ic_bookmark, "#6366F1", "#FFFFFF") // Indigo (default)
            }
        }

        private fun shareContent(content: CachedContent) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, content.title ?: "CultureX Content")
                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    Check out this cultural guide from CultureX!
                    
                    ${content.title}
                    ${content.categoryName} - ${content.countryName}
                    
                    
                    """.trimIndent()
                )
            }

            itemView.context.startActivity(
                Intent.createChooser(shareIntent, "Share via")
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CachedContent>() {
        override fun areItemsTheSame(oldItem: CachedContent, newItem: CachedContent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CachedContent, newItem: CachedContent): Boolean {
            return oldItem == newItem
        }
    }
}
