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

class CommunicationFragment : Fragment(R.layout.fragment_communication) {
    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments
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

        // Load content
        contentViewModel.loadContent(countryId, categoryId)
    }

    private fun setupRecyclerViews(view: View) {
        dosAdapter = StringListAdapter()
        dontsAdapter = StringListAdapter()
        examplesAdapter = StringListAdapter()

        view.findViewById<RecyclerView>(R.id.dos_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dosAdapter
        }

        view.findViewById<RecyclerView>(R.id.donts_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dontsAdapter
        }

        view.findViewById<RecyclerView>(R.id.examples_recycler_view)?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = examplesAdapter
        }
    }

    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text =
                    it.title ?: "Communication Style"
                view.findViewById<TextView>(R.id.content_description)?.text =
                    it.content ?: ""

                // Update lists
                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator
            view.findViewById<View>(R.id.progress_bar)?.isVisible = isLoading
        }

        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                contentViewModel.clearError()
            }
        }
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }

        view.findViewById<View>(R.id.save_offline_card)?.setOnClickListener {
            Toast.makeText(requireContext(), "Offline saving coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}