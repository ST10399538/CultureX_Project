package com.example.culturex

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.culturex.data.viewmodels.ContentViewModel

class TippingFragment : Fragment(R.layout.fragment_tipping) {

    private val contentViewModel: ContentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryId = arguments?.getString("countryId")
        val categoryId = arguments?.getString("categoryId")

        if (countryId != null && categoryId != null) {
            contentViewModel.loadContent(countryId, categoryId)
            setupObservers(view)
        }

        view.findViewById<View>(R.id.back_arrow)?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupObservers(view: View) {
        contentViewModel.content.observe(viewLifecycleOwner) { content ->
            content?.let {
                view.findViewById<TextView>(R.id.content_title)?.text = it.title ?: "Tipping Norms"
                view.findViewById<TextView>(R.id.content_description)?.text = it.content ?: ""
            }
        }
    }
}
