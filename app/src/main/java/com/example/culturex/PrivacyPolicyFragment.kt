package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.util.Log

class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    // Called when the fragment's view is created and ready
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup back button functionality
        setupBackButton(view)
        // Log that the Privacy Policy screen has been loaded
        Log.d("PrivacyPolicyFragment", "Privacy Policy screen loaded")
    }

    // Helper method to set up the back button behavior
    private fun setupBackButton(view: View) {
        val backArrow = view.findViewById<ImageView>(R.id.back_arrow)
        backArrow?.setOnClickListener {
            try {
                findNavController().navigateUp()
                Log.d("PrivacyPolicyFragment", "Navigating back from Privacy Policy")
            } catch (e: Exception) {
                Log.e("PrivacyPolicyFragment", "Back navigation failed", e)
                activity?.onBackPressed()
            }
        }
    }

    // Called when the fragment comes to the foreground
    override fun onResume() {
        super.onResume()
        Log.d("PrivacyPolicyFragment", "Privacy Policy screen resumed")
    }

    // Called when the fragment is no longer in the foreground
    override fun onPause() {
        super.onPause()
        Log.d("PrivacyPolicyFragment", "Privacy Policy screen paused")
    }
}

//Reference List:
// UiLover, 2025. Build a Coffee Shop app with Kotlin & Firebase in Android Studio Project. [video online]. Available at: https://www.youtube.com/watch?v=Pnw_9tZ2z4wn [Accessed on 16 September 2025]
// Guedmioui, A. 2023. Retrofit Android Tutorial - Make API Calls. [video online]. Available at: https://www.youtube.com/watch?v=8IhNq0ng-wk [Accessed on 14 September 2025]
// Code Heroes, 2024.Integrate Google Maps API in Android Studio 2025 | Step-by-Step Tutorial for Beginners. [video online]. Available at: https://www.youtube.com/watch?v=QVCNTPNy-vs&t=137s [Accessed on 17 September 2025]
// CodeSchmell, 2022. How to implement API in Android Studio tutorial. [video online]. Available at: https://www.youtube.com/watch?v=Kjeh47epMqI [Accessed on 17 September 2025]
// UiLover, 2023. Travel App Android Studio Tutorial Project - Android Material Design. [video online]. Available at: https://www.youtube.com/watch?v=PPhuxay3OV0 [Accessed on 12 September 2025]
// CodeWithTS, 2024. View Binding and Data Binding in Android Studio using Kotlin. [video online]. Available at: https://www.youtube.com/watch?v=tIXSuoJbX-8  [Accessed on 20 September 2025]
// Android Developers, 2025. Develop a UI with Views. [online]. Available at: https://developer.android.com/studio/write/layout-editor [Accessed on 15 September 2025]