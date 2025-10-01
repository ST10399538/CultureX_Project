package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.adapters.StringListAdapter
import com.example.culturex.data.viewmodels.ContentViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator

class CommunicationFragment : Fragment(R.layout.fragment_communication) {

    // ViewModel instance to handle data logic and lifecycle awareness
    private val contentViewModel: ContentViewModel by viewModels()
    // RecyclerView adapters for displaying lists of strings (Do's, Don'ts, Examples)
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    // Called after the fragment's view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve navigation arguments passed to this fragment
        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val categoryName = arguments?.getString("categoryName")

        // If required arguments are missing, show a message and navigate back
        if (countryId == null || categoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }
// Setup UI elements: RecyclerViews, observers for data, and click listeners
        setupRecyclerViews(view)
        setupObservers(view)
        setupClickListeners(view)

        // Trigger ViewModel to load content based on arguments
        contentViewModel.loadContent(countryId, categoryId)
    }

    // Setup the three RecyclerViews for displaying lists of Do's, Don'ts, and Examples
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

    // Setup observers to react to changes in ViewModel LiveData
    private fun setupObservers(view: View) {
        // Observe content changes (title, description, lists)
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Communication Style"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?:
                        "Effective communication across cultures requires understanding local customs, non-verbal cues, and conversational etiquette."

                // Update count badges
                view.findViewById<TextView>(R.id.dos_count)?.text = "${it.dos?.size ?: 0} Do's"
                view.findViewById<TextView>(R.id.donts_count)?.text = "${it.donts?.size ?: 0} Don'ts"
                view.findViewById<TextView>(R.id.examples_count)?.text = "${it.examples?.size ?: 0} Tips"

                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<View>(R.id.progress_overlay)?.isVisible = isLoading

            // Animate progress bar
            val progressBar = view.findViewById<LinearProgressIndicator>(R.id.page_progress)
            progressBar?.isVisible = isLoading
            if (isLoading) {
                progressBar?.setProgressCompat(75, true)
            }
        }

        // Observe errors from ViewModel and show them as Toast messages
        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    // Setup click listeners for navigation and actions
    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialButton>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }



        // Options menu
        view.findViewById<MaterialButton>(R.id.options_menu)?.setOnClickListener {
            showOptionsMenu()
        }

        // Bottom navigation card items
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
    // Show options menu (placeholder implementation with Toast)
    private fun showOptionsMenu() {
        // Show popup menu with options
        Toast.makeText(context, "Options menu", Toast.LENGTH_SHORT).show()
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