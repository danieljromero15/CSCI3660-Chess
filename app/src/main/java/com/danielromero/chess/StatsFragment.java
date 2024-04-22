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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        TextView piecesView = rootView.findViewById(R.id.stats_text_view);
        String br = System.lineSeparator();

        String[] pieceNames = {"wPAWN", "wKNIGHT", "wROOK", "wBISHOP", "wQUEEN", "wKING", "bPAWN", "bKNIGHT", "bROOK", "bBISHOP", "bQUEEN", "bKING"};
        String[] displayNames = getResources().getStringArray(R.array.piece_display_names);

        StringBuilder stats_list = new StringBuilder();
        stats_list.append(getString(R.string.human_moves) + br);
        for(int i = 0; i < pieceNames.length; i++){
            String point = displayNames[i] + ": " + Storage.getInt(pieceNames[i]) + br;
            stats_list.append(point);
            if(i == pieceNames.length/2-1) {
                stats_list.append(br + getString(R.string.p2_moves) + br);
            }
        }
        stats_list.append(br + getString(R.string.wins) + ": " + Storage.getInt("win"));
        stats_list.append(br + getString(R.string.losses) + ": " + Storage.getInt("lose"));

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