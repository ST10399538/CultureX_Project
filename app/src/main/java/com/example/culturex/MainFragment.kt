package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        // Menu item clicks
        binding.menuDressCode.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_dressCodeFragment)
        }

        binding.menuCommunication.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_communicationFragment)
        }

        binding.menuGreetings.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_greetingsFragment)
        }

        binding.menuEtiquette.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_etiquetteFragment)
        }

        binding.menuTipping.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_tippingFragment)
        }

        // Bottom navigation (optional)
//        binding.navHome.setOnClickListener { /* stay on Main */ }
//        binding.navEmergency.setOnClickListener { findNavController().navigate(R.id.action_main_to_emergencyFragment) }
//        binding.navSaved.setOnClickListener { findNavController().navigate(R.id.action_main_to_savedFragment) }
//        binding.navNotifications.setOnClickListener { findNavController().navigate(R.id.action_main_to_notificationsFragment) }
//
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
