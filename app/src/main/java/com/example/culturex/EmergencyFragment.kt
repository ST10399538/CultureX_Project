package com.example.culturex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentEmergencyBinding

class EmergencyFragment : Fragment() {

    private var _binding: FragmentEmergencyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmergencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavMap = mapOf(
            binding.navEmergency.id to null, // Already here
            binding.navSaved.id to R.id.action_main_to_savedFragment,
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
