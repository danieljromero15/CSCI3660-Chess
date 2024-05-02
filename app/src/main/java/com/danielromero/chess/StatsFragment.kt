package com.danielromero.chess;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        TextView piecesView = rootView.findViewById(R.id.stats_text_view);
        String br = System.lineSeparator();

        String[] pieceNames = {"wPAWN", "wKNIGHT", "wROOK", "wBISHOP", "wQUEEN", "wKING", "bPAWN", "bKNIGHT", "bROOK", "bBISHOP", "bQUEEN", "bKING"};
        String[] displayNames = getResources().getStringArray(R.array.piece_display_names);

        StringBuilder stats_list = new StringBuilder();
        stats_list.append(getString(R.string.human_moves)).append(br);
        for (int i = 0; i < pieceNames.length; i++) {
            String point = displayNames[i] + ": " + Storage.getInt(pieceNames[i]) + br;
            stats_list.append(point);
            if (i == pieceNames.length / 2 - 1) {
                stats_list.append(br).append(getString(R.string.p2_moves)).append(br);
            }
        }
        stats_list.append(br).append(getString(R.string.wins)).append(": ").append(Storage.getInt("win"));
        stats_list.append(br).append(getString(R.string.losses)).append(": ").append(Storage.getInt("lose"));

        Log.d("text", String.valueOf(piecesView));
        piecesView.setText(stats_list.toString());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Log.d("back", "back pressed! running code");
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.gameFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Inflate the layout for this fragment
        return rootView;
    }
}