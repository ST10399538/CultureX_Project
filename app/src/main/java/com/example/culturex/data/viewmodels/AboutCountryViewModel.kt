package com.example.culturex.data.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturex.data.models.CountryInfo
import kotlinx.coroutines.launch

class AboutCountryViewModel : ViewModel() {

    private val _countryInfo = MutableLiveData<CountryInfo?>()
    val countryInfo: LiveData<CountryInfo?> = _countryInfo

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Load comprehensive country information
     */
    fun loadCountryInfo(countryId: String, countryName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Replace with actual API call
                // For now, using mock data based on country name
                val info = getMockCountryInfo(countryName)
                _countryInfo.value = info
            } catch (e: Exception) {
                _error.value = "Failed to load country information: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Mock data provider - Replace with actual API integration
     */
    private fun getMockCountryInfo(countryName: String): CountryInfo {
        return when (countryName.lowercase()) {
            "japan" -> CountryInfo(
                geography = """Japan is an archipelago of 14,125 islands extending along the Pacific coast of East Asia. The four largest islands—Honshu, Hokkaido, Kyushu, and Shikoku—make up about 97% of Japan's land area.

The country features a diverse topography with approximately 73% mountainous terrain, dominated by the Japanese Alps and numerous active volcanoes including the iconic Mount Fuji (3,776 meters). The landscape is characterized by rugged mountains, deep valleys, and narrow coastal plains.

Japan sits on the Pacific Ring of Fire, resulting in frequent seismic activity with over 1,500 earthquakes annually and approximately 200 volcanoes, of which 60 are active. This geological position also creates natural hot springs (onsen) throughout the country.""",

                location = """Japan is located in East Asia, in the Pacific Ocean off the eastern coast of the Asian continent. It lies between latitudes 24° and 46° N, and longitudes 122° and 146° E.

The country is bordered by the Sea of Japan to the west, the Pacific Ocean to the east, the East China Sea to the south, and the Sea of Okhotsk to the north. Its nearest neighbors include Russia (to the northeast), North Korea and South Korea (to the west), and China and Taiwan (to the southwest).

Major cities include Tokyo (the capital and largest metropolitan area in the world), Osaka, Yokohama, Nagoya, Sapporo, and Fukuoka. The country spans approximately 3,000 kilometers from northeast to southwest.""",

                history = """Japan's recorded history begins around 660 BCE with the legendary founding by Emperor Jimmu. The Yayoi period (300 BCE - 300 CE) saw the introduction of wet-rice cultivation and metalworking from the Asian continent.

The classical period witnessed the rise of the imperial court in Nara and Kyoto (710-1185 CE), where Japanese culture flourished with the adoption and adaptation of Chinese writing, Buddhism, and governmental structures. The samurai class emerged during the Heian period.

Medieval Japan (1185-1603) was marked by feudalism and the rule of shoguns. After a period of civil war, Tokugawa Ieyasu unified the country, beginning the Edo period (1603-1868) of relative peace and isolation. The Meiji Restoration (1868) ended feudalism and rapidly modernized Japan, transforming it into a major world power.

The 20th century saw Japan's imperial expansion, defeat in World War II, and subsequent transformation into the world's second-largest economy by the 1980s. Today, Japan is a constitutional monarchy with a parliamentary government and remains a global leader in technology and innovation.""",

                culture = """Japanese culture is renowned for its unique blend of traditional and contemporary elements. Traditional arts include tea ceremony (sadō), flower arrangement (ikebana), calligraphy (shodō), and various martial arts such as judo, karate, and kendo.

The concept of "wa" (harmony) is fundamental to Japanese society, emphasizing group cohesion and avoiding direct conflict. This manifests in business practices, social interactions, and the emphasis on politeness and respect. The principle of "omotenashi" (wholehearted hospitality) is deeply ingrained in Japanese service culture.

Japanese cuisine, recognized by UNESCO as an Intangible Cultural Heritage, emphasizes seasonal ingredients, presentation, and balance. Beyond sushi, traditional dishes include ramen, tempura, sukiyaki, and kaiseki (elaborate multi-course meals).

Contemporary Japan is a global leader in technology, anime, manga, video games, and popular culture. Traditional festivals (matsuri), Shinto shrines, and Buddhist temples coexist with modern skyscrapers and cutting-edge technology, creating a unique cultural landscape.""",

                demographics = """Japan has a population of approximately 125 million people, making it the 11th most populous country globally. However, the population has been declining since 2011 due to low birth rates and minimal immigration.

The country faces one of the world's most aged populations, with about 29% of citizens over 65 years old. The median age is approximately 48.7 years, among the highest globally. This demographic challenge has significant implications for the economy, healthcare, and social security systems.

Japan is remarkably ethnically homogeneous, with Japanese people comprising about 98% of the population. The largest minority groups include Korean, Chinese, and Filipino communities. Recent years have seen gradual increases in immigration to address labor shortages.

Urbanization is extensive, with approximately 92% of the population living in urban areas. The Greater Tokyo Area is the world's most populous metropolitan area with over 38 million residents. Despite urbanization, rural depopulation remains a significant concern.

Literacy rate is nearly 100%, and the education system is highly regarded internationally. Japanese society values hard work, education, and social harmony, reflected in one of the world's lowest crime rates and highest life expectancies (84 years average).""",

                capital = "Tokyo",
                population = "125.7 million",
                language = "Japanese",
                currency = "Japanese Yen (¥)"
            )

            "france" -> CountryInfo(
                geography = """France is the largest country in Western Europe, covering approximately 551,695 square kilometers. Its territory forms a hexagon bordered by six different countries and four bodies of water.

The geography is remarkably diverse, ranging from the Alps in the southeast (including Mont Blanc, Western Europe's highest peak at 4,808 meters) to the Pyrenees along the Spanish border. Central France features the Massif Central, an elevated plateau of volcanic origin, while the north and west consist of relatively flat plains and low hills.

Major rivers include the Seine (flowing through Paris), Loire (France's longest river at 1,012 km), Rhône, and Garonne, which have historically served as vital transportation and trade routes. The country features three major coastlines: the Atlantic Ocean to the west, the Mediterranean Sea to the south, and the English Channel to the north.""",

                location = """France is located in Western Europe, positioned between latitudes 42° and 51° N, and longitudes 5° W and 10° E. It shares borders with eight countries: Belgium and Luxembourg to the north, Germany and Switzerland to the east, Italy to the southeast, and Monaco, Andorra, and Spain to the south.

Metropolitan France extends from the Mediterranean Sea to the English Channel and the North Sea, and from the Rhine to the Atlantic Ocean. The country also includes overseas territories in the Caribbean, South America, Indian Ocean, and Pacific Ocean.

Major cities include Paris (the capital), Marseille, Lyon, Toulouse, Nice, Nantes, Strasbourg, and Bordeaux. The country's strategic location has made it a crossroads of European culture, commerce, and politics throughout history.""",

                history = """France's history begins with Celtic Gauls who inhabited the region before Roman conquest by Julius Caesar (58-50 BCE). Roman Gaul flourished for centuries until the Frankish invasions of the 5th century established the foundations of modern France.

The medieval period saw the rise of the Capetian dynasty (987 CE) and the gradual consolidation of royal power. The Hundred Years' War with England (1337-1453) and Joan of Arc's heroism shaped French national identity. The Renaissance brought cultural flowering under François I.

The French Revolution (1789) fundamentally transformed France and influenced global politics. The Declaration of the Rights of Man and of the Citizen became a cornerstone of human rights. Napoleon Bonaparte's empire (1804-1814) spread revolutionary ideals across Europe before his defeat.

The 19th century saw industrialization and colonial expansion. France suffered devastating losses in World War I and occupation during World War II. Post-war reconstruction led to the founding of the Fifth Republic (1958) under Charles de Gaulle. Today, France is a founding member of the European Union and a global cultural and economic power.""",

                culture = """French culture has profoundly influenced Western civilization, particularly in art, architecture, literature, philosophy, and cuisine. The Enlightenment philosophers—Voltaire, Rousseau, Montesquieu—shaped modern political thought and democratic principles.

French is spoken by approximately 300 million people worldwide and serves as an official language of the United Nations. The Académie française, founded in 1635, works to preserve the French language's purity and elegance.

French cuisine is renowned globally, recognized by UNESCO as an Intangible Cultural Heritage. Regional specialties vary from Burgundy's wine and Champagne's sparkling wine to Provence's Mediterranean flavors and Brittany's crêpes. The concept of "terroir" emphasizes the connection between food and its geographical origin.

France is the world's most visited country, attracting over 90 million tourists annually. Iconic landmarks include the Eiffel Tower, Louvre Museum, Notre-Dame Cathedral, Palace of Versailles, and Mont-Saint-Michel. The country leads in fashion (Paris Fashion Week), cinema (Cannes Film Festival), and luxury goods.""",

                demographics = """France has a population of approximately 67.8 million people, making it the third most populous country in the European Union after Germany and the United Kingdom (prior to Brexit).

The population is ethnically diverse due to historical immigration from former colonies and neighboring countries. Approximately 10% of the population was born abroad, with significant communities from North Africa (Algeria, Morocco, Tunisia), sub-Saharan Africa, Portugal, and other European countries.

France has one of Europe's highest fertility rates at approximately 1.8 children per woman, though below the replacement rate. The median age is around 41.7 years, with about 20% of the population over 65. Life expectancy is among the world's highest at approximately 82.5 years.

The country is highly urbanized, with about 81% living in urban areas. Greater Paris (Île-de-France) is home to over 12 million people, making it one of Europe's largest metropolitan areas. Other major urban centers include Lyon, Marseille-Aix-en-Provence, Toulouse, and Lille.

France boasts a 99% literacy rate and offers free public education. The country operates under the principle of "laïcité" (secularism), separating religion from government. French society values égalité (equality), liberté (freedom), and fraternité (fraternity)—the national motto since the Revolution.""",

                capital = "Paris",
                population = "67.8 million",
                language = "French",
                currency = "Euro (€)"
            )

            "south africa" -> CountryInfo(
                geography = """South Africa occupies the southern tip of the African continent, covering approximately 1,221,037 square kilometers. The country is renowned for its extraordinary geographical diversity, featuring coastlines along both the Atlantic and Indian Oceans spanning over 2,500 kilometers.

The interior plateau, known as the Highveld, dominates the landscape at elevations between 1,200 and 1,800 meters. The Drakensberg mountain range forms a dramatic escarpment along the eastern edge, with peaks exceeding 3,000 meters. Table Mountain in Cape Town is one of the world's most iconic landmarks and a natural wonder.

The country encompasses diverse biomes including the arid Karoo semi-desert, the grasslands of the Highveld, subtropical forests along the eastern coast, and the unique Cape Floral Kingdom—one of the world's six floral kingdoms despite being the smallest. Major rivers include the Orange River (South Africa's longest at 2,200 km), Limpopo, and Vaal. The Kruger National Park, one of Africa's largest game reserves, showcases the country's remarkable biodiversity.""",

                location = """South Africa is located at the southern tip of Africa, positioned between latitudes 22° and 35° S, and longitudes 16° and 33° E. It is bordered by Namibia to the northwest, Botswana and Zimbabwe to the north, and Mozambique and Eswatini to the northeast.

The country uniquely surrounds the independent nation of Lesotho. South Africa has coastlines on both the Atlantic Ocean (west) and Indian Ocean (east and south), with the meeting point at Cape Agulhas—the southernmost point of Africa.

Three capital cities serve different governmental functions: Pretoria (executive), Cape Town (legislative), and Bloemfontein (judicial). Major metropolitan areas include Johannesburg (economic hub and largest city), Durban (major port city), Cape Town, Port Elizabeth, and Pretoria. The country's strategic position has historically made it a vital maritime route between Europe and Asia.""",

                history = """Indigenous San and Khoikhoi peoples inhabited the region for thousands of years before Bantu-speaking groups migrated southward around 300 CE. Dutch colonization began in 1652 with the establishment of a refreshment station at Cape Town by Jan van Riebeeck, leading to the growth of the Cape Colony.

British colonization commenced in 1806, leading to conflicts including the Anglo-Zulu War (1879) and the Anglo-Boer Wars (1880-1881 and 1899-1902). The Union of South Africa was formed in 1910 as a British dominion. In 1948, the National Party implemented apartheid, a system of institutionalized racial segregation and discrimination.

The anti-apartheid struggle, led by figures including Nelson Mandela, Oliver Tambo, and Desmond Tutu, gained international support. After decades of resistance and international pressure, apartheid was dismantled in the early 1990s. South Africa held its first democratic elections in 1994, with Nelson Mandela becoming the first Black president.

The Truth and Reconciliation Commission, chaired by Archbishop Tutu, worked to address apartheid-era crimes. Today, South Africa is a constitutional democracy with one of the most progressive constitutions in the world, though it continues to address historical inequalities and economic challenges.""",

                culture = """South Africa is known as the "Rainbow Nation" due to its multicultural diversity. The country recognizes 11 official languages, including Zulu, Xhosa, Afrikaans, English, Sotho, Tswana, Pedi, Venda, Tsonga, Swati, and Ndebele, reflecting its rich cultural heritage.

Traditional African culture remains vibrant through music, dance, and art. Zulu beadwork, Ndebele wall paintings, and various traditional crafts are celebrated nationally and internationally. Contemporary South African music blends traditional rhythms with modern genres, producing unique styles like kwaito, gqom, and Afro-house.

South African cuisine reflects its multicultural heritage, featuring dishes like bobotie, bunny chow, boerewors, biltong, and the beloved braai (barbecue) culture. Cape Malay cuisine adds spicy curries and koeksisters to the culinary landscape. The country is also renowned for its wine industry, particularly in the Western Cape.

The nation has produced globally recognized figures in literature (Nadine Gordimer, J.M. Coetzee), arts, and activism. Ubuntu, a philosophy emphasizing humanity and community, is central to South African culture. Sports, particularly rugby, soccer, and cricket, unite diverse communities. South Africa hosted the 2010 FIFA World Cup, the first on African soil.""",

                demographics = """South Africa has a population of approximately 60 million people, making it the 24th most populous country globally. The population is remarkably diverse: Black African (81%), Coloured (mixed ancestry, 9%), White (8%), and Indian/Asian (2.5%).

The country faces significant demographic challenges including a young population (median age of 28 years) and one of the world's highest HIV/AIDS prevalence rates, though treatment programs have improved dramatically. Life expectancy has increased to approximately 64 years.

Urbanization stands at about 67%, with major population centers in Gauteng (containing Johannesburg and Pretoria), KwaZulu-Natal (Durban), and Western Cape (Cape Town). Despite economic growth, South Africa grapples with high unemployment (around 33%) and remains one of the world's most unequal societies in terms of wealth distribution.

The literacy rate is approximately 95%, with education being a constitutional right. South Africa has a well-developed higher education system with prestigious universities including University of Cape Town, University of the Witwatersrand, and Stellenbosch University. The country is classified as an upper-middle-income nation and is the most industrialized economy in Africa.""",

                capital = "Pretoria, Cape Town, Bloemfontein",
                population = "60 million",
                language = "11 official languages (incl. Zulu, Xhosa, English, Afrikaans)",
                currency = "South African Rand (R)"
            )

            "india" -> CountryInfo(
                geography = """India is the seventh-largest country by land area, covering approximately 3.28 million square kilometers. The geography is extraordinarily diverse, ranging from the snow-capped Himalayas in the north (including Kanchenjunga at 8,586 meters) to tropical beaches in the south, and from the Thar Desert in the west to lush rainforests in the northeast.

The Indo-Gangetic Plain, one of the world's most fertile regions, stretches across northern India, fed by the Ganges, Brahmaputra, and Indus river systems. The Deccan Plateau dominates the southern peninsula, bordered by the Western and Eastern Ghats mountain ranges. India has a coastline of approximately 7,500 kilometers along the Arabian Sea, Bay of Bengal, and Indian Ocean.

The country experiences diverse climates, from alpine in the Himalayas to tropical in the south, with the monsoon system being crucial for agriculture. India is recognized as one of the world's 17 megadiverse countries, hosting four biodiversity hotspots. Major rivers include the Ganges (considered sacred), Brahmaputra, Yamuna, and Godavari, which support hundreds of millions of people.""",

                location = """India is located in South Asia, positioned between latitudes 8° and 37° N, and longitudes 68° and 97° E. The country shares land borders with Pakistan to the northwest, China, Nepal, and Bhutan to the north, and Myanmar and Bangladesh to the east. It also shares maritime borders with Sri Lanka and the Maldives.

The Indian subcontinent is flanked by the Arabian Sea to the west, the Bay of Bengal to the east, and the Indian Ocean to the south. The Andaman and Nicobar Islands in the Bay of Bengal and Lakshadweep in the Arabian Sea are Indian territories.

Major cities include New Delhi (the capital), Mumbai (financial capital and most populous city), Kolkata, Chennai, Bangalore (technology hub), Hyderabad, and Pune. India's strategic location at the crossroads of ancient maritime trade routes has shaped its history as a cultural and commercial hub for millennia.""",

                history = """India has one of the world's oldest continuous civilizations. The Indus Valley Civilization (3300-1300 BCE) was among the world's first urban cultures. The Vedic period (1500-500 BCE) established foundational Hindu philosophy and Sanskrit literature. Major empires including the Maurya (321-185 BCE), under whose rule Buddhism spread, and the Gupta (320-550 CE), considered India's Golden Age, shaped classical Indian culture.

Medieval India saw the rise of various regional kingdoms and the arrival of Islam, leading to the Delhi Sultanate (1206-1526) and the Mughal Empire (1526-1857), which created architectural masterpieces like the Taj Mahal and fostered Indo-Islamic culture. European colonization began with Portuguese and Dutch traders, but Britain eventually dominated, establishing the British Raj (1858-1947).

The Indian independence movement, led by Mahatma Gandhi through nonviolent resistance, and other leaders including Jawaharlal Nehru and Subhas Chandra Bose, culminated in independence on August 15, 1947. Partition created Pakistan, resulting in massive displacement and communal violence. Dr. B.R. Ambedkar drafted India's constitution, establishing the world's largest democracy.

Post-independence India has evolved from a largely agrarian economy to a major global power. Economic liberalization in 1991 spurred growth, and today India is the world's fifth-largest economy, a nuclear power, and a leader in information technology and space exploration.""",

                culture = """Indian culture is one of the world's oldest and most diverse, shaped by thousands of years of history and numerous civilizations. Hinduism, Buddhism, Jainism, and Sikhism originated here, and India is also home to significant Muslim, Christian, and other religious communities, creating a rich tapestry of beliefs and practices.

India has 22 officially recognized languages, with Hindi and English serving as official languages for the central government. Classical languages include Sanskrit, Tamil, and Telugu. Indian literature spans from ancient Vedic texts and epics (Mahabharata, Ramayana) to contemporary works by authors like Rabindranath Tagore (Nobel laureate) and modern writers.

Indian cuisine varies dramatically by region, featuring diverse flavors, spices, and cooking techniques. From North Indian curries and tandoori dishes to South Indian dosas and sambhar, from Bengali sweets to Gujarati vegetarian specialties, food is integral to cultural identity. Ayurveda, the traditional medicine system, emphasizes holistic health.

Classical arts include Bharatanatyam, Kathak, and other dance forms; Carnatic and Hindustani classical music; and various folk traditions. Bollywood (Mumbai's film industry) is the world's largest by number of films produced. India has 40 UNESCO World Heritage Sites. Yoga and meditation, originating in India, are practiced globally. Festivals like Diwali, Holi, Eid, and Christmas reflect the country's religious diversity.""",

                demographics = """India is the world's most populous country with approximately 1.43 billion people (as of 2024), accounting for about 18% of the global population. The population is incredibly diverse, encompassing thousands of ethnic groups, tribes, castes, and religious communities.

Religious composition includes Hinduism (79.8%), Islam (14.2%), Christianity (2.3%), Sikhism (1.7%), Buddhism (0.7%), and Jainism (0.4%). India has the world's second-largest Muslim population and significant religious minorities. The median age is approximately 28 years, making India one of the world's youngest major populations.

Urbanization is rapidly increasing, currently at about 35%, with major metropolitan areas experiencing explosive growth. Cities like Mumbai, Delhi, Kolkata, Bangalore, and Hyderabad are economic powerhouses. However, approximately 65% still live in rural areas, where agriculture remains the primary occupation.

The literacy rate has improved to approximately 77%, with significant gender and regional disparities. India has a robust higher education system with prestigious institutions like the Indian Institutes of Technology (IITs) and Indian Institutes of Management (IIMs). Life expectancy has increased to about 70 years.

India faces challenges including poverty (though dramatically reduced), income inequality, and infrastructure development. However, it boasts the world's largest diaspora (over 18 million), a growing middle class (estimated 300+ million), and is projected to be the world's third-largest economy by 2030.""",

                capital = "New Delhi",
                population = "1.43 billion",
                language = "Hindi, English (22+ official languages)",
                currency = "Indian Rupee (₹)"
            )

            "united states", "usa", "america" -> CountryInfo(
                geography = """The United States is the world's third-largest country by both area (9.8 million square kilometers) and population. The geography is extraordinarily diverse, encompassing virtually every climate zone and terrain type. The continental United States stretches from the Atlantic Ocean to the Pacific Ocean, bordered by Canada to the north and Mexico to the south.

Major geographical features include the Rocky Mountains (with peaks exceeding 4,000 meters), the Appalachian Mountains in the east, the Great Plains in the center, and coastal ranges along the Pacific. The Mississippi-Missouri river system is one of the world's longest at over 6,000 kilometers. The Great Lakes form the largest freshwater system on Earth.

Distinct regions include the arid Southwest deserts, tropical Florida and Hawaii, arctic Alaska, temperate Pacific Northwest, and humid subtropical Southeast. Alaska contains North America's highest peak (Denali, 6,190 meters) and vast wilderness. Hawaii consists of volcanic islands in the Pacific. The country includes diverse ecosystems from Everglades wetlands to Yellowstone's geothermal features to California's redwood forests.""",

                location = """The United States is located in North America, positioned between latitudes 25° and 49° N (continental), and longitudes 67° and 125° W. The contiguous 48 states are bordered by Canada to the north, Mexico to the south, the Atlantic Ocean to the east, and the Pacific Ocean to the west.

Alaska, the largest state, is located in the northwest extremity of North America, bordered by Canada and Russia (across the Bering Strait). Hawaii is an archipelago in the central Pacific Ocean. The country also includes territories like Puerto Rico, U.S. Virgin Islands, Guam, and American Samoa.

Major cities include Washington, D.C. (the capital), New York City (largest city and financial center), Los Angeles, Chicago, Houston, Phoenix, Philadelphia, San Antonio, San Diego, and Dallas. The country's vast geography and strategic position have made it a dominant global power with influence across multiple continents.""",

                history = """Indigenous peoples inhabited North America for over 15,000 years before European contact. European colonization began in the 16th century, with permanent English settlements established at Jamestown (1607) and Plymouth (1620). Thirteen British colonies developed along the Atlantic coast.

The American Revolution (1775-1783) resulted in independence from Britain, formalized by the Declaration of Independence (1776) and secured by victory in the Revolutionary War. The Constitution (1787) established a federal republic with separation of powers. Westward expansion through the 19th century displaced Native Americans through forced relocation policies.

The Civil War (1861-1865) over slavery and states' rights resulted in the preservation of the Union and the abolition of slavery. The Reconstruction era and subsequent Jim Crow period marked ongoing struggles for civil rights. Industrialization in the late 19th century transformed the nation into a major economic power.

The 20th century saw American involvement in both World Wars, the Great Depression, the Cold War rivalry with the Soviet Union, and the Civil Rights Movement of the 1960s. The United States emerged as a global superpower, leading in technology, culture, and military might. The 21st century has brought challenges including terrorism, economic recession, political polarization, and evolving global dynamics.""",

                culture = """American culture is characterized by diversity, individualism, and innovation, shaped by waves of immigration from every continent. The "melting pot" concept reflects the blending of cultures, though multicultural identity is increasingly celebrated. Core values include freedom, equality, democracy, and the "American Dream" of upward mobility.

English is the de facto national language, though no official language exists at the federal level. Spanish is widely spoken, and the country has speakers of hundreds of languages. American literature includes influential authors from Mark Twain to Toni Morrison. The country leads globally in scientific research and innovation.

American cuisine reflects diverse influences: regional specialties like Southern soul food, Tex-Mex, New England seafood, and Midwest comfort food. Fast food culture originated in America, as did various culinary innovations. The farm-to-table movement and diverse immigrant cuisines shape contemporary food culture.

Hollywood dominates global film and entertainment. American music has given the world jazz, blues, rock and roll, hip-hop, and country music. Sports culture centers on American football, baseball, basketball, and ice hockey. Higher education institutions like Harvard, MIT, Stanford, and many others are world-renowned. American technological companies (Apple, Google, Microsoft, Amazon) shape global digital culture.""",

                demographics = """The United States has a population of approximately 335 million people, making it the third most populous country globally. The population is remarkably diverse: White (59.3%, including Hispanic White), Hispanic/Latino (18.9%), Black/African American (13.6%), Asian (6.1%), and Native American/Alaska Native (1.3%).

The country is a nation of immigrants, with ongoing immigration from Latin America, Asia, Africa, and Europe. Approximately 14% of the population is foreign-born. English is spoken by the majority, but over 350 languages are spoken in homes across America. About 13% speak Spanish at home.

The median age is approximately 38.5 years. Life expectancy is around 76 years, though this varies significantly by race, income, and geography. The population is highly urbanized at about 83%, with major metropolitan areas including New York, Los Angeles, Chicago, Dallas-Fort Worth, Houston, and Washington D.C.

The literacy rate is approximately 99%. The United States has the world's largest higher education system, with over 4,000 colleges and universities. Americans are highly mobile, with about 9% moving residences annually. The country has significant regional cultural variations between the Northeast, South, Midwest, West, and distinct identities in states like California, Texas, and New York.

Income inequality is significant, with the top 1% holding substantial wealth. The country operates as a capitalist mixed economy and is the world's largest economy by GDP. Healthcare, education, and economic opportunity remain major social and political issues.""",

                capital = "Washington, D.C.",
                population = "335 million",
                language = "English (de facto)",
                currency = "United States Dollar ($)"
            )

            else -> CountryInfo(
                geography = """This country features diverse geographical landscapes including mountains, plains, rivers, and coastal regions. The terrain varies significantly across different regions, offering a rich variety of natural environments.

The country's topography has been shaped by millions of years of geological processes, creating unique landforms and natural features. Climate zones vary from region to region, supporting diverse ecosystems and agricultural activities.

Natural resources and geographical features have played a crucial role in shaping the country's development, settlement patterns, and economic activities throughout history.""",

                location = """Located in a strategic position, this country serves as an important crossroads in its region. It shares borders with neighboring countries and has access to important waterways or coastlines.

Major cities are distributed across the country, with the capital serving as the political, economic, and cultural center. The country's location has historically influenced its trade relationships, cultural exchanges, and geopolitical importance.

The geographical coordinates and regional position have made this nation significant in regional and international affairs.""",

                history = """This country has a rich historical heritage spanning thousands of years. Ancient civilizations laid the foundations for cultural development, followed by periods of growth, conflict, and transformation.

Throughout the medieval and modern periods, the nation has experienced significant political, social, and economic changes. Various rulers, dynasties, and governments have shaped the country's trajectory.

The contemporary era has seen modernization, industrialization, and integration into the global community. The nation continues to evolve while preserving its cultural heritage and historical identity.""",

                culture = """The country's culture reflects a unique blend of traditional customs and modern influences. Traditional arts, music, dance, and crafts continue to be valued and preserved alongside contemporary cultural expressions.

Social values emphasize community, family, and respect for traditions. Religious and spiritual practices may play important roles in daily life and cultural celebrations.

Cuisine features distinctive flavors and cooking techniques passed down through generations. National festivals and celebrations mark important historical events and seasonal changes. The country has made significant contributions to global culture in various fields.""",

                demographics = """The population includes diverse ethnic groups and communities, each contributing to the nation's cultural mosaic. Urban and rural populations are distributed across regions with varying density.

Age distribution, literacy rates, and education levels reflect the country's development stage. Family structures and social organizations follow both traditional patterns and modern trends.

The demographic profile continues to evolve with changes in birth rates, life expectancy, migration patterns, and urbanization. These factors influence the country's social policies, economic planning, and future development.""",

                capital = "Information not available",
                population = "Information not available",
                language = "Information not available",
                currency = "Information not available"
            )
        }
    }
}