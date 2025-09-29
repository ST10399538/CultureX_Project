package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentSavedBinding

class SavedFragment : Fragment(R.layout.fragment_saved) {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedBinding.bind(view)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavMap = mapOf(
            binding.navEmergency.id to R.id.action_main_to_emergencyFragment,
            binding.navSaved.id to null, // Already here
            binding.navNotifications.id to R.id.action_main_to_notificationsFragment,
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
        _binding = null
    }
}

