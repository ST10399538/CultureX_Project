package com.example.culturex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TippingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the tipping layout
        val view = inflater.inflate(R.layout.fragment_tipping, container, false)

        // Handle back arrow click
        view.findViewById<View>(R.id.back_arrow).setOnClickListener {
            // Navigate back
            findNavController().popBackStack()
        }

        // Handle Save for Offline click
        view.findViewById<View>(R.id.save_offline_card).setOnClickListener {
            // Replace this with your save logic
            Toast.makeText(requireContext(), "Saved for offline viewing", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
