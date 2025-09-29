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
import com.example.culturex.data.viewmodels.ContentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class GreetingsFragment : Fragment(R.layout.fragment_greetings) {

    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var languagesContainer: LinearLayout
    private var currentCountryName: String = ""
    private var greetingsData: MutableList<GreetingLanguage> = mutableListOf()

    data class GreetingLanguage(
        val name: String,
        val nativeName: String,
        val greeting: String,
        val greetingTranslation: String,
        val goodbye: String,
        val goodbyeTranslation: String,
        val pronunciation: String? = null
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName") ?: "Country"

        currentCountryName = countryName
        languagesContainer = view.findViewById(R.id.languages_container)

        if (countryId != null && categoryId != null) {
            setupViews(view)
            setupObservers(view)
            setupClickListeners(view)

            // Load content from API
            contentViewModel.loadContent(countryId, categoryId)

            // Show loading state
            showLoadingState(view, true)
        }

        // Animate hero card on load
        animateHeroCard(view)
    }

    private fun setupViews(view: View) {
        // Set country name
        view.findViewById<TextView>(R.id.country_name)?.text = currentCountryName

        // Load country flag if available
        // This would typically load from the API's flagImageUrl
    }

    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                showLoadingState(view, false)

                // Update title
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Greetings"

                // Parse and display content
                val description = it.content ?: getDefaultGreetingDescription()
                view.findViewById<TextView>(R.id.content_description)?.text = description

                // Parse language greetings from content
                parseAndDisplayGreetings(it.content ?: "")

                // If we have examples in the API response, use them for greetings
                it.examples?.let { examples ->
                    displayGreetingsFromExamples(examples)
                }
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<LinearProgressIndicator>(R.id.progress_indicator)?.isVisible = isLoading
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showLoadingState(view, false)
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()

                // Show default content on error
                displayDefaultGreetings()
            }
        }
    }

    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialCardView>(R.id.back_button_card)?.setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<MaterialButton>(R.id.back_to_home_button)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
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

        // Bottom navigation
        setupBottomNavigation(view)
    }

    private fun setupBottomNavigation(view: View) {
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.mainFragment)
                    true
                }
                R.id.nav_emergency -> {
                    navigateToEmergency()
                    true
                }
                R.id.nav_saved -> {
                    findNavController().navigate(R.id.savedFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun parseAndDisplayGreetings(content: String) {
        // Try to parse structured greeting data from content
        // This would ideally come from a structured API response

        if (content.contains("Zulu") || content.contains("Xhosa") || content.contains("Afrikaans")) {
            // For South Africa, display the default languages
            displayDefaultGreetings()
        } else {
            // Try to extract greeting information from the content
            extractGreetingsFromContent(content)
        }
    }

    private fun displayGreetingsFromExamples(examples: List<String>) {
        // Parse examples list to extract greeting information
        languagesContainer.removeAllViews()

        examples.forEach { example ->
            // Create a simple greeting card for each example
            if (example.contains("Hello") || example.contains("Goodbye") ||
                example.contains("greeting", ignoreCase = true)) {
                addSimpleGreetingCard(example)
            }
        }
    }

    private fun displayDefaultGreetings() {
        // Display default South African greetings as shown in the original XML
        val languages = listOf(
            GreetingLanguage(
                "Zulu", "isiZulu",
                "Sawubona / Sanibonani",
                "(to one person) / (to many) - 'I see you'",
                "Hamba kahle / Sala kahle",
                "(go well) / (stay well)",
                "sah-woo-BOH-nah"
            ),
            GreetingLanguage(
                "Xhosa", "isiXhosa",
                "Molo / Molweni",
                "(to one person) / (to many)",
                "Hamba kakuhle / Sala kakuhle",
                "(go well) / (stay well)",
                "MOH-loh"
            ),
            GreetingLanguage(
                "Afrikaans", "Afrikaans",
                "Hallo / Goeie môre",
                "Hello / Good morning",
                "Totsiens",
                "Until we meet again",
                "HAH-loh / HOO-yuh MORE-uh"
            )
        )

        greetingsData.clear()
        greetingsData.addAll(languages)

        languagesContainer.removeAllViews()
        languages.forEachIndexed { index, language ->
            addLanguageCard(language, index + 1)
        }
    }

    private fun addLanguageCard(language: GreetingLanguage, position: Int) {
        val cardView = LayoutInflater.from(context).inflate(
            R.layout.item_language_greeting_card,
            languagesContainer,
            false
        ) as MaterialCardView

        // Set up the card content
        cardView.apply {
            findViewById<TextView>(R.id.language_title)?.text = "$position. ${language.name} (${language.nativeName})"

            // Greeting section
            findViewById<TextView>(R.id.greeting_text)?.text = language.greeting
            findViewById<TextView>(R.id.greeting_translation)?.text = language.greetingTranslation

            // Goodbye section
            findViewById<TextView>(R.id.goodbye_text)?.text = language.goodbye
            findViewById<TextView>(R.id.goodbye_translation)?.text = language.goodbyeTranslation

            // Pronunciation hint
            language.pronunciation?.let {
                findViewById<TextView>(R.id.pronunciation_hint)?.apply {
                    text = "Pronunciation: $it"
                    isVisible = true
                }
            }

            // Play audio button for each phrase
            findViewById<ImageView>(R.id.play_greeting_audio)?.setOnClickListener {
                playGreetingAudio(language.greeting)
            }

            findViewById<ImageView>(R.id.play_goodbye_audio)?.setOnClickListener {
                playGreetingAudio(language.goodbye)
            }

            // Add animation
            val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            fadeIn.startOffset = (position * 100).toLong()
            startAnimation(fadeIn)
        }

        languagesContainer.addView(cardView)
    }

    private fun addSimpleGreetingCard(greeting: String) {
        val cardView = MaterialCardView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dpToPx())
            }
            radius = 20f.dpToPx().toFloat()
            cardElevation = 4f.dpToPx().toFloat()
            setCardBackgroundColor(requireContext().getColor(android.R.color.white))
        }

        val textView = TextView(requireContext()).apply {
            text = greeting
            setPadding(20.dpToPx(), 20.dpToPx(), 20.dpToPx(), 20.dpToPx())
            textSize = 14f
            setTextColor(requireContext().getColor(android.R.color.black))
        }

        cardView.addView(textView)
        languagesContainer.addView(cardView)
    }

    private fun extractGreetingsFromContent(content: String) {
        // Parse content to extract greetings
        // This is a fallback when structured data isn't available
        val lines = content.split("\n")
        var currentLanguage: String? = null

        lines.forEach { line ->
            when {
                line.contains("Hello", ignoreCase = true) ||
                        line.contains("Greeting", ignoreCase = true) -> {
                    addSimpleGreetingCard(line.trim())
                }
            }
        }
    }

    private fun showLoadingState(view: View, isLoading: Boolean) {
        view.findViewById<MaterialCardView>(R.id.loading_card)?.isVisible = isLoading
        languagesContainer.isVisible = !isLoading
    }

    private fun animateHeroCard(view: View) {
        val heroCard = view.findViewById<MaterialCardView>(R.id.hero_card)
        val slideIn = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        heroCard?.startAnimation(slideIn)
    }

    private fun playAudioPronunciation() {
        Toast.makeText(context, "Playing pronunciation guide...", Toast.LENGTH_SHORT).show()
        // Implement text-to-speech or audio playback
    }

    private fun playGreetingAudio(text: String) {
        Toast.makeText(context, "Playing: $text", Toast.LENGTH_SHORT).show()
        // Implement text-to-speech for specific greeting
    }

    private fun saveContentOffline() {
        // Save greetings data for offline use
        Toast.makeText(context, "Greetings saved for offline use", Toast.LENGTH_SHORT).show()
    }

    private fun startPracticeMode() {
        // Navigate to practice screen or show practice dialog
        Toast.makeText(context, "Starting practice mode...", Toast.LENGTH_SHORT).show()
    }

    private fun showTranslationOptions() {
        // Show language translation options
        Toast.makeText(context, "Translation options", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToEmergency() {
        val bundle = Bundle().apply {
            putString("countryId", arguments?.getString("countryId"))
            putString("countryName", currentCountryName)
        }
        findNavController().navigate(R.id.emergencyFragment, bundle)
    }

    private fun getDefaultGreetingDescription(): String {
        return """
            Because $currentCountryName has diverse cultures and languages, there are many different ways to greet people. 
            Learning these greetings shows respect for local culture and helps you connect with people in their native language. 
            
            Below are the most common greetings you'll encounter, along with their proper pronunciation and usage contexts.
        """.trimIndent()
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun Float.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }
}
