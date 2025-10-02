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
        animateViews(view)

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
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Dress Code Guide"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?:
                        "Understanding local dress codes helps you respect cultural norms and make positive impressions in both professional and social settings."

                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())
            }
        }

        contentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
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

