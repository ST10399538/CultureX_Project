package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val continueButton: Button = view.findViewById(R.id.continue_button)
        continueButton.setOnClickListener {
            // Navigate to MainFragment
            findNavController().navigate(R.id.action_onboarding_to_main)
        }
    }
}
