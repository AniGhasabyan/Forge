package com.example.forge.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.forge.R;

public class ChooseFragment extends Fragment {
    private String username;
    private String email;

    public ChooseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            username = getArguments().getString("username");
            email = getArguments().getString("email");
        }

        return inflater.inflate(R.layout.fragment_choose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout scheduleL = view.findViewById(R.id.ch_schedule);
        LinearLayout progressL = view.findViewById(R.id.ch_progress);
        LinearLayout analysisL = view.findViewById(R.id.ch_analysis);
        LinearLayout tournamentsL = view.findViewById(R.id.ch_tournaments);
        LinearLayout dietL = view.findViewById(R.id.ch_diet);

        scheduleL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(R.id.nav_chosen_schedule);
            }
        });

        progressL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(R.id.nav_chosen_progress);
            }
        });

        analysisL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(R.id.nav_chosen_analysis);
            }
        });

        tournamentsL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(R.id.nav_chosen_tournaments);
            }
        });

        dietL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(R.id.nav_chosen_diet);
            }
        });
    }

    private void openFragment(int destinationId) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("email", email);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(destinationId, bundle);
    }
}
