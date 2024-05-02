package com.danielromero.chess

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController

class StatsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_stats, container, false)
        val piecesView = rootView.findViewById<TextView>(R.id.stats_text_view)
        val br = System.lineSeparator()

        val pieceNames = arrayOf(
            "wPAWN",
            "wKNIGHT",
            "wROOK",
            "wBISHOP",
            "wQUEEN",
            "wKING",
            "bPAWN",
            "bKNIGHT",
            "bROOK",
            "bBISHOP",
            "bQUEEN",
            "bKING"
        )
        val displayNames = resources.getStringArray(R.array.piece_display_names)

        val stats_list = StringBuilder()
        stats_list.append(getString(R.string.human_moves)).append(br)
        for (i in pieceNames.indices) {
            val point = displayNames[i] + ": " + Storage.getInt(pieceNames[i]) + br
            stats_list.append(point)
            if (i == pieceNames.size / 2 - 1) {
                stats_list.append(br).append(getString(R.string.p2_moves)).append(br)
            }
        }
        stats_list.append(br).append(getString(R.string.wins)).append(": ")
            .append(Storage.getInt("win"))
        stats_list.append(br).append(getString(R.string.losses)).append(": ")
            .append(Storage.getInt("lose"))

        Log.d("text", piecesView.toString())
        piecesView.text = stats_list.toString()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.gameFragment)
        }

        // Inflate the layout for this fragment
        return rootView
    }
}