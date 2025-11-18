package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.culturex.data.entities.CachedContent
import com.example.culturex.data.repository.OfflineRepository
import com.example.culturex.data.viewmodels.ContentViewModel
import com.example.culturex.sync.NetworkStateManager
import com.example.culturex.sync.SyncManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.example.culturex.utils.SharedPreferencesManager

class EtiquetteFragment : Fragment(R.layout.fragment_etiquette) {

    // ViewModel to fetch and hold content data
    private val contentViewModel: ContentViewModel by viewModels()

    // Offline components
    private lateinit var offlineRepository: OfflineRepository
    private lateinit var syncManager: SyncManager
    private lateinit var networkStateManager: NetworkStateManager
    private lateinit var sharedPrefsManager: SharedPreferencesManager

    // Current country information
    private var currentCountryId: String? = null
    private var currentCountryName: String = ""
    private var currentCategoryId: String? = null
    private var currentContentId: String? = null
    private var currentEtiquetteType: String = "formal"

    // State
    private var isBookmarked = false
    private var isSavedOffline = false

    // Views for displaying description and key points
    private lateinit var contentDescriptionView: TextView
    private lateinit var keyPointsContentView: TextView
    private var offlineIndicator: View? = null

    // Cached etiquette data
    private var cachedEtiquetteData: Map<String, EtiquetteData>? = null

    // Called when the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize offline components and SharedPreferences
        offlineRepository = OfflineRepository.getInstance(requireContext())
        syncManager = SyncManager.getInstance(requireContext())
        networkStateManager = NetworkStateManager.getInstance(requireContext())
        sharedPrefsManager = SharedPreferencesManager(requireContext())

        // Get arguments passed from navigation
        currentCountryId = arguments?.getString("countryId")
        currentCategoryId = arguments?.getString("categoryId")
        currentCountryName = arguments?.getString("countryName") ?: "Country"

        currentContentId = "${currentCountryId}_${currentCategoryId}"

        contentDescriptionView = view.findViewById(R.id.content_description)
        keyPointsContentView = view.findViewById(R.id.key_points_content)
        offlineIndicator = view.findViewById(R.id.offline_indicator)

        // Setup UI and observers
        setupObservers(view)
        setupClickListeners(view)
        setupChipClickListeners(view)
        setupNetworkMonitoring()

        // Display country name in the UI if available
        view.findViewById<TextView>(R.id.country_name)?.text = currentCountryName

        // Load content
        loadContent()
    }

    // Setup network monitoring
    private fun setupNetworkMonitoring() {
        networkStateManager.startMonitoring {
            // When connection is restored, sync pending operations
            viewLifecycleOwner.lifecycleScope.launch {
                syncPendingOperations()
            }
        }
    }

    // Load content from network or cache
    private fun loadContent() {
        if (networkStateManager.isCurrentlyConnected()) {
            // Load from network
            currentCountryId?.let { countryId ->
                currentCategoryId?.let { categoryId ->
                    contentViewModel.loadContent(countryId, categoryId)
                }
            }
        } else {
            // Load from cache
            loadFromCache(requireView())
        }
    }

    // Load content from local cache
    private fun loadFromCache(view: View) {
        currentCountryId?.let { countryId ->
            currentCategoryId?.let { categoryId ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val cachedContent = offlineRepository.getCachedContent(countryId, categoryId)

                    if (cachedContent != null) {
                        // Restore cached etiquette data
                        cachedEtiquetteData = mapOf(
                            "formal" to EtiquetteData(
                                cachedContent.formalDescription ?: "",
                                cachedContent.formalKeyPoints ?: ""
                            ),
                            "business" to EtiquetteData(
                                cachedContent.businessDescription ?: "",
                                cachedContent.businessKeyPoints ?: ""
                            ),
                            "social" to EtiquetteData(
                                cachedContent.socialDescription ?: "",
                                cachedContent.socialKeyPoints ?: ""
                            )
                        )

                        // Update UI with cached data
                        updateContentForEtiquetteType(currentEtiquetteType)

                        view.findViewById<TextView>(R.id.content_title)?.text =
                            cachedContent.title ?: "Social Etiquette"
                        view.findViewById<TextView>(R.id.header_title)?.text =
                            cachedContent.title ?: "Social Etiquette"
                        view.findViewById<TextView>(R.id.country_name)?.text =
                            cachedContent.countryName ?: currentCountryName

                        Snackbar.make(
                            view,
                            "Showing offline content",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        // No cache, show default
                        showDefaultContent(view)
                        Snackbar.make(
                            view,
                            "No offline content available",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // Show default content when no cache is available
    private fun showDefaultContent(view: View) {
        val etiquetteData = getEtiquetteData(currentCountryName, currentEtiquetteType)
        contentDescriptionView.text = etiquetteData.description
        keyPointsContentView.text = etiquetteData.keyPoints

        // Cache this default data
        cacheEtiquetteData()
    }

    // Cache etiquette data for all types
    private fun cacheEtiquetteData() {
        currentCountryId?.let { countryId ->
            currentCategoryId?.let { categoryId ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val formalData = getEtiquetteData(currentCountryName, "formal")
                    val businessData = getEtiquetteData(currentCountryName, "business")
                    val socialData = getEtiquetteData(currentCountryName, "social")

                    val cachedContent = CachedContent(
                        id = "${countryId}_${categoryId}",
                        countryId = countryId,
                        categoryId = categoryId,
                        countryName = currentCountryName,
                        categoryName = "Social Etiquette",
                        title = "Social Etiquette",
                        content = null,
                        dos = null,
                        donts = null,
                        examples = null,
                        formalDescription = formalData.description,
                        formalKeyPoints = formalData.keyPoints,
                        businessDescription = businessData.description,
                        businessKeyPoints = businessData.keyPoints,
                        socialDescription = socialData.description,
                        socialKeyPoints = socialData.keyPoints,
                        lastUpdated = System.currentTimeMillis(),
                        isSynced = true,
                        isBookmarked = isBookmarked,
                        isSavedForOffline = isSavedOffline
                    )

                    offlineRepository.cacheContent(cachedContent)
                }
            }
        }
    }

    // Setup listeners for etiquette type chips (Formal, Business, Social)
    private fun setupChipClickListeners(view: View) {
        val chipFormal = view.findViewById<Chip>(R.id.chip_formal)
        val chipBusiness = view.findViewById<Chip>(R.id.chip_business)
        val chipSocial = view.findViewById<Chip>(R.id.chip_social)

        chipFormal?.setOnClickListener {
            currentEtiquetteType = "formal"
            updateContentForEtiquetteType("formal")
            resetOtherChips(chipFormal, chipBusiness, chipSocial)
            chipFormal.isChecked = true
        }

        chipBusiness?.setOnClickListener {
            currentEtiquetteType = "business"
            updateContentForEtiquetteType("business")
            resetOtherChips(chipBusiness, chipFormal, chipSocial)
            chipBusiness.isChecked = true
        }

        chipSocial?.setOnClickListener {
            currentEtiquetteType = "social"
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
        // First try to use cached data if available
        val etiquetteData = if (cachedEtiquetteData != null) {
            cachedEtiquetteData!![type] ?: getEtiquetteData(currentCountryName, type)
        } else {
            getEtiquetteData(currentCountryName, type)
        }

        contentDescriptionView.text = etiquetteData.description
        keyPointsContentView.text = etiquetteData.keyPoints
    }

    // Returns etiquette data for a given country and etiquette type (with language support)
    private fun getEtiquetteData(countryName: String, type: String): EtiquetteData {
        val language = sharedPrefsManager.getPreferredLanguage()

        return when {
            countryName.contains("France", ignoreCase = true) -> getFranceEtiquette(type, language)
            countryName.contains("South Africa", ignoreCase = true) -> getSouthAfricaEtiquette(type, language)
            countryName.contains("Japan", ignoreCase = true) -> getJapanEtiquette(type, language)
            countryName.contains("India", ignoreCase = true) -> getIndiaEtiquette(type, language)
            countryName.contains("United States", ignoreCase = true) ||
                    countryName.contains("USA", ignoreCase = true) -> getUSAEtiquette(type, language)
            else -> getDefaultEtiquette(type, language)
        }
    }

    private fun getFranceEtiquette(type: String, language: String): EtiquetteData {
        return when (type) {
            "formal" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Franse formele etiket beklemtoon elegansie, respek en nakoming van protokol. Die Franse waardeer verfyndheid en goeie maniere in formele omgewings.
                        
                        Groete: Gebruik "Monsieur" of "Madame" gevolg deur die van. Wag totdat jy uitgenooi word om name te gebruik.
                        
                        Kleredrag: Draag formeel en elegant. Die Franse waardeer goed-gesnyde, klassieke klere.
                        
                        Gesprek: Vermy persoonlike vrae aanvanklik. Bespreek kuns, kultuur en filosofie, maar vermy om oor geld of godsdiens te praat.
                        
                        Tafelmaniere: Hou beide hande sigbaar op die tafel (nie in jou skoot nie). Wag vir die gasheer om te begin eet.
                    """.trimIndent(),
                    keyPoints = """
                        • Groet altyd met "Bonjour" of "Bonsoir"
                        • Gebruik formele titels totdat anders uitgenooi
                        • Handhaaf elegante postuur en gedrag
                        • Punktualiteit word hoog gewaardeer
                        • Hou hande op die tafel, nooit in skoot nie
                        • Wag vir gasheer om te begin eet en heildronke aan te bied
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha okuhle kwaseFrance kugcizelela ubuhle, inhlonipho, nokulandela umthetho. AmaFulentshi ayakwazisa ukucwenga nokuziphatha kahle ezimweni ezisemthethweni.
                        
                        Ukubingelela: Sebenzisa "Monsieur" noma "Madame" elandelwa yigama lomndeni. Linda uze umenywe ukusebenzisa amagamaokuqala.
                        
                        Ukugqoka: Gqoka ngokusemthethweni futhi ngobuhle. AmaFulentshi ayathanda izingubo ezihlelwe kahle nezindala.
                        
                        Inkulumo: Gwema imibuzo yomuntu siqu ekuqaleni. Xoxa ngobuciko, isiko nentanda, kodwa gwema ukukhuluma ngemali noma inkolo.
                        
                        Ukuziphatha etafuleni: Gcina izandla zombili zibonakala etafuleni (hhayi ethangeni lakho). Linda umphathi wekhaya ukuba aqale ukudla.
                    """.trimIndent(),
                        keyPoints = """
                        • Bingelela njalo ngo "Bonjour" noma "Bonsoir"
                        • Sebenzisa izihloko ezisemthethweni kuze umenywe
                        • Gcina ukuma okuhle nokuziphatha
                        • Ukufika ngesikhathi kuyaziwa kakhulu
                        • Gcina izandla etafuleni, ungalokothi ethangeni
                        • Linda umphathi wekhaya ukuba aqale ukudla nokunathisa
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "business" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Franse besigheidskultuur waardeer intellek, debat en formele hiërargie. Die bou van persoonlike verhoudings is belangrik voor die sluit van ooreenkomste.
                        
                        Vergaderings: Verwag formaliteit en struktuur. Aanbiedings moet goed voorberei en logies wees.
                        
                        Kommunikasie: Die Franse waardeer debat en intellektuele bespreking. Moenie verskil persoonlik opneem nie.
                        
                        Kleredrag: Konserwatiewe, hoë-gehalte besigheidsklere is noodsaaklik. Beeld maak 'n groot verskil.
                        
                        Onderhandelinge: Wees voorbereid vir langdurige besprekings. Besluite volg hiërargie en kan tyd neem.
                    """.trimIndent(),
                    keyPoints = """
                        • Skeduleer vergaderings goed vooruit
                        • Bring visitekaartjies in Frans en Engels
                        • Gebruik formele titels en vanne
                        • Wees voorbereid vir intellektuele debat
                        • Lang middagete vergaderings is algemeen (2-3 ure)
                        • Hiërargie is belangrik - spreek senior lede eerste aan
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lebhizinisi laseFrance liyakwazisa ukuhlakanipha, ukuxoxisana, nohlelo olunobuholi. Ukwakha ubudlelwano bomuntu siqu kubalulekile ngaphambi kokuvala izivumelwano.
                        
                        Imihlangano: Lindela ukusemthethweni nesakhiwo. Izethulo kufanele zilungiswe kahle futhi zinengqondo.
                        
                        Ukuxhumana: AmaFulentshi ayakwazisa ukuxoxisana nokuhlakanipha. Ungathathi ukungavumelani njengomuntu siqu.
                        
                        Ukugqoka: Izingubo zebhizinisi ezilondolozayo nezezinga eliphezulu zibalulekile. Isithombe sibaluleke kakhulu.
                        
                        Izingxoxo: Lungiselela izingxoxo ezinde. Izinqumo zilandela uhlelo futhi zingathatha isikhathi.
                    """.trimIndent(),
                        keyPoints = """
                        • Hlela imihlangano ngaphambili kakhulu
                        • Letha amakhadi ebhizinisi ngesiNgisi nesiFulentshi
                        • Sebenzisa izihloko ezisemthethweni namagama omndeni
                        • Lungiselela ukuxoxisana ngobuhlakani
                        • Imihlangano yokudla kwasemini emide ivamile (amahora ama-2-3)
                        • Uhlelo lubalulekile - khuluma namalungu aphezulu kuqala
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "social" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Franse sosiale etiket beklemtoon beleefdheid, gesprek en die genot van die lewe se genoeëns. Die Franse waardeer betekenisvolle besprekings en kulturele waardering.
                        
                        Groete: Die "bise" (wangsoen) is algemeen onder vriende, tipies 2-4 soene afhangende van streek.
                        
                        Gesprek: Raak betrokke by stimulerende bespreking oor kultuur, kos en huidige gebeure. Vermy harde of lawaaierige gedrag.
                        
                        Eet: Maaltye is sosiale geleenthede om te geniet. Moenie haastig deur gange gaan nie.
                        
                        Geskenke gee: Bring blomme (nie krisant nie), wyn of sjokolade wanneer jy by 'n huis uitgenooi word.
                    """.trimIndent(),
                    keyPoints = """
                        • Sê altyd "Bonjour" wanneer jy winkels binnegaan
                        • Leer basiese Franse frases - poging word waardeer
                        • Hou jou stem op matige volume
                        • Geniet maaltye - eet is 'n sosiale plesier
                        • Moenie oor geld praat of oor salarisse vra nie
                        • Wynwaardering is kultureel belangrik
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha komphakathi waseFrance kugcizelela inhlonipho, inkulumo, nokujabulela izinto ezinhle zokuphila. AmaFulentshi ayakwazisa izingxoxo ezinengqondo nokwazisa isiko.
                        
                        Ukubingelela: "Bise" (ukwanga ngesidlele) kuvamile phakathi kwabangani, ngokuvamile ama-2-4 ukwanga kuye ngesifunda.
                        
                        Inkulumo: Hlanganyela engxoxweni egqugquzelayo ngesiko, ukudla nezenzakalo zamanje. Gwema ukuziphatha ngomsindo noma ukuphazima.
                        
                        Ukudla: Ukudla yimicimbi yomphakathi okufanele ijatshulwe. Ungaphuthumi eziteshini zokudla.
                        
                        Ukunikela izipho: Letha izimbali (hhayi ama-chrysanthemum), iwayini noma ushokoledi uma umenyiwe ekhaya.
                    """.trimIndent(),
                        keyPoints = """
                        • Shono njalo "Bonjour" uma ungena ezitolo
                        • Funda imisho eyisisekelo yesiFulentshi - umzamo uyahlonishwa
                        • Gcina izwi lakho kumthamo omaphakathi
                        • Jabulela ukudla - ukudla kuyijabulo yomphakathi
                        • Ungakhulumi ngemali noma ubuze ngemiholo
                        • Ukwazisa iwayini kubalulekile ngokwesiko
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            else -> getDefaultEtiquette(type, language)
        }
    }

    private fun getSouthAfricaEtiquette(type: String, language: String): EtiquetteData {
        return when (type) {
            "formal" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Suid-Afrikaanse formele etiket weerspieël die land se diverse kulturele erfenis, wat Afrika-, Europese en Asiatiese invloede met moderne professionaliteit kombineer.
                        
                        Groete: Gebruik titels en vanne aanvanklik. 'n Ferm handdruk is standaard oor alle kulture.
                        
                        Kleredrag: Besigheid formeel of netjiese informeel afhangende van die geleentheid. Standaarde wissel per streek en bedryf.
                        
                        Respek: Toon respek vir Suid-Afrika se diverse kulture en tale. Erken die land se geskiedenis sensitief.
                        
                        Protokol: Volg Westerse formele etiket met bewustheid van kulturele diversiteit.
                    """.trimIndent(),
                    keyPoints = """
                        • Gebruik toepaslike titels en vanne
                        • Ferm handdrukke is standaard
                        • Respekteer kulturele diversiteit
                        • Punktualiteit word verwag in formele omgewings
                        • Wees bewus van Suid-Afrika se 11 amptelike tale
                        • Toon kulturele sensitiwiteit rakende apartheidsgeskiedenis
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha okuhle kwaseNingizimu Afrika kubonisa ifa lamasiko ahlukahlukene ezweni, kuhlanganisa ithonya lase-Afrika, laseYurophu nelase-Asia nobuchwepheshe banamuhla.
                        
                        Ukubingelela: Sebenzisa izihloko namagama omndeni ekuqaleni. Ukusheka ngesandla okuqinile kuvamile kuwo onke amasiko.
                        
                        Ukugqoka: Ibhizinisi elisemthethweni noma elithobekile kuye ngomcimbi. Izindinganiso ziyahlukahluka ngesifunda nemboni.
                        
                        Inhlonipho: Bonisa inhlonipho yamasiko nezilimi ezihlukahlukene zaseNingizimu Afrika. Vuma umlando wezwe ngobunono.
                        
                        Umthetho: Landela ukuziphatha okuhle kwaseNtshonalanga nokuqaphela ukuhlukahluka kwamasiko.
                    """.trimIndent(),
                        keyPoints = """
                        • Sebenzisa izihloko ezifanele namagama omndeni
                        • Ukusheka ngesandla okuqinile kuyinto evamile
                        • Hlonipha ukuhlukahluka kwamasiko
                        • Ukufika ngesikhathi kulindelwe ezimweni ezisemthethweni
                        • Qaphela izilimi ezisemthethweni eziyi-11 zaseNingizimu Afrika
                        • Bonisa ukuzwelana ngokwesiko mayelana nomlando we-apartheid
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "business" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Suid-Afrikaanse besigheidskultuur is verhoudings-georiënteerd en relatief informeel, alhoewel professionele standaarde gehandhaaf word.
                        
                        Vergaderings: Terwyl punktualiteit gewaardeer word, begin vergaderings dalk nie altyd betyds nie. Bou verhouding voor besigheid bespreek word.
                        
                        Kommunikasie: Direkte maar vriendelike kommunikasie word waardeer. Suid-Afrikaners waardeer eerlikheid en reguitheid.
                        
                        Diversiteit: Wees respekvol teenoor die multikulturele werksmag. Engels word algemeen in besigheid gebruik.
                        
                        Onderhandelinge: Besluite mag konsultasie oor verskeie vlakke behels. Wees geduldig met die proses.
                    """.trimIndent(),
                    keyPoints = """
                        • Bou persoonlike verhoudings eerste
                        • Ruil visitekaartjies met beide hande uit
                        • Wees direkt maar vriendelik in kommunikasie
                        • Respekteer die reënboognasie se diversiteit
                        • Moenie onderhandelinge haas nie
                        • Koffievergaderings is algemeen vir aanvanklike besprekings
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lebhizinisi laseNingizimu Afrika ligxile ebudlelwaneni futhi alinasimanjemanje, nakuba izindinganiso zobuchwepheshe zigcinwa.
                        
                        Imihlangano: Nakuba ukufika ngesikhathi kuyahlonishwa, imihlangano ayihlale iqala ngesikhathi. Yakha ubudlelwano ngaphambi kokuqala ibhizinisi.
                        
                        Ukuxhumana: Ukuxhumana okuqondile kodwa okunobungane kuyahlonishwa. AbaseNingizimu Afrika bayakwazisa ubuqotho nokuqonda.
                        
                        Ukuhlukahluka: Hlonipha abasebenzi abaneziko eziningi. IsiNgisi sivame ukusetshenziswa ebhizinisweni.
                        
                        Izingxoxo: Izinqumo zingase zibandakanye ukubonisana emazingeni ahlukahlukene. Yiba nesineke nenqubo.
                    """.trimIndent(),
                        keyPoints = """
                        • Yakha ubudlelwano bomuntu siqu kuqala
                        • Shintshanisa amakhadi ebhizinisi ngezandla zombili
                        • Yiba noqondile kodwa unobungane ekuxhumaneni
                        • Hlonipha ukuhlukahluka kwesizwe lomnyama
                        • Ungaphuthumi izingxoxo
                        • Imihlangano yekhofi ivamile ezingxoxweni zokuqala
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "social" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Suid-Afrikaanse sosiale kultuur is warm, vriendelik en divers. Die konsep van "Ubuntu" (menslikheid teenoor ander) is sentraal tot sosiale interaksies.
                        
                        Groete: Groete is belangrik. Neem tyd om "Hoe gaan dit?" te vra en verwag 'n opregte antwoord.
                        
                        Gasvryheid: Suid-Afrikaners is bekend vir hul gasvryheid. Verwag vrygewige vermaak.
                        
                        Braai-kultuur: Die "braai" (braaivleis) is 'n sleutel sosiale gebeurtenis. As jy uitgenooi word, bied aan om iets te bring.
                        
                        Gesprek: Suid-Afrikaners geniet besprekings oor sport (veral rugby en krieket), natuur, en hul diverse kultuur.
                    """.trimIndent(),
                    keyPoints = """
                        • Neem tyd met groete - dit maak saak
                        • Omhels die konsep van "Ubuntu"
                        • Braais is belangrike sosiale geleenthede
                        • Sport is 'n groot gespreksonderwerp
                        • Fooigeld van 10-15% is standaard in restaurante
                        • Respekteer alle kulture en tale gelyklik
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lomphakathi waseNingizimu Afrika sinobungane, sinobuhlobo futhi sihlukahlukene. Umqondo we-"Ubuntu" (ubuntu kubanye) uyisihluthulelo sokuxhumana komphakathi.
                        
                        Ukubingelela: Ukubingelela kubalulekile. Thatha isikhathi ukubuza "Unjani?" futhi lindela impendulo eqotho.
                        
                        Ukwamukela: AbaseNingizimu Afrika baziwa ngokwamukela kwabo. Lindela ukujabuliswa ngokuvulekile.
                        
                        Isiko leBraai: "Ibraai" (ukosa inyama) iyisiganeko esibalulekile somphakathi. Uma umenyiwe, nikela ukuletha okuthile.
                        
                        Inkulumo: AbaseNingizimu Afrika bayakujabulela ukuxoxa ngemidlalo (ikakhulukazi iragbhi nekhilikhi), imvelo, nesiko sabo esihlukahlukene.
                    """.trimIndent(),
                        keyPoints = """
                        • Thatha isikhathi sokubingelela - kuyabaluleka
                        • Yamukela umqondo we-"Ubuntu"
                        • AmaBraai ayimicimbi ebalulekile yomphakathi
                        • Ezemidlalo kuyisihloko esikhulu senkulumo
                        • Ukunikela ithumbu lika-10-15% kuyinto evamile ezindaweni zokudlela
                        • Hlonipha onke amasiko nezilimi ngokulinganayo
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            else -> getDefaultEtiquette(type, language)
        }
    }

    private fun getJapanEtiquette(type: String, language: String): EtiquetteData {
        return when (type) {
            "formal" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Japannese formele etiket is diep gegrond in respek, hiërargie en eeue van tradisie. Elke gebaar het betekenis en doel.
                        
                        Groete: Buig toepaslik - diepte dui respeksvlak aan. Ruil visitekaartjies (meishi) met beide hande.
                        
                        Respek: Toon eerbied aan ouerdomme en meerderes. Gebruik toepaslike eerbewyse taal (keigo).
                        
                        Gedrag: Handhaaf kalmte en vermy emosionele vertonings. Stilte word gewaardeer en is gemaklik.
                        
                        Geskenke gee: Formele geskenk-gee (temiyage) volg spesifieke protokolle. Aanbieding maak net soveel saak as die geskenk.
                    """.trimIndent(),
                    keyPoints = """
                        • Buig wanneer jy groet - diepte toon respeksvlak
                        • Verwyder skoene wanneer jy huise binnegaan
                        • Bied en ontvang items met beide hande
                        • Moenie nooit met eetstokke wys nie
                        • Vermy direkte weiering - gebruik indirekte taal
                        • Respekteer stilte - dit is nie ongemaklik nie
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha okuhle kwaseJapan kugxile enhlolelweni, ohlwini, namasiko amakhulu eminyaka. Yonke imizwa inencazelo nenhloso.
                        
                        Ukubingelela: Khothama ngendlela efanele - ukujula kubonisa izinga lenhlonipho. Shintshanisa amakhadi ebhizinisi (meishi) ngezandla zombili.
                        
                        Inhlonipho: Bonisa inhlonipho kubadala nakwabaphezulu. Sebenzisa ulimi oluhloniphekile olufanele (keigo).
                        
                        Ukuziphatha: Gcina ukuzola futhi ugweme ukubonisa imizwa. Ukuthula kuyahlonishwa futhi kulula.
                        
                        Ukunikela izipho: Ukunikela izipho okusemthethweni (temiyage) kulandela imithetho ethile. Indlela yokunikela ibaluleke njengomnikelo.
                    """.trimIndent(),
                        keyPoints = """
                        • Khothama lapho ubingelela - ukujula kubonisa izinga lenhlonipho
                        • Khipha izicathulo uma ungena emakhaya
                        • Nikela futhi wamukele izinto ngezandla zombili
                        • Ungalokothi ukhuphule ngezinduku zokudla
                        • Gwema ukunqaba ngokuqondile - sebenzisa ulimi olungaqondile
                        • Hlonipha ukuthula - akuyona into enobunzima
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "business" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Japannese besigheidskultuur beklemtoon groepharmonie (wa), konsensus besluitneming, en langtermyn verhoudings bo vinnige winste.
                        
                        Vergaderings: Arriveer vroeg. Die mees senior persoon praat eerste. Verwag formele voorstellings en visitekaartjie-uitruiling rituele.
                        
                        Kommunikasie: Indirekte kommunikasie word verkies. Lees tussen die lyne is noodsaaklik. "Ja" kan beteken "Ek verstaan," nie "Ek stem saam" nie.
                        
                        Besluite: Gemaak deur konsensus (ringi-stelsel). Dit neem tyd maar verseker inkoop.
                        
                        Verhoudings: Besigheidsverhoudings word met verloop van tyd gebou deur na-werk sosialisering (nomikai).
                    """.trimIndent(),
                    keyPoints = """
                        • Ruil visitekaartjies (meishi) seremonieel uit
                        • Moenie nooit op iemand se visitekaartjie skryf nie
                        • Punktualiteit is absoluut krities
                        • Konsensus is belangriker as spoed
                        • Na-werk drink bou verhoudings
                        • Draag konserwatief - donker pakke is standaard
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lebhizinisi laseJapan ligcizelela ukuvumelana kweqembu (wa), ukwenza izinqumo ngokuvumelana, nobudlelwano besikhathi eside kunengeniso esheshayo.
                        
                        Imihlangano: Fika kusenesikhathi. Umuntu omdala kunawo onke ukhuluma kuqala. Lindela izethulo ezisemthethweni nezinqubo zokushintshanisa amakhadi ebhizinisi.
                        
                        Ukuxhumana: Ukuxhumana okungaqondile kuyathandwa. Ukufunda phakathi kwemigqa kubalulekile. "Yebo" kungasho "Ngiyaqonda," hhayi "Ngiyavuma."
                        
                        Izinqumo: Zenziwa ngokuvumelana (uhlelo lwe-ringi). Lokhu kuthatha isikhathi kodwa kuqinisekisa ukuzibophezela.
                        
                        Ubudlelwano: Ubudlelwano bebhizinisi buyakhiwa ngokuhamba kwesikhathi ngokuxhumana ngemva komsebenzi (nomikai).
                    """.trimIndent(),
                        keyPoints = """
                        • Shintshanisa amakhadi ebhizinisi (meishi) ngokomkhuba
                        • Ungalokothi ubhale ekhathini lebhizinisi lomuntu
                        • Ukufika ngesikhathi kubaluleke kakhulu
                        • Ukuvumelana kubaluleke kunesivinini
                        • Ukuphuza ngemva komsebenzi kwakha ubudlelwano
                        • Gqoka ngendlela elondolozayo - izingubo ezimnyama ziyinto evamile
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "social" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Japannese sosiale etiket balanseer tradisie met moderne lewe, beklemtoon harmonie, oorweging vir ander, en die lees van sosiale leidrade.
                        
                        Openbare Gedrag: Handhaaf stilte in openbare ruimtes. Vermy telefoonoproepe op treine. Moenie eet terwyl jy loop nie.
                        
                        Eet: Sê "itadakimasu" voor ete en "gochisosama" daarna. Skink drankies vir ander, nooit vir jouself nie.
                        
                        Bad: Was deeglik voor jy gemeenskaplike baddens (onsen) binnegaan. Tatoeëermerke word dalk nie toegelaat nie.
                        
                        Geskenke gee: Klein geskenke (omiyage) wanneer jy besoek. Weier geskenke twee keer voor aanvaarding (tradisioneel).
                    """.trimIndent(),
                    keyPoints = """
                        • Buig wanneer jy vriende groet (minder formeel as besigheid)
                        • Verwyder skoene by ingange
                        • Moenie fooigeld gee nie - dit kan beledigend wees
                        • Skink drankies vir ander, nie vir jouself nie
                        • Sê "itadakimasu" voor ete
                        • Hou foon stil in openbare ruimtes
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha komphakathi waseJapan kulinganisa amasiko nokuphila kwanamuhla, kugcizelela ukuvumelana, ukucabangela abanye, nokufunda izimpawu zomphakathi.
                        
                        Ukuziphatha Emphakathini: Gcina ukuthula ezindaweni zomphakathi. Gwema ukushaya ucingo ezitimelalini. Ungadli ngesikhathi uhamba.
                        
                        Ukudla: Shono "itadakimasu" ngaphambi kokudla futhi "gochisosama" ngemva. Thela iziphuzo kwabanye, ungalokothi ozithelele.
                        
                        Ukugeza: Geza ngokuphelele ngaphambi kokungena emanzini omphakathi (onsen). Ama-tattoo angase angeniswe.
                        
                        Ukunikela izipho: Izipho ezincane (omiyage) uma uvakasha. Nqaba izipho kabili ngaphambi kokwamukela (ngokomasiko).
                    """.trimIndent(),
                        keyPoints = """
                        • Khothama lapho ubingelela abangani (kungekho semthethweni njengelebhizinisi)
                        • Khipha izicathulo ezindaweni zokungena
                        • Unganikeli ithumbu - kungaba yinhlamba
                        • Thela iziphuzo kwabanye, hhayi wena
                        • Shono "itadakimasu" ngaphambi kokudla
                        • Gcina ucingo lwakho lungathuli emphakathini
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            else -> getDefaultEtiquette(type, language)
        }
    }

    private fun getIndiaEtiquette(type: String, language: String): EtiquetteData {
        return when (type) {
            "formal" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Indiese formele etiket kombineer antieke tradisies met moderne praktyke, wat aansienlik wissel oor streke, godsdienste en gemeenskappe.
                        
                        Groete: Die "namaste" (hande saam, ligte buig) is respekvol. Handdrukke is algemeen in besigheid maar kontroleer kulturele leidrade.
                        
                        Respek: Toon besondere respek aan ouerdomme. Om ouerdomme se voete te raak is 'n teken van diep respek in sommige gemeenskappe.
                        
                        Drag: Beskeie, konserwatiewe drag is belangrik, veral in godsdienstige omgewings. Vroue moet skouers en knieë bedek.
                        
                        Hiërargie: Respek vir senioriteit en status is diep ingewortel. Spreek mense aan met titels en vanne.
                    """.trimIndent(),
                    keyPoints = """
                        • Groet met "Namaste" - hande saam
                        • Respekteer ouerdomme en hiërargie
                        • Draag beskeie, veral by godsdienstige plekke
                        • Verwyder skoene wanneer jy huise en tempels binnegaan
                        • Vermy openbare vertonings van geneentheid
                        • Gebruik regterhand vir gee en ontvang
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha okuhle kwaseNdiya kuhlanganisa amasiko amakhulu nezindlela zanamuhla, okuhlukahluka kakhulu ezifundazweni, ezinkolweni nasemiphakathini.
                        
                        Ukubingelela: "Namaste" (izandla ndawonye, ukukhothama kancane) kuyinhlonipho. Ukusheka ngesandla kuvamile ebhizinisweni kodwa hlola izimpawu zesiko.
                        
                        Inhlonipho: Bonisa inhlonipho ekhethekile kubadala. Ukuthinta izinyawo zabadala kuyisibonakaliso senhlonipho ejulile kweminye imiphakathi.
                        
                        Ukugqoka: Ukugqoka okuthobeke, okulondolozayo kubalulekile, ikakhulukazi ezindaweni zenkolo. Abesifazane kufanele bembese amahlombe namadolo.
                        
                        Uhlelo: Inhlonipho yobukhulu nesikhundla igxilile kakhulu. Khuluma nabantu ngokusebenzisa izihloko namagama omndeni.
                    """.trimIndent(),
                        keyPoints = """
                        • Bingelela ngo "Namaste" - izandla ndawonye
                        • Hlonipha ubadala nohlelo
                        • Gqoka ngokuthobeka, ikakhulukazi ezindaweni zenkolo
                        • Khipha izicathulo uma ungena emakhaya nasemathempelini
                        • Gwema ukubonisa uthando emphakathini
                        • Sebenzisa isandla sokudla ukunikela nokwamukela
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "business" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Indiese besigheidskultuur is verhoudings-gefokus, hiërargies, en toenemend geglobaliseer terwyl tradisionele waardes gehandhaaf word.
                        
                        Vergaderings: Verwag 'n mate van buigsaamheid met tyd. Die bou van persoonlike verhouding gaan besigheidsbesprekings vooraf.
                        
                        Kommunikasie: Indiërs kan knik of "ja" sê om beleefd te wees, nie noodwendig in ooreenstemming nie. Volg op in skrif.
                        
                        Onderhandelinge: Verwag gedetailleerde onderhandelinge. Indiërs is bekwame onderhandelaars wat waarde heg aan goeie transaksies.
                        
                        Hiërargie: Besluite word bo geneem. Toon respek aan senior lede en wag vir hul insette.
                    """.trimIndent(),
                    keyPoints = """
                        • Bou persoonlike verhoudings eerste
                        • Respekteer hiërargiese besluitneming
                        • Wees geduldig - "Indiese Standaardtyd" mag van toepassing wees
                        • Gebruik titels en vanne
                        • Verwag uitgebreide onderhandelinge
                        • Tee/chai word gereeld aangebied - aanvaar grasielik
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lebhizinisi laseNdiya ligxile ebudlelwaneni, bunohlelo, futhi liyasemhlabeni ngenkathi ligcina amanani endabuko.
                        
                        Imihlangano: Lindela ukuvumelana ngesikhathi. Ukwakha ubudlelwano bomuntu siqu kungaphambi kwezingxoxo zebhizinisi.
                        
                        Ukuxhumana: AmaNdiya angase akhwikize noma athi "yebo" ukuze ahloniphe, hhayi ukuthi ayavuma. Landelela ngokubhala.
                        
                        Izingxoxo: Lindela izingxoxo ezinemininingwane. AmaNdiya angabaxoxi abahlakaniphile abazisa ukuthola imishwalense emihle.
                        
                        Uhlelo: Izinqumo zenziwa phezulu. Bonisa inhlonipho kumalungu aphezulu futhi ulinde umbono wawo.
                    """.trimIndent(),
                        keyPoints = """
                        • Yakha ubudlelwano bomuntu siqu kuqala
                        • Hlonipha ukwenziwa kwezinqumo ngokohlelo
                        • Yiba nesineke - "Isikhathi saseNdiya esivamile" singase sisebenze
                        • Sebenzisa izihloko namagama omndeni
                        • Lindela izingxoxo eziningi
                        • Itiye/i-chai inikezwa njalo - yamukela ngomusa
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "social" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Indiese sosiale etiket is warm, gasvry, en diep beïnvloed deur diverse godsdienstige en kulturele tradisies reg oor die land.
                        
                        Gasvryheid: Indiese gasvryheid is legendaries. Gaste word behandel soos gode ("Atithi Devo Bhava"). Verwag vrygewige kosaanbiedinge.
                        
                        Koskultuur: Baie Indiërs is vegetaries. Vra altyd oor dieetbeperkings. Gebruik regterhand vir eet.
                        
                        Familie: Familie is sentraal tot Indiese kultuur. Verwag vrae oor familie, huwelik en kinders.
                        
                        Feeste: Indië het talle godsdienstige en kulturele feeste. Deelname word verwelkom en waardeer.
                    """.trimIndent(),
                    keyPoints = """
                        • Aanvaar gasvryheid grasielik
                        • Gebruik regterhand vir eet en groet
                        • Respekteer diverse godsdienstige praktyke
                        • Vra oor dieetbeperkings (baie vegetariërs)
                        • Familie-vrae is normaal en vriendelik
                        • Verwyder skoene voor jy huise binnegaan
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lomphakathi waseNdiya sinobungane, sinobuhlobo, futhi sithonywe kakhulu yinkolo namasiko ahlukahlukene ezweni lonke.
                        
                        Ukwamukela: Ukwamukela kwaseNdiya kuyadumisa. Izivakashi ziphathwa njengonkulunkulu ("Atithi Devo Bhava"). Lindela ukudla okuvulekile.
                        
                        Isiko Lokudla: AmaNdiya amaningi angabangawenyama. Buza njalo ngezikhathi zokudla. Sebenzisa isandla sokudla sokudla.
                        
                        Umndeni: Umndeni uyisihluthulelo kwesiko laseNdiya. Lindela imibuzo ngomndeni, umshado nezingane.
                        
                        Imikhosi: INdiya inemicimbi eminingi yenkolo nesiko. Ukubamba iqhaza kuyemukelwa futhi kuyahlonishwa.
                    """.trimIndent(),
                        keyPoints = """
                        • Yamukela ukwamukela ngomusa
                        • Sebenzisa isandla sokudla sokudla nokubingelela
                        • Hlonipha izinkambo zenkolo ezahlukahlukene
                        • Buza ngezikhathi zokudla (abaningi abangabangawenyama)
                        • Imibuzo yomndeni ivamile futhi inobungane
                        • Khipha izicathulo ngaphambi kokungena emakhaya
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            else -> getDefaultEtiquette(type, language)
        }
    }

    private fun getUSAEtiquette(type: String, language: String): EtiquetteData {
        return when (type) {
            "formal" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Amerikaanse formele etiket is geneig om minder rigied te wees as in baie ander lande, maar handhaaf steeds standaarde van respek en professionaliteit.
                        
                        Groete: Ferm handdruk met direkte oogkontak. Amerikaners beweeg vinnig na eerste-naam basis.
                        
                        Punktualiteit: Om betyds te wees is baie belangrik. Om laat te arriveer word as onrespectvol beskou.
                        
                        Kommunikasie: Direkte kommunikasie word gewaardeer. Amerikaners waardeer reguit, eerlike gesprek.
                        
                        Persoonlike Ruimte: Amerikaners handhaaf meer persoonlike ruimte as baie kulture. Respekteer grense.
                    """.trimIndent(),
                    keyPoints = """
                        • Ferm handdruk met oogkontak
                        • Punktualiteit word hoog gewaardeer
                        • Gebruik name gou na voorstelling
                        • Direkte oogkontak toon vertroue
                        • Staan ongeveer 'n arm se lengte apart
                        • "Asseblief" en "dankie" is belangrik
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Ukuziphatha okuhle kwaseMelika kuvame ukungabi namthetho oluqinile njengamanye amazwe, kodwa kusagcina izindinganiso zenhlonipho nobuchwepheshe.
                        
                        Ukubingelela: Ukusheka ngesandla okuqinile nokukhangela ngeso. AbaseMelika bashesha ukusebenzisa amagama okuqala.
                        
                        Ukufika ngesikhathi: Ukufika ngesikhathi kubaluleke kakhulu. Ukufika sekwephuze kubonwa njengenhlamba.
                        
                        Ukuxhumana: Ukuxhumana okuqondile kuyahlonishwa. AbaseMelika bayakuthanda ukuxoxa ngokuqondile nangokuqotho.
                        
                        Isikhala Somuntu Siqu: AbaseMelika bagcina isikhala somuntu siqu esikhulu kunamasiko amaningi. Hlonipha imingcele.
                    """.trimIndent(),
                        keyPoints = """
                        • Ukusheka ngesandla okuqinile nokukhangela ngeso
                        • Ukufika ngesikhathi kuyahlonishwa kakhulu
                        • Sebenzisa amagama okuqala ngokushesha ngemva kwethulo
                        • Ukubheka ngeso kuqondile kubonisa ukuzethemba
                        • Yima cishe ubude bongalo
                        • "Ngiyacela" no "ngiyabonga" kubalulekile
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "business" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Amerikaanse besigheidskultuur waardeer doeltreffendheid, reguitheid en resultate. Tyd is geld, en besigheid beweeg vinnig.
                        
                        Vergaderings: Begin en eindig betyds. Agendas word streng gevolg. Kom vinnig tot die punt.
                        
                        Kommunikasie: Direkte en eksplisiete kommunikasie word verwag. Amerikaners sê wat hulle bedoel en bedoel wat hulle sê.
                        
                        Informaliteit: Besigheidskultuur is relatief informeel. Name word vinnig gebruik, selfs met uitvoerende beamptes.
                        
                        Onderhandelinge: Amerikaners verwag wen-wen uitkomste en kan ongeduldig word met langdurige onderhandelinge.
                    """.trimIndent(),
                    keyPoints = """
                        • Tyd is geld - wees punktueel en doeltreffend
                        • Direkte kommunikasie word verwag
                        • Name word vinnig in besigheid gebruik
                        • Besluite kan vinnig geneem word
                        • Klein praatjies voor vergaderings is kort
                        • Geskrewe ooreenkomste is belangrik
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lebhizinisi laseMelika liyakwazisa ukusebenza kahle, ukuqonda, nemiphumela. Isikhathi yimali, futhi ibhizinisi liyahamba ngokushesha.
                        
                        Imihlangano: Qala futhi uphele ngesikhathi. Ama-agenda alandelwa ngokuqinile. Fika endabeni ngokushesha.
                        
                        Ukuxhumana: Ukuxhumana okuqondile nokucacile kulindelwe. AbaseMelika bathi abakushoyo futhi basho abakushoyo.
                        
                        Ukungabi Semthethweni: Isiko lebhizinisi alinasimanjemanje kakhulu. Amagama okuqala asetshenziswangokushesha, ngisho nabaphathi.
                        
                        Izingxoxo: AbaseMelika balindele imiphumela ephumelela bobabili futhi bangase bakhathale ngezingxoxo ezinde.
                    """.trimIndent(),
                        keyPoints = """
                        • Isikhathi yimali - fika ngesikhathi futhi usebenze kahle
                        • Ukuxhumana okuqondile kulindelwe
                        • Amagama okuqala asetshenziswa ngokushesha ebhizinisweni
                        • Izinqumo zingakhishwa ngokushesha
                        • Ukuxoxa okuncane ngaphambi kwemihlangano kumfushane
                        • Izivumelwano ezibhaliwe zibalulekile
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            "social" -> when (language) {
                "af" -> EtiquetteData(
                    description = """
                        Amerikaanse sosiale kultuur is oor die algemeen informeel, vriendelik en individualisties. Amerikaners waardeer persoonlike vryheid en informele verhoudings.
                        
                        Groete: Informeel en vriendelik. "Hi, hoe gaan dit?" is 'n groet, nie 'n werklike vraag wat 'n gedetailleerde antwoord verwag nie.
                        
                        Gesprek: Amerikaners is gemaklik met klein praatjies en informele gesprek met vreemdelinge.
                        
                        Fooigeld: Fooigeld van 15-20% word verwag in restaurante, taxi's en vir dienste. Dit is nie opsioneel nie.
                        
                        Persoonlike Ruimte: Amerikaners waardeer privaatheid en persoonlike ruimte. Vermy oormatig persoonlike vrae aanvanklik.
                    """.trimIndent(),
                    keyPoints = """
                        • Informele, vriendelike groete is standaard
                        • "Hoe gaan dit?" is 'n groet, reageer kortliks
                        • Gee fooigeld van 15-20% by restaurante (verpligtend)
                        • Om rekeninge te verdeel ("Dutch gaan") is algemeen
                        • Amerikaners glimlag gereeld - dit is vriendelik, nie flirteer nie
                        • Persoonlike vrae oor ouderdom/geld word vermy
                    """.trimIndent()
                )
                "zu" -> EtiquetteData(
                        description = """
                        Isiko lomphakathi waseMelika kaningi alinasimanjemanje, linobungane futhi ligxile kumuntu ngamunye. AbaseMelika bayakwazisa inkululeko yomuntu siqu nobudlelwano obungazikizi.
                        
                        Ukubingelela: Okungazikizi futhi okunobungane. "Sawubona, unjani?" kuyisibingelelo, hhayi umbuzo wangempela olindele impendulo enemininingwane.
                        
                        Inkulumo: AbaseMelika banethezeka ngokukhuluma okuncane nokukhuluma okunjalo nabangaziwa.
                        
                        Ukunikela ithumbu: Ukunikela ithumbu lwe-15-20% kulindelwe ezindaweni zokudlela, ematekisi nakwezinye izinsiza. Akuyona into ongakhetha kuyo.
                        
                        Isikhala Somuntu Siqu: AbaseMelika bayakwazisa ubumfihlo nesikhala somuntu siqu. Gwema imibuzo yomuntu siqu ekuqaleni.
                    """.trimIndent(),
                        keyPoints = """
                        • Ukubingelela okungazikizi, okunobungane kuyinto evamile
                        • "Unjani?" kuyisibingelelo, phendula ngokufushane
                        • Nikela ithumbu lwe-15-20% ezindaweni zokudlela (kuyadingeka)
                        • Ukuhlukanisa izikweletu ("ukuhamba ngesiDashi") kuvamile
                        • AbaseMelika bayamamatheka njalo - kunobungane, akuyona indlela yokushela
                        • Imibuzo yomuntu siqu ngeminyaka/imali iyagwenywa
                    """.trimIndent()
                    )
                else -> EtiquetteData(
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
            }
            else -> getDefaultEtiquette(type, language)
        }
    }

    // Default etiquette if country/type not found
    private fun getDefaultEtiquette(type: String, language: String): EtiquetteData {
        return when (language) {
            "af" -> EtiquetteData(
                description = "Kies 'n spesifieke etiketkategorie om gedetailleerde inligting te sien oor ${type} etiket in hierdie land.",
                keyPoints = "• Klik op Formeel, Besigheid of Sosiaal om spesifieke etikettips te sien\n• Inligting wissel per land en konteks\n• Neem plaaslike gebruike waar en vra wanneer onseker"
            )
            "zu" -> EtiquetteData(
                description = "Khetha isigaba esithile sokuzipha ukuze ubone ulwazi olunemininingwane nge-${type} ukuziphatha kuleli zwe.",
                keyPoints = "• Chofoza ku-Formal, Business noma Social ukuze ubone amathiphu athile okuzipha\n• Ulwazi luyahlukahluka ngezwe nomongo\n• Qaphela amasiko endawo futhi ubuze uma ungaqiniseki"
            )
            else -> EtiquetteData(
                description = "Select a specific etiquette category to view detailed information about ${type} etiquette in this country.",
                keyPoints = "• Click on Formal, Business, or Social to see specific etiquette tips\n• Information varies by country and context\n• Observe local customs and ask when unsure"
            )
        }
    }

    // Setup observers for ViewModel live data
    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Social Etiquette"
                view.findViewById<TextView>(R.id.header_title)?.text = it.title ?: "Social Etiquette"

                val description = it.content ?: getDefaultEtiquetteDescription()
                contentDescriptionView.text = description

                view.findViewById<TextView>(R.id.country_name)?.text =
                    it.countryName ?: arguments?.getString("countryName") ?: "Country"

                val keyPointsText = buildString {
                    append("• Respect local customs and traditions\n")
                    append("• Observe and follow social cues\n")
                    append("• Be mindful of personal space and boundaries\n")
                    append("• Show appreciation for local hospitality\n")
                    append("• Learn basic greetings in the local language")
                }
                keyPointsContentView.text = keyPointsText

                // Cache the etiquette data
                cacheEtiquetteData()
            }
        }

        // Observe cached content
        currentCountryId?.let { countryId ->
            currentCategoryId?.let { categoryId ->
                offlineRepository.getCachedContentLiveData(countryId, categoryId)
                    .observe(viewLifecycleOwner) { cachedContent ->
                        cachedContent?.let {
                            isBookmarked = it.isBookmarked
                            isSavedOffline = it.isSavedForOffline
                        }
                    }
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state if needed
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                loadFromCache(requireView())
                contentViewModel.clearError()
            }
        }

        // Observe network state
        viewLifecycleOwner.lifecycleScope.launch {
            networkStateManager.isConnected.collect { isConnected ->
                updateOfflineIndicator(isConnected)
            }
        }
    }

    // Update offline indicator
    private fun updateOfflineIndicator(isConnected: Boolean) {
        offlineIndicator?.isVisible = !isConnected

        if (!isConnected) {
            view?.let {
                Snackbar.make(
                    it,
                    "You're offline. Changes will sync when connection is restored.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    // Sync pending operations
    private suspend fun syncPendingOperations() {
        val result = syncManager.syncAllPendingOperations()

        if (result.syncedCount > 0) {
            view?.let {
                Snackbar.make(
                    it,
                    "Synced ${result.syncedCount} change(s)",
                    Snackbar.LENGTH_SHORT
                ).show()
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
            toggleSaveOffline()
        }

        view.findViewById<MaterialCardView>(R.id.share_card)?.setOnClickListener {
            shareContent()
        }

        view.findViewById<MaterialCardView>(R.id.bookmark_card)?.setOnClickListener {
            toggleBookmark()
        }

        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_quick_guide)?.setOnClickListener {
            showQuickGuide()
        }

        setupBottomNavigation(view)
    }

    // Toggle bookmark
    private fun toggleBookmark() {
        currentContentId?.let { contentId ->
            viewLifecycleOwner.lifecycleScope.launch {
                isBookmarked = !isBookmarked
                offlineRepository.toggleBookmark(contentId, isBookmarked)

                val message = if (isBookmarked) "Bookmarked!" else "Bookmark removed"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Sync if online
                if (networkStateManager.isCurrentlyConnected()) {
                    syncPendingOperations()
                }
            }
        }
    }

    // Toggle save offline
    private fun toggleSaveOffline() {
        currentContentId?.let { contentId ->
            viewLifecycleOwner.lifecycleScope.launch {
                isSavedOffline = !isSavedOffline
                offlineRepository.toggleSaveOffline(contentId, isSavedOffline)

                val message = if (isSavedOffline)
                    "Content saved for offline viewing"
                else
                    "Removed from offline storage"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Sync if online
                if (networkStateManager.isCurrentlyConnected()) {
                    syncPendingOperations()
                }
            }
        }
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

    private fun shareContent() {
        Toast.makeText(context, "Sharing etiquette guide...", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        networkStateManager.stopMonitoring()
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
