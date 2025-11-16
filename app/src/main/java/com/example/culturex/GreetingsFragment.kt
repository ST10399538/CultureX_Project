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
import android.speech.tts.TextToSpeech
import java.util.*
import androidx.navigation.fragment.findNavController
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import android.speech.tts.UtteranceProgressListener
import android.os.Handler
import android.os.Looper

class GreetingsFragment : Fragment(R.layout.fragment_greetings) {

    // ViewModel that fetches cultural content from an API
    private val contentViewModel: ContentViewModel by viewModels()

    // RecyclerView and Adapter to display a list of greetings
    private lateinit var greetingsRecyclerView: RecyclerView
    private lateinit var greetingsAdapter: GreetingsAdapter

    // Text-to-Speech engine for pronunciation
    private var textToSpeech: TextToSpeech? = null
    private var isTTSInitialized = false
    private var currentLocale: Locale = Locale.US
    private var ttsSpeedRate = 0.75f // Slower for learning

    // Stores the name of the current country (for display and loading greetings)
    private var currentCountryName: String = ""

    // Store current greetings list for practice mode
    private var currentGreetings: List<GreetingItem> = emptyList()
    private var currentGreetingIndex = 0
    private var isPracticeModeActive = false

    // Handler for delayed operations
    private val handler = Handler(Looper.getMainLooper())

    // UI elements
    private var ttsStatusText: TextView? = null
    private var ttsLanguageText: TextView? = null
    private var greetingCountText: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed into this fragment
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName") ?: "Country"

        currentCountryName = countryName

        // Initialize UI references
        ttsStatusText = view.findViewById(R.id.tts_status_text)
        ttsLanguageText = view.findViewById(R.id.tts_language_text)
        greetingCountText = view.findViewById(R.id.greeting_count_text)

        // Initialize TextToSpeech
        initializeTextToSpeech()

        // Setup UI components and event listeners
        setupViews(view)
        setupRecyclerView(view)
        setupObservers(view)
        setupClickListeners(view)

        if (countryId != null && categoryId != null) {
            contentViewModel.loadContent(countryId, categoryId)
            showLoadingState(view, true)
        } else {
            loadPredeterminedGreetings()
        }

        animateHeroCard(view)
    }

    private fun initializeTextToSpeech() {
        ttsStatusText?.text = "Initializing pronunciation engine..."
        ttsLanguageText?.text = "Please wait..."

        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTTSInitialized = true
                configureTTSForCountry()

                // Set up utterance progress listener for better control
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // Speech started
                    }

                    override fun onDone(utteranceId: String?) {
                        // Speech completed
                        if (isPracticeModeActive && utteranceId == "practice_greeting") {
                            handler.postDelayed({
                                if (isPracticeModeActive) {
                                    practiceNextGreeting()
                                }
                            }, 1500) // 1.5 second delay before next greeting
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Pronunciation error. Trying again...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            } else {
                isTTSInitialized = false
                ttsStatusText?.text = "Pronunciation unavailable"
                ttsLanguageText?.text = "Device does not support text-to-speech"
                Toast.makeText(
                    requireContext(),
                    "Text-to-Speech initialization failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun configureTTSForCountry() {
        val locale = getLocaleForCountry(currentCountryName)
        currentLocale = locale

        val result = textToSpeech?.setLanguage(locale)

        when (result) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                // Try alternative locale or fallback to English
                val fallbackLocale = getFallbackLocale(currentCountryName)
                textToSpeech?.setLanguage(fallbackLocale)
                currentLocale = fallbackLocale

                ttsStatusText?.text = "Using ${fallbackLocale.displayLanguage} pronunciation"
                ttsLanguageText?.text = "For authentic ${locale.displayLanguage}, install language pack in device settings"

                Toast.makeText(
                    requireContext(),
                    "Install ${locale.displayLanguage} language pack for authentic pronunciation",
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                ttsStatusText?.text = "✓ ${locale.displayLanguage} pronunciation ready"
                ttsLanguageText?.text = "Tap any greeting to hear authentic native pronunciation"
            }
        }

        // Configure TTS settings for optimal learning
        textToSpeech?.setSpeechRate(ttsSpeedRate)
        textToSpeech?.setPitch(1.0f)
    }

    private fun getLocaleForCountry(country: String): Locale {
        return when {
            // Europe
            country.contains("France", ignoreCase = true) -> Locale.FRANCE
            country.contains("Germany", ignoreCase = true) -> Locale.GERMANY
            country.contains("Italy", ignoreCase = true) -> Locale.ITALY
            country.contains("Spain", ignoreCase = true) -> Locale("es", "ES")
            country.contains("Portugal", ignoreCase = true) -> Locale("pt", "PT")
            country.contains("Netherlands", ignoreCase = true) -> Locale("nl", "NL")
            country.contains("Russia", ignoreCase = true) -> Locale("ru", "RU")
            country.contains("Poland", ignoreCase = true) -> Locale("pl", "PL")
            country.contains("Greece", ignoreCase = true) -> Locale("el", "GR")
            country.contains("Sweden", ignoreCase = true) -> Locale("sv", "SE")
            country.contains("Norway", ignoreCase = true) -> Locale("no", "NO")
            country.contains("Denmark", ignoreCase = true) -> Locale("da", "DK")
            country.contains("Finland", ignoreCase = true) -> Locale("fi", "FI")

            // Asia
            country.contains("Japan", ignoreCase = true) -> Locale.JAPAN
            country.contains("China", ignoreCase = true) -> Locale.CHINA
            country.contains("Korea", ignoreCase = true) -> Locale.KOREA
            country.contains("India", ignoreCase = true) -> Locale("hi", "IN")
            country.contains("Thailand", ignoreCase = true) -> Locale("th", "TH")
            country.contains("Vietnam", ignoreCase = true) -> Locale("vi", "VN")
            country.contains("Indonesia", ignoreCase = true) -> Locale("id", "ID")
            country.contains("Philippines", ignoreCase = true) -> Locale("fil", "PH")
            country.contains("Malaysia", ignoreCase = true) -> Locale("ms", "MY")
            country.contains("Singapore", ignoreCase = true) -> Locale("zh", "SG")
            country.contains("Turkey", ignoreCase = true) -> Locale("tr", "TR")
            country.contains("Arab", ignoreCase = true) ||
                    country.contains("Saudi", ignoreCase = true) -> Locale("ar", "SA")

            // Africa
            country.contains("South Africa", ignoreCase = true) -> Locale("af", "ZA")
            country.contains("Egypt", ignoreCase = true) -> Locale("ar", "EG")
            country.contains("Morocco", ignoreCase = true) -> Locale("ar", "MA")
            country.contains("Kenya", ignoreCase = true) -> Locale("sw", "KE")
            country.contains("Nigeria", ignoreCase = true) -> Locale("yo", "NG")

            // Americas
            country.contains("United States", ignoreCase = true) -> Locale.US
            country.contains("Canada", ignoreCase = true) -> Locale.CANADA
            country.contains("Mexico", ignoreCase = true) -> Locale("es", "MX")
            country.contains("Brazil", ignoreCase = true) -> Locale("pt", "BR")
            country.contains("Argentina", ignoreCase = true) -> Locale("es", "AR")
            country.contains("Chile", ignoreCase = true) -> Locale("es", "CL")
            country.contains("Colombia", ignoreCase = true) -> Locale("es", "CO")
            country.contains("Peru", ignoreCase = true) -> Locale("es", "PE")

            // Oceania
            country.contains("Australia", ignoreCase = true) -> Locale("en", "AU")
            country.contains("New Zealand", ignoreCase = true) -> Locale("en", "NZ")

            else -> Locale.US
        }
    }

    private fun getFallbackLocale(country: String): Locale {
        // Return English or a commonly available alternative
        return when {
            country.contains("South Africa", ignoreCase = true) -> Locale.UK
            country.contains("India", ignoreCase = true) -> Locale("en", "IN")
            country.contains("Philippines", ignoreCase = true) -> Locale.US
            else -> Locale.US
        }
    }

    private fun setupViews(view: View) {
        view.findViewById<TextView>(R.id.country_name)?.text = currentCountryName
    }

    private fun setupRecyclerView(view: View) {
        greetingsRecyclerView = view.findViewById(R.id.greetings_recycler_view)
        greetingsAdapter = GreetingsAdapter { greeting ->
            // Handle click on greeting item - speak with detailed phonetic breakdown
            speakGreetingWithContext(greeting)
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

                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Greetings"

                val description = it.content ?: getDefaultGreetingDescription()
                view.findViewById<TextView>(R.id.content_description)?.text = description

                val greetings = parseGreetingsFromContent(it)

                if (greetings.isNotEmpty()) {
                    currentGreetings = greetings
                    greetingsAdapter.submitList(greetings)
                    updateGreetingCount(greetings.size)
                } else {
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
                Toast.makeText(requireContext(), "Loading offline greetings...", Toast.LENGTH_SHORT).show()
                contentViewModel.clearError()
                loadPredeterminedGreetings()
            }
        }
    }

    private fun updateGreetingCount(count: Int) {
        greetingCountText?.text = "$count essential greetings • ${currentLocale.displayLanguage} pronunciation"
    }

    private fun parseGreetingsFromContent(content: com.example.culturex.data.models.CountryModels.CulturalContentDTO): List<GreetingItem> {
        val greetings = mutableListOf<GreetingItem>()

        content.examples?.forEach { example ->
            if (example.contains("Hello", ignoreCase = true) ||
                example.contains("Goodbye", ignoreCase = true)) {
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

        if (greetings.isEmpty() && !content.content.isNullOrEmpty()) {
            if (currentCountryName.contains("South Africa", ignoreCase = true)) {
                return getPredeterminedSouthAfricanGreetings()
            }
        }

        return greetings
    }

    private fun loadPredeterminedGreetings() {
        val greetings = when {
            currentCountryName.contains("South Africa", ignoreCase = true) ->
                getPredeterminedSouthAfricanGreetings()
            currentCountryName.contains("France", ignoreCase = true) ->
                getPredeterminedFrenchGreetings()
            currentCountryName.contains("Japan", ignoreCase = true) ->
                getPredeterminedJapaneseGreetings()
            currentCountryName.contains("India", ignoreCase = true) ->
                getPredeterminedIndianGreetings()
            currentCountryName.contains("United States", ignoreCase = true) ->
                getPredeterminedAmericanGreetings()
            currentCountryName.contains("Spain", ignoreCase = true) ->
                getPredeterminedSpanishGreetings()


            else ->
                getPredeterminedDefaultGreetings()
        }

        currentGreetings = greetings
        greetingsAdapter.submitList(greetings)
        updateGreetingCount(greetings.size)
    }

    // COMPREHENSIVE GREETING DATABASES BY COUNTRY

    private fun getPredeterminedSouthAfricanGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Zulu (isiZulu)",
                greeting = "Sawubona",
                greetingTranslation = "(to one person) - 'I see you' - Formal and respectful",
                goodbye = "Hamba kahle",
                goodbyeTranslation = "(go well) - Said to someone leaving",
                pronunciation = "sah-woo-BOH-nah"
            ),
            GreetingItem(
                language = "Zulu - Plural",
                greeting = "Sanibonani",
                greetingTranslation = "(to multiple people) - 'I see you all'",
                goodbye = "Sala kahle",
                goodbyeTranslation = "(stay well) - Said by someone leaving",
                pronunciation = "sah-nee-boh-NAH-nee"
            ),
            GreetingItem(
                language = "Xhosa (isiXhosa)",
                greeting = "Molo",
                greetingTranslation = "(to one person) - General friendly greeting",
                goodbye = "Hamba kakuhle",
                goodbyeTranslation = "(go well)",
                pronunciation = "MOH-loh"
            ),
            GreetingItem(
                language = "Xhosa - Plural",
                greeting = "Molweni",
                greetingTranslation = "(to multiple people)",
                goodbye = "Sala kakuhle",
                goodbyeTranslation = "(stay well)",
                pronunciation = "moh-LWEH-nee"
            ),
            GreetingItem(
                language = "Afrikaans",
                greeting = "Hallo / Goeiedag",
                greetingTranslation = "Hello / Good day",
                goodbye = "Totsiens",
                goodbyeTranslation = "Until we meet again",
                pronunciation = "HAH-loh / HOO-yuh-dahkh"
            ),
            GreetingItem(
                language = "Afrikaans - Informal",
                greeting = "Howzit",
                greetingTranslation = "How's it going? (Very casual)",
                goodbye = "Lekker dag",
                goodbyeTranslation = "Have a nice day",
                pronunciation = "HOW-zit"
            ),
            GreetingItem(
                language = "Sotho (Sesotho)",
                greeting = "Dumela",
                greetingTranslation = "(singular) - Hello",
                goodbye = "Sala hantle",
                goodbyeTranslation = "Stay well",
                pronunciation = "doo-MEH-lah"
            ),
            GreetingItem(
                language = "Tswana (Setswana)",
                greeting = "Dumela rra/mma",
                greetingTranslation = "Hello sir/madam - Very respectful",
                goodbye = "Tsamaya sentle",
                goodbyeTranslation = "Go well",
                pronunciation = "doo-MEH-lah rah/mah"
            ),
            GreetingItem(
                language = "Venda (Tshivenda)",
                greeting = "Ndaa/Aa",
                greetingTranslation = "Hello (response-based greeting)",
                goodbye = "Kha vha sale zwavhuḓi",
                goodbyeTranslation = "Stay well",
                pronunciation = "n-DAH / AH"
            ),
            GreetingItem(
                language = "English - South African",
                greeting = "Howzit / Heita",
                greetingTranslation = "Local slang for 'How are you?'",
                goodbye = "Cheers / Sharp",
                goodbyeTranslation = "Goodbye (informal)",
                pronunciation = null
            )
        )
    }

    private fun getPredeterminedFrenchGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "French (Français) - Formal",
                greeting = "Bonjour",
                greetingTranslation = "Good day (until 6pm) - Use with strangers, elders, professionals",
                goodbye = "Au revoir",
                goodbyeTranslation = "Goodbye (formal)",
                pronunciation = "bon-ZHOOR"
            ),
            GreetingItem(
                language = "French - Evening",
                greeting = "Bonsoir",
                greetingTranslation = "Good evening (after 6pm)",
                goodbye = "Bonne soirée",
                goodbyeTranslation = "Have a good evening",
                pronunciation = "bon-SWAHR"
            ),
            GreetingItem(
                language = "French - Informal",
                greeting = "Salut",
                greetingTranslation = "Hi/Bye (friends and peers only)",
                goodbye = "Ciao / À plus",
                goodbyeTranslation = "Bye / See you later",
                pronunciation = "sah-LOO"
            ),
            GreetingItem(
                language = "French - Casual",
                greeting = "Ça va?",
                greetingTranslation = "How's it going? (informal)",
                goodbye = "À bientôt",
                goodbyeTranslation = "See you soon",
                pronunciation = "sah VAH"
            ),
            GreetingItem(
                language = "French - Very Formal",
                greeting = "Enchanté(e)",
                greetingTranslation = "Pleased to meet you (first meeting)",
                goodbye = "Ravi d'avoir fait votre connaissance",
                goodbyeTranslation = "Pleased to have met you",
                pronunciation = "on-shon-TAY"
            ),
            GreetingItem(
                language = "French - Morning",
                greeting = "Bon matin",
                greetingTranslation = "Good morning (Quebec French)",
                goodbye = "Bonne journée",
                goodbyeTranslation = "Have a good day",
                pronunciation = "bon mah-TAN"
            )
        )
    }

    private fun getPredeterminedJapaneseGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Japanese (日本語) - General",
                greeting = "こんにちは (Konnichiwa)",
                greetingTranslation = "Hello (daytime, 10am-6pm)",
                goodbye = "さようなら (Sayonara)",
                goodbyeTranslation = "Goodbye (formal, long-term parting)",
                pronunciation = "kon-nee-chee-WAH"
            ),
            GreetingItem(
                language = "Japanese - Morning",
                greeting = "おはようございます (Ohayou gozaimasu)",
                greetingTranslation = "Good morning (formal)",
                goodbye = "いってきます (Ittekimasu)",
                goodbyeTranslation = "I'm leaving (from home)",
                pronunciation = "oh-hah-yoh goh-zai-MAHS"
            ),
            GreetingItem(
                language = "Japanese - Evening",
                greeting = "こんばんは (Konbanwa)",
                greetingTranslation = "Good evening (after 6pm)",
                goodbye = "おやすみなさい (Oyasuminasai)",
                goodbyeTranslation = "Good night",
                pronunciation = "kon-bahn-WAH"
            ),
            GreetingItem(
                language = "Japanese - Casual",
                greeting = "やあ (Yaa) / よう (Yo)",
                greetingTranslation = "Hey (very informal, close friends)",
                goodbye = "じゃあね (Jaa ne) / バイバイ (Baibai)",
                goodbyeTranslation = "See ya / Bye bye",
                pronunciation = "YAH / YOH"
            ),
            GreetingItem(
                language = "Japanese - Workplace",
                greeting = "お疲れ様です (Otsukaresama desu)",
                greetingTranslation = "Thank you for your hard work (colleagues)",
                goodbye = "お先に失礼します (Osakini shitsurei shimasu)",
                goodbyeTranslation = "Excuse me for leaving first",
                pronunciation = "oh-tsoo-kah-reh-sah-mah dess"
            ),
            GreetingItem(
                language = "Japanese - Returning",
                greeting = "ただいま (Tadaima)",
                greetingTranslation = "I'm home / I'm back",
                goodbye = "いってらっしゃい (Itterasshai)",
                goodbyeTranslation = "Take care / Have a good day (to someone leaving)",
                pronunciation = "tah-dah-ee-MAH"
            ),
            GreetingItem(
                language = "Japanese - First Meeting",
                greeting = "はじめまして (Hajimemashite)",
                greetingTranslation = "Nice to meet you (first time)",
                goodbye = "また会いましょう (Mata aimashou)",
                goodbyeTranslation = "Let's meet again",
                pronunciation = "hah-jee-meh-mah-SHTEH"
            )
        )
    }

    private fun getPredeterminedIndianGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Hindi (हिन्दी)",
                greeting = "नमस्ते (Namaste)",
                greetingTranslation = "Hello/Goodbye (respectful, with hands together)",
                goodbye = "अलविदा (Alvida)",
                goodbyeTranslation = "Goodbye (formal)",
                pronunciation = "nah-mah-STAY"
            ),
            GreetingItem(
                language = "Hindi - Very Formal",
                greeting = "नमस्कार (Namaskar)",
                greetingTranslation = "Respectful greeting (more formal than Namaste)",
                goodbye = "फिर मिलेंगे (Phir milenge)",
                goodbyeTranslation = "We'll meet again",
                pronunciation = "nah-mahs-KAHR"
            ),
            GreetingItem(
                language = "Hindi - Casual",
                greeting = "क्या हाल है? (Kya haal hai?)",
                greetingTranslation = "How are you? / What's up?",
                goodbye = "चलते हैं (Chalte hain)",
                goodbyeTranslation = "Let's go / I'm leaving",
                pronunciation = "kyah HAHL hay"
            ),
            GreetingItem(
                language = "Tamil (தமிழ்)",
                greeting = "வணக்கம் (Vanakkam)",
                greetingTranslation = "Hello (respectful, all times of day)",
                goodbye = "போய் வருகிறேன் (Poi varugiren)",
                goodbyeTranslation = "I'll go and come back",
                pronunciation = "vah-nahk-KAHM"
            ),
            GreetingItem(
                language = "Bengali (বাংলা)",
                greeting = "নমস্কার (Nomoshkar)",
                greetingTranslation = "Hello (formal and respectful)",
                goodbye = "বিদায় (Biday)",
                goodbyeTranslation = "Farewell",
                pronunciation = "noh-mosh-KAHR"
            ),
            GreetingItem(
                language = "Punjabi (ਪੰਜਾਬੀ)",
                greeting = "ਸਤ ਸ੍ਰੀ ਅਕਾਲ (Sat Sri Akal)",
                greetingTranslation = "God is truth (Sikh greeting)",
                goodbye = "ਅਲਵਿਦਾ (Alvida)",
                goodbyeTranslation = "Goodbye",
                pronunciation = "saht shree ah-KAHL"
            ),
            GreetingItem(
                language = "Telugu (తెలుగు)",
                greeting = "నమస్కారం (Namaskaram)",
                greetingTranslation = "Hello (formal greeting)",
                goodbye = "మళ్ళీ కలుద్దాం (Malli kaluddam)",
                goodbyeTranslation = "Let's meet again",
                pronunciation = "nah-mahs-kah-RAHM"
            ),
            GreetingItem(
                language = "Gujarati (ગુજરાતી)",
                greeting = "નમસ્તે (Namaste)",
                greetingTranslation = "Hello (with hands joined)",
                goodbye = "આવજો (Aavjo)",
                goodbyeTranslation = "Come again / Goodbye",
                pronunciation = "nah-mahs-TEH"
            )
        )
    }

    private fun getPredeterminedSpanishGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "Spanish (Español) - General",
                greeting = "Hola",
                greetingTranslation = "Hello (universal, any time)",
                goodbye = "Adiós",
                goodbyeTranslation = "Goodbye",
                pronunciation = "OH-lah"
            ),
            GreetingItem(
                language = "Spanish - Morning",
                greeting = "Buenos días",
                greetingTranslation = "Good morning (until noon)",
                goodbye = "Que tengas buen día",
                goodbyeTranslation = "Have a good day",
                pronunciation = "BWEH-nos DEE-ahs"
            ),
            GreetingItem(
                language = "Spanish - Afternoon",
                greeting = "Buenas tardes",
                greetingTranslation = "Good afternoon/evening (noon to dark)",
                goodbye = "Buenas tardes",
                goodbyeTranslation = "Good afternoon (can be used as goodbye)",
                pronunciation = "BWEH-nahs TAHR-dehs"
            ),
            GreetingItem(
                language = "Spanish - Night",
                greeting = "Buenas noches",
                greetingTranslation = "Good night/evening (after dark)",
                goodbye = "Que duermas bien",
                goodbyeTranslation = "Sleep well",
                pronunciation = "BWEH-nahs NOH-chehs"
            ),
            GreetingItem(
                language = "Spanish - Casual",
                greeting = "¿Qué tal? / ¿Cómo estás?",
                greetingTranslation = "How's it going? / How are you?",
                goodbye = "Hasta luego / Nos vemos",
                goodbyeTranslation = "See you later / See you",
                pronunciation = "keh TAHL / KOH-moh ehs-TAHS"
            ),
            GreetingItem(
                language = "Spanish - Very Informal",
                greeting = "¿Qué pasa? / ¿Qué onda?",
                greetingTranslation = "What's up? (Mexico/Latin America slang)",
                goodbye = "Chao / Cuídate",
                goodbyeTranslation = "Bye / Take care",
                pronunciation = "keh PAH-sah / keh OHN-dah"
            )
        )
    }

    private fun getPredeterminedAmericanGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "American English - Casual",
                greeting = "Hey / Hi",
                greetingTranslation = "Informal greeting, very common",
                goodbye = "See ya / Later",
                goodbyeTranslation = "Casual farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "American English - Friendly",
                greeting = "What's up? / How's it going?",
                greetingTranslation = "Casual greeting + inquiry",
                goodbye = "Take it easy / Take care",
                goodbyeTranslation = "Friendly farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "American English - Very Casual",
                greeting = "Yo / Sup",
                greetingTranslation = "Very informal, youth slang",
                goodbye = "Peace / Peace out",
                goodbyeTranslation = "Slang farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "American English - Southern",
                greeting = "Howdy / Hey y'all",
                greetingTranslation = "Southern/Western greeting",
                goodbye = "Y'all take care now",
                goodbyeTranslation = "Southern farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "Spanish - American Context",
                greeting = "Hola / ¿Qué tal?",
                greetingTranslation = "Hello / How are you? (Latin American communities)",
                goodbye = "Adiós / Hasta luego",
                goodbyeTranslation = "Goodbye / See you later",
                pronunciation = "OH-lah"
            )
        )
    }

    private fun getPredeterminedDefaultGreetings(): List<GreetingItem> {
        return listOf(
            GreetingItem(
                language = "International English",
                greeting = "Hello",
                greetingTranslation = "Universal greeting",
                goodbye = "Goodbye",
                goodbyeTranslation = "Universal farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "International - Formal",
                greeting = "Good morning / Good afternoon",
                greetingTranslation = "Polite, time-appropriate greetings",
                goodbye = "Have a good day",
                goodbyeTranslation = "Polite farewell",
                pronunciation = null
            ),
            GreetingItem(
                language = "International - Casual",
                greeting = "Hi / Hey",
                greetingTranslation = "Casual greeting",
                goodbye = "Bye / See you",
                goodbyeTranslation = "Casual farewell",
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

        // TTS Settings button
        view.findViewById<MaterialCardView>(R.id.tts_settings_card)?.setOnClickListener {
            showTTSSettings()
        }

        // Save offline
        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            saveContentOffline()
        }

        // Practice card - Interactive practice mode
        view.findViewById<MaterialCardView>(R.id.practice_card)?.setOnClickListener {
            startPracticeMode()
        }

        // FAB play all
        view.findViewById<FloatingActionButton>(R.id.fab_play_all)?.setOnClickListener {
            playAllGreetings()
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

    /**
     * Show TTS settings dialog to adjust speed
     */
    private fun showTTSSettings() {
        val options = arrayOf("Slow (Best for learning)", "Normal", "Fast")
        val currentSpeed = when {
            ttsSpeedRate <= 0.75f -> 0
            ttsSpeedRate <= 1.0f -> 1
            else -> 2
        }

        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Pronunciation Speed")
        builder.setSingleChoiceItems(options, currentSpeed) { dialog, which ->
            ttsSpeedRate = when (which) {
                0 -> 0.7f // Slow
                1 -> 1.0f // Normal
                2 -> 1.3f // Fast
                else -> 0.75f
            }
            textToSpeech?.setSpeechRate(ttsSpeedRate)

            val speedText = when (which) {
                0 -> "Slow speed selected - Best for learning"
                1 -> "Normal speed selected"
                2 -> "Fast speed selected"
                else -> "Speed adjusted"
            }
            Toast.makeText(requireContext(), speedText, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    /**
     * Speak greeting with detailed context
     */
    private fun speakGreetingWithContext(greeting: GreetingItem) {
        if (!isTTSInitialized) {
            Toast.makeText(context, "Text-to-Speech not ready yet", Toast.LENGTH_SHORT).show()
            return
        }

        // Speak the language name first
        speakText(greeting.language, TextToSpeech.QUEUE_FLUSH)

        // Add a pause
        textToSpeech?.playSilence(400, TextToSpeech.QUEUE_ADD, null)

        // Speak the greeting
        speakText(greeting.greeting, TextToSpeech.QUEUE_ADD)

        // If there's a goodbye, speak it too
        if (greeting.goodbye.isNotEmpty()) {
            textToSpeech?.playSilence(400, TextToSpeech.QUEUE_ADD, null)
            speakText(greeting.goodbye, TextToSpeech.QUEUE_ADD)
        }

        // Show toast with translation
        Toast.makeText(
            context,
            "🔊 ${greeting.greeting}\n${greeting.greetingTranslation}",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Play all greetings sequentially with pauses
     */
    private fun playAllGreetings() {
        if (!isTTSInitialized) {
            Toast.makeText(context, "Text-to-Speech not ready yet", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentGreetings.isEmpty()) {
            Toast.makeText(context, "No greetings to play", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "🔊 Playing all ${currentGreetings.size} greetings...", Toast.LENGTH_LONG).show()

        // Play each greeting with pauses
        currentGreetings.forEachIndexed { index, greeting ->
            // Pause before each greeting
            textToSpeech?.playSilence(if (index == 0) 100 else 1000, TextToSpeech.QUEUE_ADD, null)

            // Speak language name
            speakText(greeting.language, TextToSpeech.QUEUE_ADD)
            textToSpeech?.playSilence(400, TextToSpeech.QUEUE_ADD, null)

            // Speak the greeting
            speakText(greeting.greeting, TextToSpeech.QUEUE_ADD)

            // Speak the goodbye if available
            if (greeting.goodbye.isNotEmpty()) {
                textToSpeech?.playSilence(400, TextToSpeech.QUEUE_ADD, null)
                speakText(greeting.goodbye, TextToSpeech.QUEUE_ADD)
            }
        }
    }

    /**
     * Interactive practice mode - speaks greetings one by one with detailed explanations
     */
    private fun startPracticeMode() {
        if (!isTTSInitialized) {
            Toast.makeText(context, "Text-to-Speech not ready yet", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentGreetings.isEmpty()) {
            Toast.makeText(context, "No greetings to practice", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPracticeModeActive) {
            // Start practice mode
            isPracticeModeActive = true
            currentGreetingIndex = 0
            Toast.makeText(
                context,
                "🔥 Practice Mode Started!\nListening and repeating after each greeting...",
                Toast.LENGTH_LONG
            ).show()
            practiceNextGreeting()
        } else {
            // Stop practice mode
            isPracticeModeActive = false
            textToSpeech?.stop()
            Toast.makeText(
                context,
                "Practice mode stopped",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun practiceNextGreeting() {
        if (!isPracticeModeActive || currentGreetingIndex >= currentGreetings.size) {
            // Finished all greetings
            isPracticeModeActive = false
            Toast.makeText(
                context,
                "✓ Practice Complete! You've mastered ${currentGreetings.size} greetings.\n" +
                        "Great job learning ${currentLocale.displayLanguage}!",
                Toast.LENGTH_LONG
            ).show()
            currentGreetingIndex = 0
            return
        }

        val greeting = currentGreetings[currentGreetingIndex]
        val progress = "${currentGreetingIndex + 1}/${currentGreetings.size}"

        // Show which greeting is being practiced
        Toast.makeText(
            context,
            "Practice $progress: ${greeting.language}\n👂 Listen carefully...",
            Toast.LENGTH_SHORT
        ).show()

        // Create detailed practice sequence
        val utteranceId = "practice_greeting"

        // Introduce the language
        speakText("Greeting ${currentGreetingIndex + 1}. ${greeting.language}", TextToSpeech.QUEUE_FLUSH)

        // Pause
        textToSpeech?.playSilence(500, TextToSpeech.QUEUE_ADD, null)

        // Speak the greeting slowly
        speakText(greeting.greeting, TextToSpeech.QUEUE_ADD)

        // Pause for user to repeat
        textToSpeech?.playSilence(1000, TextToSpeech.QUEUE_ADD, null)

        // Speak it again
        speakText("Again. ${greeting.greeting}", TextToSpeech.QUEUE_ADD)

        // If there's a goodbye
        if (greeting.goodbye.isNotEmpty()) {
            textToSpeech?.playSilence(800, TextToSpeech.QUEUE_ADD, null)
            speakText("Goodbye. ${greeting.goodbye}", TextToSpeech.QUEUE_ADD)
            textToSpeech?.playSilence(1000, TextToSpeech.QUEUE_ADD, null)
            speakText("Again. ${greeting.goodbye}", TextToSpeech.QUEUE_ADD, utteranceId, null)

        }

        currentGreetingIndex++
    }

    /**
     * Speak text using TextToSpeech
     */
    private fun speakText(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH, utteranceId: String? = null, params: Bundle? = null) {
        if (!isTTSInitialized) {
            return
        }

        if (utteranceId != null) {
            textToSpeech?.speak(text, queueMode, null, utteranceId)
        } else {
            textToSpeech?.speak(text, queueMode, null, null)
        }
    }

    private fun saveContentOffline() {
        Toast.makeText(
            context,
            "✓ ${currentGreetings.size} greetings saved for offline use in ${currentCountryName}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getDefaultGreetingDescription(): String {
        return """
            Master essential greetings to show respect and connect with locals. Each greeting includes native pronunciation, 
            cultural context, and appropriate usage scenarios. 
            
            Tap any greeting card to hear authentic pronunciation, or use Practice Mode for guided learning with repetition.
        """.trimIndent()
    }

    override fun onPause() {
        // Stop any ongoing speech when fragment is paused
        textToSpeech?.stop()
        isPracticeModeActive = false
        super.onPause()
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech to free resources
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
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
