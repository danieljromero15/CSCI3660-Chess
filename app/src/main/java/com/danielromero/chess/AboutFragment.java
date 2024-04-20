package com.danielromero.chess;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        // simply gets the string array and puts it all into the textview
        String[] contributor_names = getResources().getStringArray(R.array.contributor_names);
        TextView namesView = rootView.findViewById(R.id.contributor_names);

        StringBuilder names_list = new StringBuilder();
        for (String name : contributor_names) {
            names_list.append(name).append(System.lineSeparator()).append(System.lineSeparator());
        }

        namesView.setText(names_list.toString());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("back", "back pressed! running code");
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.gameFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return rootView;
    }
}