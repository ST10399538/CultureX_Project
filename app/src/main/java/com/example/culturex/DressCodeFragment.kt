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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturex.data.viewmodels.ContentViewModel
import com.example.culturex.adapters.StringListAdapter
import com.example.culturex.data.entities.CachedContent
import com.example.culturex.data.repository.OfflineRepository
import com.example.culturex.sync.NetworkStateManager
import com.example.culturex.sync.SyncManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DressCodeFragment : Fragment(R.layout.fragment_dress_code) {

    // ViewModel instance to handle content data (lifecycle-aware)
    private val contentViewModel: ContentViewModel by viewModels()

    // Offline components
    private lateinit var offlineRepository: OfflineRepository
    private lateinit var syncManager: SyncManager
    private lateinit var networkStateManager: NetworkStateManager

    // RecyclerView adapters for displaying lists of "Dos", "Don'ts", and "Examples"
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter

    // Current content state
    private var currentCountryId: String? = null
    private var currentCategoryId: String? = null
    private var currentContentId: String? = null
    private var isBookmarked = false
    private var isSavedOffline = false

    // UI elements
    private var bookmarkIcon: ImageView? = null
    private var saveOfflineIcon: ImageView? = null
    private var saveOfflineCard: MaterialCardView? = null
    private var offlineIndicator: View? = null

    // Called when the fragment's view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize offline components
        offlineRepository = OfflineRepository.getInstance(requireContext())
        syncManager = SyncManager.getInstance(requireContext())
        networkStateManager = NetworkStateManager.getInstance(requireContext())

        // Retrieve arguments passed to this fragment
        currentCountryId = arguments?.getString("countryId")
        currentCategoryId = arguments?.getString("categoryId")
        val categoryName = arguments?.getString("categoryName")

        // If required arguments are missing, show error and navigate back
        if (currentCountryId == null || currentCategoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        currentContentId = "${currentCountryId}_${currentCategoryId}"

        // Setup UI components and data observers
        setupRecyclerViews(view)
        setupUIReferences(view)
        setupObservers(view)
        setupClickListeners(view)
        setupNetworkMonitoring()
        animateViews(view)

        // Load content (from cache or network)
        loadContent()
    }

    private fun setupUIReferences(view: View) {
        bookmarkIcon = view.findViewById(R.id.bookmark_icon)
        saveOfflineIcon = view.findViewById(R.id.save_offline_icon)
        saveOfflineCard = view.findViewById(R.id.save_offline_card)
        offlineIndicator = view.findViewById(R.id.offline_indicator)
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

    // Setup LiveData observers from the ViewModel to update the UI
    private fun setupObservers(view: View) {
        // Observe online content from ViewModel
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                updateUI(
                    view,
                    it.title,
                    it.content,
                    it.dos ?: emptyList(),
                    it.donts ?: emptyList(),
                    it.examples ?: emptyList()
                )

                // Cache content for offline use
                cacheCurrentContent(it.title, it.content, it.dos, it.donts, it.examples)
            }
        }

        // Observe cached content from local database
        currentCountryId?.let { countryId ->
            currentCategoryId?.let { categoryId ->
                offlineRepository.getCachedContentLiveData(countryId, categoryId)
                    .observe(viewLifecycleOwner) { cachedContent ->
                        cachedContent?.let {
                            isBookmarked = it.isBookmarked
                            isSavedOffline = it.isSavedForOffline
                            updateBookmarkUI()
                            updateSaveOfflineUI()
                        }
                    }
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            view.findViewById<View>(R.id.progress_bar)?.isVisible = isLoading
        }

        // Observe errors and show them as Toast messages
        contentViewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Try loading from cache if network fails
                loadFromCache(view)
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

    // Setup network monitoring
    private fun setupNetworkMonitoring() {
        networkStateManager.startMonitoring {
            // When connection is restored, sync pending operations
            viewLifecycleOwner.lifecycleScope.launch {
                syncPendingOperations()
            }
        }
    }

    // Load content from ViewModel or cache
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
                        updateUI(
                            view,
                            cachedContent.title,
                            cachedContent.content,
                            cachedContent.dos ?: emptyList(),
                            cachedContent.donts ?: emptyList(),
                            cachedContent.examples ?: emptyList()
                        )

                        Snackbar.make(
                            view,
                            "Showing offline content",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
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

    // Cache current content to local database
    private fun cacheCurrentContent(
        title: String?,
        content: String?,
        dos: List<String>?,
        donts: List<String>?,
        examples: List<String>?
    ) {
        currentCountryId?.let { countryId ->
            currentCategoryId?.let { categoryId ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val cachedContent = CachedContent(
                        id = "${countryId}_${categoryId}",
                        countryId = countryId,
                        categoryId = categoryId,
                        countryName = arguments?.getString("countryName"),
                        categoryName = arguments?.getString("categoryName"),
                        title = title,
                        content = content,
                        dos = dos,
                        donts = donts,
                        examples = examples,
                        formalDescription = null,
                        formalKeyPoints = null,
                        businessDescription = null,
                        businessKeyPoints = null,
                        socialDescription = null,
                        socialKeyPoints = null,
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

    // Update UI with content
    private fun updateUI(
        view: View,
        title: String?,
        content: String?,
        dos: List<String>,
        donts: List<String>,
        examples: List<String>
    ) {
        view.findViewById<TextView>(R.id.content_title)?.text = title ?: "Dress Code Guide"
        view.findViewById<TextView>(R.id.content_description)?.text = content ?:
                "Understanding local dress codes helps you respect cultural norms and make positive impressions in both professional and social settings."

        dosAdapter.updateItems(dos)
        dontsAdapter.updateItems(donts)
        examplesAdapter.updateItems(examples)
    }

    // Setup all click listeners for navigation and actions
    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<MaterialCardView>(R.id.back_button_card)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }
        view.findViewById<ImageView>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        // Bookmark button
        view.findViewById<MaterialCardView>(R.id.bookmark_card)?.setOnClickListener {
            toggleBookmark()
        }

        // Save offline button - NOW WORKS LIKE BOOKMARK
        view.findViewById<MaterialCardView>(R.id.save_offline_card)?.setOnClickListener {
            toggleSaveOffline()
        }

        // Share button
        view.findViewById<MaterialCardView>(R.id.share_card)?.setOnClickListener {
            shareContent()
        }

        // Bottom navigation
        setupBottomNavigation(view)
    }

    // Setup navigation bar actions
    private fun setupBottomNavigation(view: View) {
        val navHome = view.findViewById<LinearLayout>(R.id.nav_home)
        val navEmergency = view.findViewById<LinearLayout>(R.id.nav_emergency)
        val navSaved = view.findViewById<LinearLayout>(R.id.nav_saved)


        navHome?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        navEmergency?.setOnClickListener {
            navigateToEmergency()
        }

        navSaved?.setOnClickListener {
            navigateToSaved()
        }
    }

    private fun animateViews(view: View) {
        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        view.findViewById<View>(R.id.save_offline_card)?.startAnimation(fadeIn)
        view.findViewById<View>(R.id.share_card)?.startAnimation(fadeIn)
    }

    // Toggle bookmark status
    private fun toggleBookmark() {
        currentContentId?.let { contentId ->
            viewLifecycleOwner.lifecycleScope.launch {
                isBookmarked = !isBookmarked
                offlineRepository.toggleBookmark(contentId, isBookmarked)
                updateBookmarkUI()

                val message = if (isBookmarked) {
                    "Added to Saved Items"
                } else {
                    "Removed from Saved Items"
                }

                view?.let {
                    Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                        .setAction("VIEW") {
                            navigateToSaved()
                        }
                        .show()
                }

                // Sync if online
                if (networkStateManager.isCurrentlyConnected()) {
                    syncPendingOperations()
                }
            }
        }
    }

    // Toggle save offline status - ENHANCED VERSION
    private fun toggleSaveOffline() {
        currentContentId?.let { contentId ->
            viewLifecycleOwner.lifecycleScope.launch {
                isSavedOffline = !isSavedOffline

                // When saving offline, also bookmark the content automatically
                if (isSavedOffline && !isBookmarked) {
                    isBookmarked = true
                    offlineRepository.toggleBookmark(contentId, true)
                    updateBookmarkUI()
                }

                offlineRepository.toggleSaveOffline(contentId, isSavedOffline)
                updateSaveOfflineUI()

                val message = if (isSavedOffline) {
                    "Content saved for offline viewing and added to Saved Items"
                } else {
                    "Removed from offline storage"
                }

                view?.let {
                    Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                        .setAction(if (isSavedOffline) "VIEW" else "UNDO") {
                            if (isSavedOffline) {
                                navigateToSaved()
                            } else {
                                // Undo removal
                                viewLifecycleOwner.lifecycleScope.launch {
                                    isSavedOffline = true
                                    offlineRepository.toggleSaveOffline(contentId, true)
                                    updateSaveOfflineUI()
                                }
                            }
                        }
                        .show()
                }

                // Animate the card to show feedback
                animateSaveOfflineCard()

                // Sync if online
                if (networkStateManager.isCurrentlyConnected()) {
                    syncPendingOperations()
                }
            }
        }
    }

    // Animate save offline card for visual feedback
    private fun animateSaveOfflineCard() {
        saveOfflineCard?.let { card ->
            val scaleUp = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            card.startAnimation(scaleUp)
        }
    }

    // Update bookmark icon based on state
    private fun updateBookmarkUI() {
        bookmarkIcon?.setImageResource(
            if (isBookmarked) R.drawable.ic_bookmark_filled
            else R.drawable.ic_bookmark
        )

        // Change tint color based on bookmark state
        bookmarkIcon?.setColorFilter(
            if (isBookmarked)
                requireContext().getColor(android.R.color.holo_orange_dark)
            else
                requireContext().getColor(R.color.gray_600)
        )
    }

    // Update save offline icon and card appearance based on state
    private fun updateSaveOfflineUI() {
        // Update icon
        saveOfflineIcon?.setImageResource(
            if (isSavedOffline) R.drawable.ic_download_done
            else R.drawable.ic_download
        )

        // Change card appearance based on state
        saveOfflineCard?.apply {
            if (isSavedOffline) {
                setCardBackgroundColor(requireContext().getColor(R.color.blue_100))
                strokeWidth = 2
                strokeColor = requireContext().getColor(R.color.blue_500)
            } else {
                setCardBackgroundColor(requireContext().getColor(R.color.blue_50))
                strokeWidth = 0
            }
        }

        // Update icon tint
        saveOfflineIcon?.setColorFilter(
            requireContext().getColor(
                if (isSavedOffline) R.color.blue_700
                else R.color.blue_500
            )
        )
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

    // Sync pending operations with server
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

    private fun shareContent() {
        val shareText = """
            Check out this cultural guide from CultureX!
            
            ${arguments?.getString("categoryName")} - ${arguments?.getString("countryName")}
            
            Learn about local dress codes and cultural norms.
        """.trimIndent()

        val shareIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "CultureX - ${arguments?.getString("categoryName")}")
        }

        startActivity(android.content.Intent.createChooser(shareIntent, "Share via"))
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

