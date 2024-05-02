package com.danielromero.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController

class AboutFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_about, container, false)

        // simply gets the string array and puts it all into the textview
        val contributorNames = resources.getStringArray(R.array.contributor_names)
        val namesView = rootView.findViewById<TextView>(R.id.contributor_names)

        val namesList = StringBuilder()
        for (name in contributorNames) {
            namesList.append(name).append(System.lineSeparator()).append(System.lineSeparator())
        }

        namesView.text = namesList.toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.gameFragment)
        }

        return rootView
    }
}