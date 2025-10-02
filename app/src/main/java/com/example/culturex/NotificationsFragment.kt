package com.example.culturex

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.culturex.databinding.FragmentEmergencyBinding
import com.example.culturex.databinding.FragmentNotificationsBinding
import com.example.culturex.databinding.FragmentSavedBinding

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    // View binding instance for accessing layout views
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationsBinding.bind(view)

        setupBottomNavigation()
        // TODO: Add logic for showing notifications
    }

    // Function to handle bottom navigation item clicks
    private fun setupBottomNavigation() {
        val bottomNavMap = mapOf(
            binding.navEmergency.id to R.id.emergencyFragment,
            binding.navSaved.id to R.id.savedFragment,
            binding.navNotifications.id to null,
            binding.navHome.id to R.id.mainFragment,
            binding.profileIcon.id to R.id.profileFragment
        )

        bottomNavMap.forEach { (viewId, actionId) ->
            binding.root.findViewById<View>(viewId)?.setOnClickListener {
                actionId?.let { findNavController().navigate(it) }
            }
        }
    }

    // Called when the fragment's view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
