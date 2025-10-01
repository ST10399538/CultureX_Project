package com.example.culturex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.GreetingItem
import com.example.culturex.adapters.GreetingsAdapter
import com.example.culturex.data.viewmodels.ContentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class GreetingsFragment : Fragment(R.layout.fragment_greetings) {

    // ViewModel that fetches cultural content from an API
    private val contentViewModel: ContentViewModel by viewModels()

    // RecyclerView and Adapter to display a list of greetings
    private lateinit var greetingsRecyclerView: RecyclerView
    private lateinit var greetingsAdapter: GreetingsAdapter

    // Stores the name of the current country (for display and loading greetings)
    private var currentCountryName: String = ""

    // Called when the Fragment’s view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Get arguments passed into this fragment (country and category IDs, and country name)
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName") ?: "Country"

        // Save the current country name (used later in UI and greetings)
        currentCountryName = countryName

        // Setup UI components and event listeners
        setupViews(view)
        setupRecyclerView(view)
        setupObservers(view)
        setupClickListeners(view)

        if (countryId != null && categoryId != null) {
            // Load content from API
            contentViewModel.loadContent(countryId, categoryId)
            showLoadingState(view, true)
        } else {
            // Load default data if no IDs provided
            loadPredeterminedGreetings()
        }

        // Animate hero card on load
        animateHeroCard(view)
    }

    private fun setupViews(view: View) {
        // Set country name
        view.findViewById<TextView>(R.id.country_name)?.text = currentCountryName
    }

    private fun setupRecyclerView(view: View) {
        greetingsRecyclerView = view.findViewById(R.id.greetings_recycler_view)
        greetingsAdapter = GreetingsAdapter { greeting ->
            // Handle click on greeting item
            playGreetingAudio(greeting.greeting)
        }

        greetingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = greetingsAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                showLoadingState(view, false)

                // Update title
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Greetings"

                // Update description
                val description = it.content ?: getDefaultGreetingDescription()
                view.findViewById<TextView>(R.id.content_description)?.text = description

                // Try to parse greetings from API content
                val greetings = parseGreetingsFromContent(it)

                if (greetings.isNotEmpty()) {
                    greetingsAdapter.submitList(greetings)
                } else {
                    // Fallback to predetermined data
                    loadPredeterminedGreetings()
                }
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<LinearProgressIndicator>(R.id.progress_indicator)?.isVisible = isLoading
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showLoadingState(view, false)
                Toast.makeText(requireContext(), "Loading default greetings...", Toast.LENGTH_SHORT).show()
                contentViewModel.clearError()

                // Load predetermined data on error
                loadPredeterminedGreetings()
            }
        }
    }

    private fun parseGreetingsFromContent(content: com.example.culturex.data.models.CountryModels.CulturalContentDTO): List<GreetingItem> {
        val greetings = mutableListOf<GreetingItem>()

        // Try to parse from examples if they contain greeting patterns
        content.examples?.forEach { example ->
            if (example.contains("Hello", ignoreCase = true) ||
                example.contains("Goodbye", ignoreCase = true)) {
                // Simple parsing - this would be more sophisticated in production
                val parts = example.split(":")
                if (parts.size >= 2) {
                    greetings.add(
                        GreetingItem(
                            language = "Local Language",
                            greeting = parts[0].trim(),
                            greetingTranslation = parts.getOrNull(1)?.trim() ?: "",
                            goodbye = "",
                            goodbyeTranslation = "",
                            pronunciation = null
                        )
                    )
                }
            }
        }

        // If no greetings found in examples, check content text
        if (greetings.isEmpty() && !content.content.isNullOrEmpty()) {
            // Check if content contains country-specific greetings
            if (currentCountryName.contains("South Africa", ignoreCase = true)) {
                return getPredeterminedSouthAfricanGreetings()
            }
        }

        return greetings
    }

    // Load greetings for specific countries (or fallback to default)
    private fun loadPredeterminedGreetings() {
        val greetings = when {
            currentCountryName.contains("South Africa", ignoreCase = true) -> {
                getPredeterminedSouthAfricanGreetings()
            }
            currentCountryName.contains("France", ignoreCase = true) -> {
                getPredeterminedFrenchGreetings()
            }
            currentCountryName.contains("Japan", ignoreCase = true) -> {
                getPredeterminedJapaneseGreetings()
            }
            currentCountryName.contains("India", ignoreCase = true) -> {
                getPredeterminedIndianGreetings()
            }
            currentCountryName.contains("United States", ignoreCase = true) -> {
                getPredeterminedAmericanGreetings()
            }
            else -> {
                getPredeterminedDefaultGreetings()
            }
        }

        greetingsAdapter.submitList(greetings)
    }

    // Predetermined greetings by country (used as fallback)
    private fun getPredeterminedSouthAfricanGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Zulu (isiZulu)",
                greeting = "Sawubona / Sanibonani",
                greetingTranslation = "(to one person) / (to many) - 'I see you'",
                goodbye = "Hamba kahle / Sala kahle",
                goodbyeTranslation = "(go well) / (stay well)",
                pronunciation = "sah-woo-BOH-nah"
            ),
            GreetingItem(
                language = "Xhosa (isiXhosa)",
                greeting = "Molo / Molweni",
                greetingTranslation = "(to one person) / (to many)",
                goodbye = "Hamba kakuhle / Sala kakuhle",
                goodbyeTranslation = "(go well) / (stay well)",
                pronunciation = "MOH-loh"
            ),
            GreetingItem(
                language = "Afrikaans",
                greeting = "Hallo / Goeie môre",
                greetingTranslation = "Hello / Good morning",
                goodbye = "Totsiens",
                goodbyeTranslation = "Until we meet again",
                pronunciation = "HAH-loh / HOO-yuh MORE-uh"
            ),
            GreetingItem(
                language = "English",
                greeting = "Hello / Hi",
                greetingTranslation = "Standard greeting",
                goodbye = "Goodbye / Cheers",
                goodbyeTranslation = "Standard farewell",
                pronunciation = null
            )
        )
    }

    private fun getPredeterminedFrenchGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "French (Français)",
                greeting = "Bonjour",
                greetingTranslation = "Good day (formal)",
                goodbye = "Au revoir",
                goodbyeTranslation = "Goodbye",
                pronunciation = "bon-ZHOOR / oh reh-VWAHR"
            ),
            GreetingItem(
                language = "French (Informal)",
                greeting = "Salut",
                greetingTranslation = "Hi/Bye (informal)",
                goodbye = "À bientôt",
                goodbyeTranslation = "See you soon",
                pronunciation = "sah-LOO / ah bee-an-TOH"
            )
        )
    }

    private fun getPredeterminedJapaneseGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Japanese (日本語)",
                greeting = "こんにちは (Konnichiwa)",
                greetingTranslation = "Hello (daytime)",
                goodbye = "さようなら (Sayōnara)",
                goodbyeTranslation = "Goodbye",
                pronunciation = "kon-nee-chee-wah"
            ),
            GreetingItem(
                language = "Japanese (Morning)",
                greeting = "おはよう (Ohayō)",
                greetingTranslation = "Good morning",
                goodbye = "また明日 (Mata ashita)",
                goodbyeTranslation = "See you tomorrow",
                pronunciation = "oh-hah-yoh"
            )
        )
    }

    private fun getPredeterminedIndianGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Hindi (हिन्दी)",
                greeting = "नमस्ते (Namaste)",
                greetingTranslation = "Hello/Goodbye (respectful)",
                goodbye = "अलविदा (Alvida)",
                goodbyeTranslation = "Goodbye",
                pronunciation = "nah-mah-STAY"
            ),
            GreetingItem(
                language = "Tamil (தமிழ்)",
                greeting = "வணக்கம் (Vanakkam)",
                greetingTranslation = "Hello (respectful)",
                goodbye = "போய் வருகிறேன் (Poi varugiren)",
                goodbyeTranslation = "I'll go and come back",
                pronunciation = "vah-nahk-kahm"
            )
        )
    }

    private fun getPredeterminedAmericanGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "American English",
                greeting = "Hey / What's up?",
                greetingTranslation = "Casual greeting",
                goodbye = "See ya / Take care",
                goodbyeTranslation = "Casual farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "Spanish (Español)",
                greeting = "Hola",
                greetingTranslation = "Hello",
                goodbye = "Adiós / Hasta luego",
                goodbyeTranslation = "Goodbye / See you later",
                pronunciation = "OH-lah"
            )
        )
    }

    private fun getPredeterminedDefaultGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "International",
                greeting = "Hello",
                greetingTranslation = "Universal greeting",
                goodbye = "Goodbye",
                goodbyeTranslation = "Universal farewell",
                pronunciation = null
            )
        )
    }

    // Setup button click listeners
    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialCardView>(R.id.back_button_card)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Audio button
        view.findViewById<MaterialCardView>(R.id.audio_button_card)?.setOnClickListener {
            playAudioPronunciation()
        }

        // Save offline
        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            saveContentOffline()
        }

        // Practice card
        view.findViewById<MaterialCardView>(R.id.practice_card)?.setOnClickListener {
            startPracticeMode()
        }

        // FAB translate
        view.findViewById<FloatingActionButton>(R.id.fab_translate)?.setOnClickListener {
            showTranslationOptions()
        }
    }

    private fun showLoadingState(view: View, isLoading: Boolean) {
        view.findViewById<MaterialCardView>(R.id.loading_card)?.isVisible = isLoading
        greetingsRecyclerView.isVisible = !isLoading
    }

    private fun animateHeroCard(view: View) {
        val heroCard = view.findViewById<MaterialCardView>(R.id.hero_card)
        val slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        heroCard?.startAnimation(slideIn)
    }

    // Stub functions (currently show Toasts, but could play audio or save data)
    private fun playAudioPronunciation() {
        Toast.makeText(context, "Playing pronunciation guide...", Toast.LENGTH_SHORT).show()
    }

    private fun playGreetingAudio(text: String) {
        Toast.makeText(context, "Playing: $text", Toast.LENGTH_SHORT).show()
    }

    private fun saveContentOffline() {
        Toast.makeText(context, "Greetings saved for offline use", Toast.LENGTH_SHORT).show()
    }

    private fun startPracticeMode() {
        Toast.makeText(context, "Starting practice mode...", Toast.LENGTH_SHORT).show()
    }

    // Stub functions (currently show Toasts, but could play audio or save data)
    private fun showTranslationOptions() {
        Toast.makeText(context, "Translation options", Toast.LENGTH_SHORT).show()
    }

    // Default description for greetings section
    private fun getDefaultGreetingDescription(): String {
        return """
            Learning local greetings shows respect for the culture and helps you connect with people. 
            Below are common greetings in $currentCountryName with their pronunciation guides.
        """.trimIndent()
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
