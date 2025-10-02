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

    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val categoryName = arguments?.getString("categoryName")

        if (countryId == null || categoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupRecyclerViews(view)
        setupObservers(view)
        setupClickListeners(view)

        contentViewModel.loadContent(countryId, categoryId)
    }

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

    private fun setupObservers(view: View) {
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

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

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