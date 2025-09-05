package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class DressCodeFragment : Fragment(R.layout.fragment_dress_code) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button listener
        view.findViewById<View>(R.id.back_arrow).setOnClickListener {
            findNavController().navigateUp()
        }

        // Save for Offline Viewing button
        view.findViewById<View>(R.id.save_offline_card)?.setOnClickListener {
            // TODO: Implement offline saving functionality
        }
    }
}
