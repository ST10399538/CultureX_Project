package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentSavedBinding

class SavedFragment : Fragment(R.layout.fragment_saved) {

    // Backing property for view binding. `_binding` is nullable to avoid memory leaks.
    private var _binding: FragmentSavedBinding? = null
    // Public non-nullable accessor for the binding. Only valid between onViewCreated and onDestroyView
    private val binding get() = _binding!!

    // Called after the fragment's view has been created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedBinding.bind(view)

        setupBottomNavigation()
    }


    // Function to handle bottom navigation item clicks
    private fun setupBottomNavigation() {
        val bottomNavMap = mapOf(
            binding.navEmergency.id to R.id.emergencyFragment,
            binding.navSaved.id to null, // Already here
            binding.navNotifications.id to R.id.notificationsFragment,
            binding.navHome.id to R.id.mainFragment,
            binding.profileIcon.id to R.id.profileFragment,
            binding.searchIcon.id to R.id.touristAttractionsFragment
        )

        bottomNavMap.forEach { (viewId, actionId) ->
            binding.root.findViewById<View>(viewId)?.setOnClickListener {
                actionId?.let { findNavController().navigate(it) }
            }
        }
    }
    // Called when the fragment's view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

