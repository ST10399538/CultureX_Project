package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.StringListAdapter
import com.example.culturex.data.viewmodels.ContentViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class TippingFragment : Fragment(R.layout.fragment_tipping) {

    // ViewModel instance for content data
    private val contentViewModel: ContentViewModel by viewModels()
    // RecyclerView adapters for "Dos", "Don'ts" and example lists
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    // Tracks whether the content is marked as favorite
    private var isFavorite = false

    // Currency information
    private var currentCurrencyCode: String = "USD"
    private var currentCurrencySymbol: String = "$"
    private var currentCountryName: String = ""
    private var recommendedTipPercentage: Int = 15
    private var numberFormat: NumberFormat? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments passed to the fragment
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName")

        // Check for required data; if missing, show a toast and navigate up
        if (countryId == null || categoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Store country name and initialize currency settings
        currentCountryName = countryName ?: ""
        initializeCurrencyForCountry(currentCountryName)

        // Set the country name in the UI and update tip calculator
        view.findViewById<TextView>(R.id.country_name)?.text = currentCountryName
        updateCalculatorUI(view)

        // Setup RecyclerViews, LiveData observers, and click listeners
        setupRecyclerViews(view)
        setupObservers(view)
        setupClickListeners(view)

        // Load content for the specified country and category
        contentViewModel.loadContent(countryId, categoryId)
    }

    // Set currency, symbol, tip percentage, and number format based on country
    private fun initializeCurrencyForCountry(countryName: String) {
        // Set currency based on country
        when (countryName) {
            "United States" -> {
                currentCurrencyCode = "USD"
                currentCurrencySymbol = "$"
                recommendedTipPercentage = 18
                numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            }
            "France" -> {
                currentCurrencyCode = "EUR"
                currentCurrencySymbol = "€"
                recommendedTipPercentage = 10
                numberFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE)
            }
            "South Africa" -> {
                currentCurrencyCode = "ZAR"
                currentCurrencySymbol = "R"
                recommendedTipPercentage = 10
                numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
            }
            "Japan" -> {
                currentCurrencyCode = "JPY"
                currentCurrencySymbol = "¥"
                recommendedTipPercentage = 0 // Tipping is not customary in Japan
                numberFormat = NumberFormat.getCurrencyInstance(Locale.JAPAN)
            }
            "India" -> {
                currentCurrencyCode = "INR"
                currentCurrencySymbol = "₹"
                recommendedTipPercentage = 10
                numberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            }
            "United Kingdom" -> {
                currentCurrencyCode = "GBP"
                currentCurrencySymbol = "£"
                recommendedTipPercentage = 12
                numberFormat = NumberFormat.getCurrencyInstance(Locale.UK)
            }
            else -> {
                currentCurrencyCode = "USD"
                currentCurrencySymbol = "$"
                recommendedTipPercentage = 15
                numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            }
        }

        try {
            numberFormat?.currency = Currency.getInstance(currentCurrencyCode)
        } catch (e: Exception) {
            // Fallback to default if currency code is invalid
            numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        }
    }

    private fun updateCalculatorUI(view: View) {
        // Update currency symbol in hint
        view.findViewById<TextView>(R.id.currency_symbol)?.text = currentCurrencySymbol

        // Update recommended tip percentage
        view.findViewById<TextView>(R.id.recommended_tip_text)?.text =
            if (recommendedTipPercentage > 0) {
                "Recommended: $recommendedTipPercentage%"
            } else {
                "Tipping not customary"
            }

        // Update button states based on recommended percentage
        when (recommendedTipPercentage) {
            10 -> view.findViewById<MaterialButton>(R.id.tip_10)?.isSelected = true
            15 -> view.findViewById<MaterialButton>(R.id.tip_15)?.isSelected = true
            18, 20 -> view.findViewById<MaterialButton>(R.id.tip_20)?.isSelected = true
        }

        // Show/hide custom tip option for countries with unique tipping
        val customTipLayout = view.findViewById<View>(R.id.custom_tip_layout)
        customTipLayout?.isVisible = recommendedTipPercentage != 0
    }

    // Initialize adapters for the "Dos", "Don'ts", and examples RecyclerViews
    private fun setupRecyclerViews(view: View) {
        dosAdapter = StringListAdapter()
        dontsAdapter = StringListAdapter()
        examplesAdapter = StringListAdapter()

        view.findViewById<RecyclerView>(R.id.dos_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dosAdapter
            isNestedScrollingEnabled = false
        }

        view.findViewById<RecyclerView>(R.id.donts_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dontsAdapter
            isNestedScrollingEnabled = false
        }

        view.findViewById<RecyclerView>(R.id.examples_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = examplesAdapter
            isNestedScrollingEnabled = false
        }
    }

    // Observe content LiveData to update UI when content is loaded
    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Tipping Culture"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?:
                        "Understanding tipping customs helps you show appreciation appropriately and avoid cultural misunderstandings."

                view.findViewById<TextView>(R.id.country_name)?.text =
                    it.countryName ?: arguments?.getString("countryName") ?: "Country"

                // Update RecyclerViews with the loaded content
                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())

                view.findViewById<View>(R.id.content_scroll_view)?.isVisible = true
            }
        }

        // Observe errors and show toast messages
        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<View>(R.id.progress_bar)?.isVisible = isLoading
            view.findViewById<View>(R.id.content_scroll_view)?.isVisible = !isLoading
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners(view: View) {
        // Back arrow
        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Favorite button
        view.findViewById<View>(R.id.favorite_button)?.setOnClickListener {
            toggleFavorite(view)
        }

        // Tip calculator buttons
        view.findViewById<MaterialButton>(R.id.tip_10)?.setOnClickListener {
            selectTipButton(view, R.id.tip_10)
            calculateTip(10)
        }

        view.findViewById<MaterialButton>(R.id.tip_15)?.setOnClickListener {
            selectTipButton(view, R.id.tip_15)
            calculateTip(15)
        }

        view.findViewById<MaterialButton>(R.id.tip_20)?.setOnClickListener {
            selectTipButton(view, R.id.tip_20)
            calculateTip(20)
        }

        // Custom tip percentage
        view.findViewById<MaterialButton>(R.id.tip_custom)?.setOnClickListener {
            val customPercentage = view.findViewById<TextInputEditText>(R.id.custom_tip_input)
                ?.text?.toString()?.toIntOrNull()
            if (customPercentage != null && customPercentage in 1..100) {
                calculateTip(customPercentage)
            } else {
                Toast.makeText(context, "Please enter a valid percentage (1-100)", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear button
        view.findViewById<MaterialButton>(R.id.clear_button)?.setOnClickListener {
            clearCalculator(view)
        }

        // Action chips
        view.findViewById<Chip>(R.id.save_offline_chip)?.setOnClickListener {
            saveContentOffline()
        }

        view.findViewById<Chip>(R.id.share_chip)?.setOnClickListener {
            shareContent()
        }

        view.findViewById<Chip>(R.id.translate_chip)?.setOnClickListener {
            translateContent()
        }

        // Bottom navigation
        setupBottomNavigation(view)
    }

    private fun selectTipButton(view: View, selectedButtonId: Int) {
        // Reset all buttons
        view.findViewById<MaterialButton>(R.id.tip_10)?.isSelected = false
        view.findViewById<MaterialButton>(R.id.tip_15)?.isSelected = false
        view.findViewById<MaterialButton>(R.id.tip_20)?.isSelected = false

        // Select the clicked button
        view.findViewById<MaterialButton>(selectedButtonId)?.isSelected = true
    }

    // Display the currency symbol in the tip calculator
    private fun calculateTip(percentage: Int) {
        val billInput = view?.findViewById<TextInputEditText>(R.id.bill_input)
        val tipAmountView = view?.findViewById<TextView>(R.id.tip_amount_display)
        val totalAmountView = view?.findViewById<TextView>(R.id.total_amount_display)
        val perPersonView = view?.findViewById<TextView>(R.id.per_person_amount)
        val splitInput = view?.findViewById<TextInputEditText>(R.id.split_input)

        val billText = billInput?.text?.toString()

        if (billText.isNullOrEmpty()) {
            Toast.makeText(context, "Please enter a bill amount", Toast.LENGTH_SHORT).show()
            return
        }

        val billAmount = billText.toDoubleOrNull()
        if (billAmount == null || billAmount <= 0) {
            Toast.makeText(context, "Invalid amount entered", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate amounts
        val tipAmount = billAmount * percentage / 100
        val totalAmount = billAmount + tipAmount

        // Format with proper currency
        val formattedTip = numberFormat?.format(tipAmount) ?: "${"%.2f".format(tipAmount)}"
        val formattedTotal = numberFormat?.format(totalAmount) ?: "${"%.2f".format(totalAmount)}"

        // Update displays with animations
        tipAmountView?.apply {
            text = formattedTip
            animate().alpha(0f).setDuration(100).withEndAction {
                animate().alpha(1f).setDuration(200).start()
            }.start()
        }

        totalAmountView?.apply {
            text = formattedTotal
            animate().alpha(0f).setDuration(100).withEndAction {
                animate().alpha(1f).setDuration(200).start()
            }.start()
        }

        // Calculate split amount if applicable
        val splitCount = splitInput?.text?.toString()?.toIntOrNull() ?: 1
        if (splitCount > 1) {
            val perPersonAmount = totalAmount / splitCount
            val formattedPerPerson = numberFormat?.format(perPersonAmount) ?:
            "${"%.2f".format(perPersonAmount)}"
            perPersonView?.text = "Per person: $formattedPerPerson"
            perPersonView?.isVisible = true
        } else {
            perPersonView?.isVisible = false
        }

        // Show percentage indicator
        view?.findViewById<TextView>(R.id.percentage_indicator)?.apply {
            text = "$percentage%"
            isVisible = true
        }

        // Show special message for Japan (no tipping culture)
        if (currentCountryName == "Japan" && percentage > 0) {
            Toast.makeText(
                context,
                "Note: Tipping is not customary in Japan and may be considered rude",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    // Reset all input fields and displays
    private fun clearCalculator(view: View) {
        view.findViewById<TextInputEditText>(R.id.bill_input)?.text?.clear()
        view.findViewById<TextInputEditText>(R.id.custom_tip_input)?.text?.clear()
        view.findViewById<TextInputEditText>(R.id.split_input)?.text?.clear()
        view.findViewById<TextView>(R.id.tip_amount_display)?.text = numberFormat?.format(0) ?: "$0.00"
        view.findViewById<TextView>(R.id.total_amount_display)?.text = numberFormat?.format(0) ?: "$0.00"
        view.findViewById<TextView>(R.id.per_person_amount)?.isVisible = false
        view.findViewById<TextView>(R.id.percentage_indicator)?.isVisible = false

        // Reset button selection
        view.findViewById<MaterialButton>(R.id.tip_10)?.isSelected = false
        view.findViewById<MaterialButton>(R.id.tip_15)?.isSelected = false
        view.findViewById<MaterialButton>(R.id.tip_20)?.isSelected = false
    }

    private fun setupBottomNavigation(view: View) {
        view.findViewById<View>(R.id.nav_emergency)?.setOnClickListener {
            navigateToEmergency()
        }

        view.findViewById<View>(R.id.nav_home)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        view.findViewById<View>(R.id.nav_saved)?.setOnClickListener {
            navigateToSaved()
        }


    }
    // Toggle favorite state and update icon
    private fun toggleFavorite(view: View) {
        isFavorite = !isFavorite
        val favoriteIcon = view.findViewById<ImageView>(R.id.favorite_icon)
        if (isFavorite) {
            favoriteIcon?.setImageResource(R.drawable.ic_bookmark_filled)
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoriteIcon?.setImageResource(R.drawable.ic_bookmark)
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContentOffline() {
        // Placeholder for offline save
        Toast.makeText(context, "Content saved for offline viewing", Toast.LENGTH_SHORT).show()
    }

    // Placeholder for sharing function
    private fun shareContent() {
        Toast.makeText(context, "Sharing content...", Toast.LENGTH_SHORT).show()
    }

    // Placeholder for translation feature
    private fun translateContent() {
        Toast.makeText(context, "Translation feature coming soon", Toast.LENGTH_SHORT).show()
    }

    // Pass country info and navigate to emergency fragment
    private fun navigateToEmergency() {
        val bundle = Bundle().apply {
            putString("countryId", arguments?.getString("countryId"))
            putString("countryName", arguments?.getString("countryName"))
        }
        findNavController().navigate(R.id.emergencyFragment, bundle)
    }

    private fun navigateToSaved() {
        findNavController().navigate(R.id.savedFragment)
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

