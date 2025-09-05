package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class CommunicationFragment : Fragment(R.layout.fragment_communication) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button listener
        view.findViewById<View>(R.id.back_arrow).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
