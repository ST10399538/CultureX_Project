package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.culturex.data.viewmodels.ContentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class EtiquetteFragment : Fragment(R.layout.fragment_etiquette) {

    // ViewModel to fetch and hold content data
    private val contentViewModel: ContentViewModel by viewModels()

    // Current country information
    private var currentCountryId: String? = null
    private var currentCountryName: String = ""

    // Views for displaying description and key points
    private lateinit var contentDescriptionView: TextView
    private lateinit var keyPointsContentView: TextView

    // Called when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from navigation
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName")
// Save country info for later use
        currentCountryId = countryId
        currentCountryName = countryName ?: "Country"
        contentDescriptionView = view.findViewById(R.id.content_description)
        keyPointsContentView = view.findViewById(R.id.key_points_content)

        // If both country and category are available, load content from ViewModel
        if (countryId != null && categoryId != null) {
            contentViewModel.loadContent(countryId, categoryId)
            setupObservers(view)
        }

        // Display country name in the UI if available
        countryName?.let {
            view.findViewById<TextView>(R.id.country_name)?.text = it
        }

        // Setup click listeners for various UI elements
        setupClickListeners(view)
        setupChipClickListeners(view)
    }

    // Setup listeners for etiquette type chips (Formal, Business, Social)
    private fun setupChipClickListeners(view: View) {
        val chipFormal = view.findViewById<Chip>(R.id.chip_formal)
        val chipBusiness = view.findViewById<Chip>(R.id.chip_business)
        val chipSocial = view.findViewById<Chip>(R.id.chip_social)

        chipFormal?.setOnClickListener {
            updateContentForEtiquetteType("formal")
            resetOtherChips(chipFormal, chipBusiness, chipSocial)
            chipFormal.isChecked = true
        }

        chipBusiness?.setOnClickListener {
            updateContentForEtiquetteType("business")
            resetOtherChips(chipBusiness, chipFormal, chipSocial)
            chipBusiness.isChecked = true
        }

        chipSocial?.setOnClickListener {
            updateContentForEtiquetteType("social")
            resetOtherChips(chipSocial, chipFormal, chipBusiness)
            chipSocial.isChecked = true
        }
    }

    // Deselect all other chips except the selected one
    private fun resetOtherChips(selected: Chip, vararg others: Chip?) {
        others.forEach { it?.isChecked = false }
    }

    // Update the etiquette content based on the type (Formal, Business, Social)
    private fun updateContentForEtiquetteType(type: String) {
        val countryId = currentCountryId ?: return

        // Update UI with description and key points
        val etiquetteData = getEtiquetteData(currentCountryName, type)
        contentDescriptionView.text = etiquetteData.description
        keyPointsContentView.text = etiquetteData.keyPoints
    }

    // Returns etiquette data for a given country and etiquette type
    private fun getEtiquetteData(countryName: String, type: String): EtiquetteData {
        return when {
            countryName.contains("France", ignoreCase = true) -> getFranceEtiquette(type)
            countryName.contains("South Africa", ignoreCase = true) -> getSouthAfricaEtiquette(type)
            countryName.contains("Japan", ignoreCase = true) -> getJapanEtiquette(type)
            countryName.contains("India", ignoreCase = true) -> getIndiaEtiquette(type)
            countryName.contains("United States", ignoreCase = true) ||
                    countryName.contains("USA", ignoreCase = true) -> getUSAEtiquette(type)
            else -> getDefaultEtiquette(type)
        }
    }

    private fun getFranceEtiquette(type: String): EtiquetteData {
        return when (type) {
            "formal" -> EtiquetteData(
                description = """
                    French formal etiquette emphasizes elegance, respect, and adherence to protocol. The French value sophistication and proper manners in formal settings.
                    
                    Greetings: Use "Monsieur" or "Madame" followed by the last name. Wait to be invited to use first names.
                    
                    Dress Code: Dress formally and elegantly. The French appreciate well-tailored, classic clothing.
                    
                    Conversation: Avoid personal questions initially. Discuss art, culture, and philosophy, but avoid talking about money or religion.
                    
                    Table Manners: Keep both hands visible on the table (not in your lap). Wait for the host to begin eating.
                """.trimIndent(),
                keyPoints = """
                    • Always greet with "Bonjour" or "Bonsoir"
                    • Use formal titles until invited otherwise
                    • Maintain elegant posture and demeanor
                    • Punctuality is highly valued
                    • Keep hands on the table, never in lap
                    • Wait for host to start eating and offer toasts
                """.trimIndent()
            )
            "business" -> EtiquetteData(
                description = """
                    French business culture values intellect, debate, and formal hierarchy. Building personal relationships is important before closing deals.
                    
                    Meetings: Expect formality and structure. Presentations should be well-prepared and logical.
                    
                    Communication: The French appreciate debate and intellectual discussion. Don't take disagreement personally.
                    
                    Dress Code: Conservative, high-quality business attire is essential. Image matters significantly.
                    
                    Negotiations: Be prepared for lengthy discussions. Decisions follow hierarchy and may take time.
                """.trimIndent(),
                keyPoints = """
                    • Schedule meetings well in advance
                    • Bring business cards in French and English
                    • Use formal titles and last names
                    • Be prepared for intellectual debate
                    • Long lunch meetings are common (2-3 hours)
                    • Hierarchy is important - address senior members first
                """.trimIndent()
            )
            "social" -> EtiquetteData(
                description = """
                    French social etiquette emphasizes politeness, conversation, and enjoying life's pleasures. The French value meaningful discussions and cultural appreciation.
                    
                    Greetings: The "bise" (cheek kiss) is common among friends, typically 2-4 kisses depending on region.
                    
                    Conversation: Engage in stimulating discussion about culture, food, and current events. Avoid loud or boisterous behavior.
                    
                    Dining: Meals are social events to be savored. Don't rush through courses.
                    
                    Gift-Giving: Bring flowers (not chrysanthemums), wine, or chocolates when invited to a home.
                """.trimIndent(),
                keyPoints = """
                    • Always say "Bonjour" when entering shops
                    • Learn basic French phrases - effort is appreciated
                    • Keep your voice at moderate volume
                    • Savor meals - eating is a social pleasure
                    • Don't discuss money or ask about salaries
                    • Wine appreciation is culturally important
                """.trimIndent()
            )
            else -> getDefaultEtiquette(type)
        }
    }

    private fun getSouthAfricaEtiquette(type: String): EtiquetteData {
        return when (type) {
            "formal" -> EtiquetteData(
                description = """
                    South African formal etiquette reflects the country's diverse cultural heritage, combining African, European, and Asian influences with modern professionalism.
                    
                    Greetings: Use titles and surnames initially. A firm handshake is standard across all cultures.
                    
                    Dress Code: Business formal or smart casual depending on the occasion. Standards vary by region and industry.
                    
                    Respect: Show respect for South Africa's diverse cultures and languages. Acknowledge the country's history sensitively.
                    
                    Protocol: Follow Western formal etiquette with awareness of cultural diversity.
                """.trimIndent(),
                keyPoints = """
                    • Use appropriate titles and surnames
                    • Firm handshakes are standard
                    • Respect cultural diversity
                    • Punctuality is expected in formal settings
                    • Be aware of South Africa's 11 official languages
                    • Show cultural sensitivity regarding apartheid history
                """.trimIndent()
            )
            "business" -> EtiquetteData(
                description = """
                    South African business culture is relationship-oriented and relatively informal, though professional standards are maintained.
                    
                    Meetings: While punctuality is valued, meetings may not always start on time. Build rapport before diving into business.
                    
                    Communication: Direct but friendly communication is appreciated. South Africans value honesty and straightforwardness.
                    
                    Diversity: Be respectful of the multicultural workforce. English is commonly used in business.
                    
                    Negotiations: Decisions may involve consultation across various levels. Be patient with the process.
                """.trimIndent(),
                keyPoints = """
                    • Build personal relationships first
                    • Exchange business cards with both hands
                    • Be direct but friendly in communication
                    • Respect the rainbow nation's diversity
                    • Don't rush negotiations
                    • Coffee meetings are common for initial discussions
                """.trimIndent()
            )
            "social" -> EtiquetteData(
                description = """
                    South African social culture is warm, friendly, and diverse. The concept of "Ubuntu" (humanity towards others) is central to social interactions.
                    
                    Greetings: Greetings are important. Take time to ask "How are you?" and expect a genuine response.
                    
                    Hospitality: South Africans are known for their hospitality. Expect generous entertainment.
                    
                    Braai Culture: The "braai" (barbecue) is a key social event. If invited, offer to bring something.
                    
                    Conversation: South Africans enjoy discussion about sports (especially rugby and cricket), nature, and their diverse culture.
                """.trimIndent(),
                keyPoints = """
                    • Take time with greetings - they matter
                    • Embrace the concept of "Ubuntu"
                    • Braais (BBQs) are important social events
                    • Sport is a major conversation topic
                    • Tipping 10-15% is standard in restaurants
                    • Respect all cultures and languages equally
                """.trimIndent()
            )
            else -> getDefaultEtiquette(type)
        }
    }

    private fun getJapanEtiquette(type: String): EtiquetteData {
        return when (type) {
            "formal" -> EtiquetteData(
                description = """
                    Japanese formal etiquette is deeply rooted in respect, hierarchy, and centuries of tradition. Every gesture has meaning and purpose.
                    
                    Greetings: Bow appropriately - depth indicates respect level. Exchange business cards (meishi) with both hands.
                    
                    Respect: Show deference to elders and superiors. Use appropriate honorific language (keigo).
                    
                    Behavior: Maintain composure and avoid emotional displays. Silence is valued and comfortable.
                    
                    Gift-Giving: Formal gift-giving (temiyage) follows specific protocols. Presentation matters as much as the gift.
                """.trimIndent(),
                keyPoints = """
                    • Bow when greeting - depth shows respect level
                    • Remove shoes when entering homes
                    • Present and receive items with both hands
                    • Never point with chopsticks
                    • Avoid direct refusal - use indirect language
                    • Respect silence - it's not awkward
                """.trimIndent()
            )
            "business" -> EtiquetteData(
                description = """
                    Japanese business culture emphasizes group harmony (wa), consensus decision-making, and long-term relationships over quick profits.
                    
                    Meetings: Arrive early. The most senior person speaks first. Expect formal introductions and business card exchange rituals.
                    
                    Communication: Indirect communication is preferred. Reading between the lines is essential. "Yes" may mean "I understand," not "I agree."
                    
                    Decisions: Made through consensus (ringi system). This takes time but ensures buy-in.
                    
                    Relationships: Business relationships are built over time through after-work socializing (nomikai).
                """.trimIndent(),
                keyPoints = """
                    • Exchange business cards (meishi) ceremonially
                    • Never write on someone's business card
                    • Punctuality is absolutely critical
                    • Consensus is more important than speed
                    • After-work drinking builds relationships
                    • Dress conservatively - dark suits are standard
                """.trimIndent()
            )
            "social" -> EtiquetteData(
                description = """
                    Japanese social etiquette balances tradition with modern life, emphasizing harmony, consideration for others, and reading social cues.
                    
                    Public Behavior: Maintain quiet in public spaces. Avoid phone calls on trains. Don't eat while walking.
                    
                    Dining: Say "itadakimasu" before eating and "gochisosama" after. Pour drinks for others, never yourself.
                    
                    Bathing: Wash thoroughly before entering communal baths (onsen). Tattoos may not be allowed.
                    
                    Gift-Giving: Small gifts (omiyage) when visiting. Refuse gifts twice before accepting (traditionally).
                """.trimIndent(),
                keyPoints = """
                    • Bow when greeting friends (less formal than business)
                    • Remove shoes at entrances
                    • Don't tip - it can be insulting
                    • Pour drinks for others, not yourself
                    • Say "itadakimasu" before eating
                    • Keep phone on silent in public
                """.trimIndent()
            )
            else -> getDefaultEtiquette(type)
        }
    }

    private fun getIndiaEtiquette(type: String): EtiquetteData {
        return when (type) {
            "formal" -> EtiquetteData(
                description = """
                    Indian formal etiquette combines ancient traditions with modern practices, varying significantly across regions, religions, and communities.
                    
                    Greetings: The "namaste" (hands together, slight bow) is respectful. Handshakes are common in business but check cultural cues.
                    
                    Respect: Show particular respect to elders. Touching feet of elders is a sign of deep respect in some communities.
                    
                    Dress: Modest, conservative dress is important, especially in religious settings. Women should cover shoulders and knees.
                    
                    Hierarchy: Respect for seniority and status is deeply ingrained. Address people by titles and surnames.
                """.trimIndent(),
                keyPoints = """
                    • Greet with "Namaste" - hands together
                    • Respect elders and hierarchy
                    • Dress modestly, especially at religious sites
                    • Remove shoes when entering homes and temples
                    • Avoid public displays of affection
                    • Use right hand for giving and receiving
                """.trimIndent()
            )
            "business" -> EtiquetteData(
                description = """
                    Indian business culture is relationship-focused, hierarchical, and increasingly globalized while maintaining traditional values.
                    
                    Meetings: Expect some flexibility with time. Building personal rapport precedes business discussions.
                    
                    Communication: Indians may nod or say "yes" to be polite, not necessarily in agreement. Follow up in writing.
                    
                    Negotiations: Expect detailed negotiations. Indians are skilled negotiators who value getting good deals.
                    
                    Hierarchy: Decisions are made at the top. Show respect to senior members and wait for their input.
                """.trimIndent(),
                keyPoints = """
                    • Build personal relationships first
                    • Respect hierarchical decision-making
                    • Be patient - "Indian Standard Time" may apply
                    • Use titles and surnames
                    • Expect extensive negotiations
                    • Tea/chai is offered frequently - accept graciously
                """.trimIndent()
            )
            "social" -> EtiquetteData(
                description = """
                    Indian social etiquette is warm, hospitable, and deeply influenced by diverse religious and cultural traditions across the country.
                    
                    Hospitality: Indian hospitality is legendary. Guests are treated like gods ("Atithi Devo Bhava"). Expect generous food offerings.
                    
                    Food Culture: Many Indians are vegetarian. Always ask about dietary restrictions. Use right hand for eating.
                    
                    Family: Family is central to Indian culture. Expect questions about family, marriage, and children.
                    
                    Festivals: India has numerous religious and cultural festivals. Participation is welcomed and appreciated.
                """.trimIndent(),
                keyPoints = """
                    • Accept hospitality graciously
                    • Use right hand for eating and greeting
                    • Respect diverse religious practices
                    • Ask about dietary restrictions (many vegetarians)
                    • Family questions are normal and friendly
                    • Remove shoes before entering homes
                """.trimIndent()
            )
            else -> getDefaultEtiquette(type)
        }
    }

    private fun getUSAEtiquette(type: String): EtiquetteData {
        return when (type) {
            "formal" -> EtiquetteData(
                description = """
                    American formal etiquette tends to be less rigid than in many other countries, but still maintains standards of respect and professionalism.
                    
                    Greetings: Firm handshake with direct eye contact. Americans quickly move to first-name basis.
                    
                    Punctuality: Being on time is very important. Arriving late is considered disrespectful.
                    
                    Communication: Direct communication is valued. Americans appreciate straightforward, honest conversation.
                    
                    Personal Space: Americans maintain more personal space than many cultures. Respect boundaries.
                """.trimIndent(),
                keyPoints = """
                    • Firm handshake with eye contact
                    • Punctuality is highly valued
                    • Use first names quickly after introduction
                    • Direct eye contact shows confidence
                    • Stand about arm's length apart
                    • "Please" and "thank you" are important
                """.trimIndent()
            )
            "business" -> EtiquetteData(
                description = """
                    American business culture values efficiency, directness, and results. Time is money, and business moves quickly.
                    
                    Meetings: Start and end on time. Agendas are followed strictly. Get to the point quickly.
                    
                    Communication: Direct and explicit communication is expected. Americans say what they mean and mean what they say.
                    
                    Informality: Business culture is relatively informal. First names are used quickly, even with executives.
                    
                    Negotiations: Americans expect win-win outcomes and may become impatient with lengthy negotiations.
                """.trimIndent(),
                keyPoints = """
                    • Time is money - be punctual and efficient
                    • Direct communication is expected
                    • First names used quickly in business
                    • Decisions can be made quickly
                    • Small talk before meetings is brief
                    • Written agreements are important
                """.trimIndent()
            )
            "social" -> EtiquetteData(
                description = """
                    American social culture is generally informal, friendly, and individualistic. Americans value personal freedom and casual relationships.
                    
                    Greetings: Casual and friendly. "Hi, how are you?" is a greeting, not a genuine question expecting a detailed answer.
                    
                    Conversation: Americans are comfortable with small talk and casual conversation with strangers.
                    
                    Tipping: Tipping 15-20% is expected in restaurants, taxis, and for services. It's not optional.
                    
                    Personal Space: Americans value privacy and personal space. Avoid overly personal questions initially.
                """.trimIndent(),
                keyPoints = """
                    • Casual, friendly greetings are standard
                    • "How are you?" is a greeting, respond briefly
                    • Tip 15-20% at restaurants (mandatory)
                    • Splitting bills ("going Dutch") is common
                    • Americans smile frequently - it's friendly, not flirting
                    • Personal questions about age/money are avoided
                """.trimIndent()
            )
            else -> getDefaultEtiquette(type)
        }
    }
    // Default etiquette if country/type not found
    private fun getDefaultEtiquette(type: String): EtiquetteData {
        return EtiquetteData(
            description = "Select a specific etiquette category to view detailed information about ${type} etiquette in this country.",
            keyPoints = "• Click on Formal, Business, or Social to see specific etiquette tips\n• Information varies by country and context\n• Observe local customs and ask when unsure"
        )
    }

    // Setup observers for ViewModel live data
    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Social Etiquette"
                view.findViewById<TextView>(R.id.header_title)?.text = it.title ?: "Social Etiquette"

                val description = it.content ?: getDefaultEtiquetteDescription()
                contentDescriptionView.text = description

                // Show correct country name
                view.findViewById<TextView>(R.id.country_name)?.text =
                    it.countryName ?: arguments?.getString("countryName") ?: "Country"

                // Display default key points
                val keyPointsText = buildString {
                    append("• Respect local customs and traditions\n")
                    append("• Observe and follow social cues\n")
                    append("• Be mindful of personal space and boundaries\n")
                    append("• Show appreciation for local hospitality\n")
                    append("• Learn basic greetings in the local language")
                }
                keyPointsContentView.text = keyPointsText
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state if needed
        }

        // Observe and handle errors
        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    // Setup click listeners for top/bottom navigation buttons
    private fun setupClickListeners(view: View) {
        view.findViewById<MaterialCardView>(R.id.back_arrow_card)?.setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<MaterialCardView>(R.id.more_options_card)?.setOnClickListener {
            showMoreOptions()
        }

        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            saveContentOffline()
        }

        view.findViewById<MaterialCardView>(R.id.share_card)?.setOnClickListener {
            shareContent()
        }

        view.findViewById<MaterialCardView>(R.id.bookmark_card)?.setOnClickListener {
            bookmarkContent()
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_quick_guide)?.setOnClickListener {
            showQuickGuide()
        }

        setupBottomNavigation(view)
    }

    // Handle bottom navigation clicks
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

    // Default etiquette description if nothing is loaded
    private fun getDefaultEtiquetteDescription(): String {
        return """
            Understanding and respecting local etiquette is essential for meaningful cultural exchange and successful interactions abroad. Social etiquette encompasses the unwritten rules that govern daily interactions, from greetings and conversations to dining and gift-giving.
            
            Why Etiquette Matters:
            
            • Building Relationships: Proper etiquette shows respect and helps establish trust with locals, opening doors to authentic experiences and deeper cultural understanding.
            
            • Professional Success: In business settings, understanding local etiquette can be the difference between closing a deal and causing offense.
            
            • Personal Safety: Following local customs helps you blend in and avoid drawing unwanted attention or accidentally disrespecting cultural norms.
            
            • Cultural Appreciation: Demonstrating awareness of local etiquette shows that you value and respect the host culture, fostering positive cross-cultural relationships.
            
            Remember, etiquette varies significantly between regions and contexts. What's polite in one culture may be offensive in another. Stay observant, ask questions when unsure, and approach differences with curiosity rather than judgment.
        """.trimIndent()
    }

    // Actions triggered by option buttons
    private fun showMoreOptions() {
        Toast.makeText(context, "More options", Toast.LENGTH_SHORT).show()
    }

    private fun saveContentOffline() {
        Toast.makeText(context, "Content saved for offline viewing", Toast.LENGTH_SHORT).show()
    }

    private fun shareContent() {
        Toast.makeText(context, "Sharing etiquette guide...", Toast.LENGTH_SHORT).show()
    }

    private fun bookmarkContent() {
        Toast.makeText(context, "Bookmarked!", Toast.LENGTH_SHORT).show()
    }

    private fun showQuickGuide() {
        Toast.makeText(context, "Opening quick reference guide...", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToEmergency() {
        val bundle = Bundle().apply {
            putString("countryId", arguments?.getString("countryId"))
            putString("countryName", arguments?.getString("countryName"))
        }
        findNavController().navigate(R.id.emergencyFragment, bundle)
    }

    // Data class holding etiquette description and key points
    data class EtiquetteData(
        val description: String,
        val keyPoints: String
    )
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]
