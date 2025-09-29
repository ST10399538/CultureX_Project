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

class TippingFragment : Fragment(R.layout.fragment_tipping) {

    private val contentViewModel: ContentViewModel by viewModels()
    private lateinit var dosAdapter: StringListAdapter
    private lateinit var dontsAdapter: StringListAdapter
    private lateinit var examplesAdapter: StringListAdapter
    private var isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")
        val countryName = arguments?.getString("countryName")

        if (countryId == null || categoryId == null) {
            Toast.makeText(requireContext(), "Missing required data", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        countryName?.let {
            view.findViewById<TextView>(R.id.country_name)?.text = it
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
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Tipping Culture"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?:
                        "Understanding tipping customs helps you show appreciation appropriately and avoid cultural misunderstandings."

                view.findViewById<TextView>(R.id.country_name)?.text =
                    it.countryName ?: arguments?.getString("countryName") ?: "Country"

                dosAdapter.updateItems(it.dos ?: emptyList())
                dontsAdapter.updateItems(it.donts ?: emptyList())
                examplesAdapter.updateItems(it.examples ?: emptyList())

                view.findViewById<View>(R.id.content_scroll_view)?.isVisible = true
            }
        }

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

        view.findViewById<MaterialButton>(R.id.back_to_home_button)?.setOnClickListener {
            findNavController().navigate(R.id.mainFragment)
        }

        // Favorite button
        view.findViewById<View>(R.id.favorite_button)?.setOnClickListener {
            toggleFavorite(view)
        }

        // Tip calculator buttons
        view.findViewById<MaterialButton>(R.id.tip_10)?.setOnClickListener {
            calculateTip(10)
        }

        view.findViewById<MaterialButton>(R.id.tip_15)?.setOnClickListener {
            calculateTip(15)
        }

        view.findViewById<MaterialButton>(R.id.tip_20)?.setOnClickListener {
            calculateTip(20)
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

        view.findViewById<View>(R.id.nav_notifications)?.setOnClickListener {
            navigateToNotifications()
        }
    }

    private fun toggleFavorite(view: View) {
        isFavorite = !isFavorite
        val favoriteIcon = view.findViewById<ImageView>(R.id.favorite_icon)
        if (isFavorite) {
            favoriteIcon?.setImageResource(R.drawable.ic_globe)
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoriteIcon?.setImageResource(R.drawable.ic_globe)
            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateTip(percentage: Int) {
        val billInput = view?.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.bill_input)
        val resultView = view?.findViewById<TextView>(R.id.tip_result)

        val billText = billInput?.text?.toString()

        if (billText.isNullOrEmpty()) {
            Toast.makeText(context, "Please enter a bill amount", Toast.LENGTH_SHORT).show()
            return
        }

        val billAmount = billText.toDoubleOrNull()
        if (billAmount == null) {
            Toast.makeText(context, "Invalid amount entered", Toast.LENGTH_SHORT).show()
            return
        }

        val tipAmount = billAmount * percentage / 100
        val totalAmount = billAmount + tipAmount

        resultView?.text = "Tip: R${"%.2f".format(tipAmount)} | Total: R${"%.2f".format(totalAmount)}"

    }

    private fun saveContentOffline() {
        Toast.makeText(context, "Content saved for offline viewing", Toast.LENGTH_SHORT).show()
    }

    private fun shareContent() {
        Toast.makeText(context, "Sharing content...", Toast.LENGTH_SHORT).show()
    }

    private fun translateContent() {
        Toast.makeText(context, "Translation feature coming soon", Toast.LENGTH_SHORT).show()
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

    private fun navigateToNotifications() {
        findNavController().navigate(R.id.notificationsFragment)
    }
    }

