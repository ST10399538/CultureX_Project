package com.example.culturex.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.R
import com.google.android.material.card.MaterialCardView

class GreetingsAdapter(private val onPlayAudio: (GreetingItem) -> Unit
) : ListAdapter<GreetingItem, GreetingsAdapter.GreetingViewHolder>(GreetingDiffCallback()) {

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GreetingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_greeting_card, parent, false)
        return GreetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: GreetingViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    // Inner class representing a single item in the RecyclerView
    inner class GreetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.greeting_card)
        private val languageTitle: TextView = itemView.findViewById(R.id.language_title)
        private val greetingText: TextView = itemView.findViewById(R.id.greeting_text)
        private val greetingTranslation: TextView = itemView.findViewById(R.id.greeting_translation)
        private val goodbyeText: TextView = itemView.findViewById(R.id.goodbye_text)
        private val goodbyeTranslation: TextView = itemView.findViewById(R.id.goodbye_translation)
        private val pronunciationHint: TextView = itemView.findViewById(R.id.pronunciation_hint)
        private val pronunciationContainer: LinearLayout = itemView.findViewById(R.id.pronunciation_container)
        private val playGreetingAudio: ImageView = itemView.findViewById(R.id.play_greeting_audio)
        private val playGoodbyeAudio: ImageView = itemView.findViewById(R.id.play_goodbye_audio)
        private val goodbyeSection: LinearLayout = itemView.findViewById(R.id.goodbye_section)

        fun bind(item: GreetingItem, position: Int) {
            // Set language title with position number
            languageTitle.text = "$position. ${item.language}"

            // Set greeting information
            greetingText.text = item.greeting
            greetingTranslation.text = item.greetingTranslation

            // Set goodbye information if available
            if (item.goodbye.isNotEmpty()) {
                goodbyeSection.isVisible = true
                goodbyeText.text = item.goodbye
                goodbyeTranslation.text = item.goodbyeTranslation
            } else {
                goodbyeSection.isVisible = false
            }

            // Set pronunciation if available
            if (!item.pronunciation.isNullOrEmpty()) {
                pronunciationContainer.isVisible = true
                pronunciationHint.text = "Pronunciation: ${item.pronunciation}"
            } else {
                pronunciationContainer.isVisible = false
            }

            // Set up audio play buttons
            playGreetingAudio.setOnClickListener {
                onPlayAudio(item)
            }

            playGoodbyeAudio.setOnClickListener {
                onPlayAudio(item)
            }

            // Add animation
            cardView.alpha = 0f
            cardView.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay((position * 50).toLong())
                .start()
        }
    }

    class GreetingDiffCallback : DiffUtil.ItemCallback<GreetingItem>() {
        override fun areItemsTheSame(oldItem: GreetingItem, newItem: GreetingItem): Boolean {
            return oldItem.language == newItem.language
        }

        // Check if the contents of the items are exactly the same
        override fun areContentsTheSame(oldItem: GreetingItem, newItem: GreetingItem): Boolean {
            return oldItem == newItem
        }
    }
}


// GreetingItem.kt


data class GreetingItem(
    val language: String,
    val greeting: String,
    val greetingTranslation: String,
    val goodbye: String,
    val goodbyeTranslation: String,
    val pronunciation: String? = null
)

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]
