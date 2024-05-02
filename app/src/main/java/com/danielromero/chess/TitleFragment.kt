package com.danielromero.chess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

class TitleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startGameButton = view.findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.gameFragment)
        }

        val load = Bundle() // I don't even have to put anything here for it to pass notNull lol
        //load.putInt("load", 0);
        val loadGameButton = view.findViewById<Button>(R.id.loadGameButton)
        loadGameButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.gameFragment, load)
        }
    }
}