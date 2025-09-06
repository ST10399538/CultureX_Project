package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        // ---- Country Spinner Setup ----
        val countries = resources.getStringArray(R.array.countries)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            countries
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.countrySpinner.adapter = adapter

        binding.countrySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = countries[position]
                // TODO: handle country change (e.g., update content)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                // Optional: handle no selection
            }
        }


        // ---- Menu item clicks ----
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
        // ---- Bottom Navigation ----
        binding.navEmergency.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_emergencyFragment)
        }
        binding.navSaved.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_savedFragment)
        }
        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_notificationsFragment)
        }
        binding.navHome.setOnClickListener {
            // Already in MainFragment
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
