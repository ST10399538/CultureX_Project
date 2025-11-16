package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.culturex.databinding.FragmentSavedBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.example.culturex.adapters.SavedContentAdapter
import com.example.culturex.data.entities.CachedContent
import com.example.culturex.data.repository.OfflineRepository
import com.example.culturex.sync.NetworkStateManager
import com.example.culturex.sync.SyncManager
import com.google.android.material.chip.Chip

import kotlinx.coroutines.launch

class SavedFragment : Fragment(R.layout.fragment_saved) {

    // View binding
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    // Offline components
    private lateinit var offlineRepository: OfflineRepository
    private lateinit var syncManager: SyncManager
    private lateinit var networkStateManager: NetworkStateManager

    // Adapter for displaying saved content
    private lateinit var savedContentAdapter: SavedContentAdapter

    // Current filter
    private var currentFilter: ContentFilter = ContentFilter.ALL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedBinding.bind(view)

        // Initialize offline components
        offlineRepository = OfflineRepository.getInstance(requireContext())
        syncManager = SyncManager.getInstance(requireContext())
        networkStateManager = NetworkStateManager.getInstance(requireContext())

        // Setup UI
        setupRecyclerView()
        setupFilterChips()
        setupClickListeners()
        setupBottomNavigation()
        setupNetworkMonitoring()

        // Load saved content
        loadSavedContent()
    }

    private fun setupRecyclerView() {
        savedContentAdapter = SavedContentAdapter(
            onItemClick = { content ->
                navigateToContent(content)
            },
            onBookmarkClick = { content ->
                toggleBookmark(content)
            },
            onDeleteClick = { content ->
                deleteSavedContent(content)
            }
        )

        binding.recyclerSavedItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = savedContentAdapter
        }
    }

    private fun setupFilterChips() {
        binding.chipAll.setOnClickListener {
            currentFilter = ContentFilter.ALL
            loadSavedContent()
        }

        binding.chipEtiquette.setOnClickListener {
            currentFilter = ContentFilter.ETIQUETTE
            loadSavedContent()
        }

        binding.chipCustoms.setOnClickListener {
            currentFilter = ContentFilter.CUSTOMS
            loadSavedContent()
        }

        binding.chipEmergency.setOnClickListener {
            currentFilter = ContentFilter.EMERGENCY
            loadSavedContent()
        }
    }

    private fun setupClickListeners() {
        binding.exploreButton.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        binding.profileIcon.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.touristAttractionsFragment)
        }
    }

    private fun setupNetworkMonitoring() {
        networkStateManager.startMonitoring {
            // When connection is restored, sync pending operations
            viewLifecycleOwner.lifecycleScope.launch {
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
        }
    }

    private fun loadSavedContent() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe bookmarked content from database
            offlineRepository.getBookmarkedContent().observe(viewLifecycleOwner) { allContent ->
                val filteredContent = when (currentFilter) {
                    ContentFilter.ALL -> allContent
                    ContentFilter.ETIQUETTE -> allContent.filter {
                        it.categoryName?.contains("Etiquette", ignoreCase = true) == true ||
                                it.title?.contains("Etiquette", ignoreCase = true) == true
                    }
                    ContentFilter.CUSTOMS -> allContent.filter {
                        it.categoryName?.contains("Dress", ignoreCase = true) == true ||
                                it.title?.contains("Dress", ignoreCase = true) == true
                    }
                    ContentFilter.EMERGENCY -> allContent.filter {
                        it.categoryName?.contains("Emergency", ignoreCase = true) == true ||
                                it.title?.contains("Emergency", ignoreCase = true) == true
                    }
                }

                updateUI(filteredContent)
            }
        }
    }

    private fun updateUI(contentList: List<CachedContent>) {
        if (contentList.isEmpty()) {
            binding.emptyState.isVisible = true
            binding.recyclerSavedItems.isVisible = false
        } else {
            binding.emptyState.isVisible = false
            binding.recyclerSavedItems.isVisible = true
            savedContentAdapter.submitList(contentList)
        }
    }

    private fun navigateToContent(content: CachedContent) {
        val bundle = Bundle().apply {
            putString("countryId", content.countryId)
            putString("categoryId", content.categoryId)
            putString("countryName", content.countryName)
            putString("categoryName", content.categoryName)
        }

        // Navigate based on content type
        when {
            content.categoryName?.contains("Dress", ignoreCase = true) == true -> {
                findNavController().navigate(R.id.dressCodeFragment, bundle)
            }
            content.categoryName?.contains("Etiquette", ignoreCase = true) == true -> {
                findNavController().navigate(R.id.etiquetteFragment, bundle)
            }
            content.categoryName?.contains("Emergency", ignoreCase = true) == true -> {
                findNavController().navigate(R.id.emergencyFragment, bundle)
            }
            else -> {
                // Default navigation
                findNavController().navigate(R.id.mainFragment)
            }
        }
    }

    private fun toggleBookmark(content: CachedContent) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Unbookmark the content
            offlineRepository.toggleBookmark(content.id, false)

            Snackbar.make(
                binding.root,
                "Removed from saved items",
                Snackbar.LENGTH_LONG
            ).setAction("UNDO") {
                // Re-bookmark if user clicks undo
                viewLifecycleOwner.lifecycleScope.launch {
                    offlineRepository.toggleBookmark(content.id, true)
                }
            }.show()

            // Sync if online
            if (networkStateManager.isCurrentlyConnected()) {
                syncManager.syncAllPendingOperations()
            }
        }
    }

    private fun deleteSavedContent(content: CachedContent) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Remove from saved offline
            offlineRepository.toggleSaveOffline(content.id, false)
            // Also unbookmark
            offlineRepository.toggleBookmark(content.id, false)

            Snackbar.make(
                binding.root,
                "Content deleted",
                Snackbar.LENGTH_SHORT
            ).show()

            // Sync if online
            if (networkStateManager.isCurrentlyConnected()) {
                syncManager.syncAllPendingOperations()
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavMap = mapOf(
            binding.navEmergency.id to R.id.emergencyFragment,
            binding.navSaved.id to null, // Already here
            binding.navNotifications.id to R.id.notificationsFragment,
            binding.navHome.id to R.id.mainFragment
        )

        bottomNavMap.forEach { (viewId, actionId) ->
            binding.root.findViewById<View>(viewId)?.setOnClickListener {
                actionId?.let { findNavController().navigate(it) }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        networkStateManager.stopMonitoring()
        _binding = null
    }

    enum class ContentFilter {
        ALL, ETIQUETTE, CUSTOMS, EMERGENCY
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

