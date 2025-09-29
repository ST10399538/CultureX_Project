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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class EtiquetteFragment : Fragment(R.layout.fragment_etiquette) {

    private val contentViewModel: ContentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName")

        if (countryId != null && categoryId != null) {
            contentViewModel.loadContent(countryId, categoryId)
            setupObservers(view)
        }

        countryName?.let {
            view.findViewById<TextView>(R.id.country_name)?.text = it
        }

        setupClickListeners(view)
    }

    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Social Etiquette"
                view.findViewById<TextView>(R.id.header_title)?.text = it.title ?: "Social Etiquette"

                // Set detailed etiquette description
                val description = it.content ?: getDefaultEtiquetteDescription()
                view.findViewById<TextView>(R.id.content_description)?.text = description

                // Set country name
                view.findViewById<TextView>(R.id.country_name)?.text =
                    it.countryName ?: arguments?.getString("countryName") ?: "Country"

                // Populate key points if available
                val keyPointsText = buildString {
                    append("• Respect local customs and traditions\n")
                    append("• Observe and follow social cues\n")
                    append("• Be mindful of personal space and boundaries\n")
                    append("• Show appreciation for local hospitality\n")
                    append("• Learn basic greetings in the local language")
                }
                view.findViewById<TextView>(R.id.key_points_content)?.text = keyPointsText
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state if needed
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialCardView>(R.id.back_arrow_card)?.setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<MaterialButton>(R.id.back_to_home_button)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        // More options
        view.findViewById<MaterialCardView>(R.id.more_options_card)?.setOnClickListener {
            showMoreOptions()
        }

        // Action cards
        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            saveContentOffline()
        }

        view.findViewById<MaterialCardView>(R.id.share_card)?.setOnClickListener {
            shareContent()
        }

        view.findViewById<MaterialCardView>(R.id.bookmark_card)?.setOnClickListener {
            bookmarkContent()
        }

        // FAB Quick Guide
        view.findViewById<ExtendedFloatingActionButton>(R.id.fab_quick_guide)?.setOnClickListener {
            showQuickGuide()
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
}

