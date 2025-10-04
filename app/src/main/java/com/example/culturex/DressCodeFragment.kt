package com.example.culturex

import android.os.Bundle
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
import com.example.culturex.data.viewmodels.ContentViewModel
import com.example.culturex.adapters.StringListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class DressCodeFragment : Fragment(R.layout.fragment_dress_code) {

    // ViewModel instance to handle content data (lifecycle-aware)
    private val contentViewModel: ContentViewModel by viewModels()

    // RecyclerView adapters for displaying lists of "Dos", "Don'ts", and "Examples"
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    // Called when the fragment's view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments passed to this fragment
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val categoryName = arguments?.getString("categoryName")

        // If required arguments are missing, show error and navigate back
        if (countryId == null || categoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        // Setup UI components and data observers
        setupRecyclerViews(view)
        setupObservers(view)
        setupClickListeners(view)
        animateViews(view)

// Request content for this country and category from the ViewModel
        contentViewModel.loadContent(countryId, categoryId)
    }

    // Initialize RecyclerViews for Dos, Don'ts, and Examples lists
    private fun setupRecyclerViews(view: View) {
        dosAdapter = StringListAdapter()
        dontsAdapter = StringListAdapter()
        examplesAdapter = StringListAdapter()

        view.findViewById<RecyclerView>(R.id.dos_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dosAdapter
            isNestedScrollingEnabled = false
        } // Setup RecyclerView for "Dos"

        view.findViewById<RecyclerView>(R.id.donts_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dontsAdapter
            isNestedScrollingEnabled = false
        } // Setup RecyclerView for "Don't"

        view.findViewById<RecyclerView>(R.id.examples_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = examplesAdapter
            isNestedScrollingEnabled = false
        } // Setup RecyclerView for "Examples"
    }

    // Setup LiveData observers from the ViewModel to update the UI
    private fun setupObservers(view: View) {
        // Observe content (title, description, dos, don'ts, examples)
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Dress Code Guide"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?:
                        "Understanding local dress codes helps you respect cultural norms and make positive impressions in both professional and social settings."

                // Update lists with data from ViewModel
                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<View>(R.id.progress_bar)?.isVisible = isLoading
        }

        // Observe errors and show them as Toast messages
        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    // Setup all click listeners for navigation and actions
    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialCardView>(R.id.back_button_card)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }
        view.findViewById<ImageView>(R.id.back_arrow)?.setOnClickListener{
            findNavController().navigate(R.id.mainFragment)
        }



        // Bookmark button
        view.findViewById<MaterialCardView>(R.id.bookmark_card)?.setOnClickListener {
            toggleBookmark(view)
        }

        // Save offline button
        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            saveContentOffline()
        }

        // Share button
        view.findViewById<MaterialCardView>(R.id.share_card)?.setOnClickListener {
            shareContent()
        }

        // Bottom navigation
        setupBottomNavigation(view)
    }

    // Setup navigation bar actions (home, emergency, saved, profile)
    private fun setupBottomNavigation(view: View) {
        val navHome = view.findViewById<LinearLayout>(R.id.nav_home)
        val navEmergency = view.findViewById<LinearLayout>(R.id.nav_emergency)
        val navSaved = view.findViewById<LinearLayout>(R.id.nav_saved)
        val navProfile = view.findViewById<LinearLayout>(R.id.nav_profile)

        navHome?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        navEmergency?.setOnClickListener {
            navigateToEmergency()
        }

        navSaved?.setOnClickListener {
            navigateToSaved()
        }

        navProfile?.setOnClickListener {
            Toast.makeText(requireContext(), "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }


    private fun animateViews(view: View) {
        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        view.findViewById<View>(R.id.save_offline_card)?.startAnimation(fadeIn)
        view.findViewById<View>(R.id.share_card)?.startAnimation(fadeIn)
    }

    private fun toggleBookmark(view: View) {
        // Toggle bookmark icon and save state
        Toast.makeText(context, "Bookmarked!", Toast.LENGTH_SHORT).show()
    }

    private fun saveContentOffline() {
        Toast.makeText(context, "Content saved for offline viewing", Toast.LENGTH_SHORT).show()
    }

    private fun shareContent() {
        // Implement sharing functionality
        Toast.makeText(context, "Share feature coming soon", Toast.LENGTH_SHORT).show()
    }

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

