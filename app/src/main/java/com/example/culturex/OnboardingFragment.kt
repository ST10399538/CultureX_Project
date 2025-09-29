package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.util.Log

class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private lateinit var continueButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton = view.findViewById(R.id.continue_button)

        continueButton.setOnClickListener {
            // Navigate to MainFragment when user clicks Continue/Next
            try {
                findNavController().navigate(R.id.action_onboarding_to_main)
            } catch (e: Exception) {
                Log.e("OnboardingFragment", "Navigation to main failed", e)
                Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}