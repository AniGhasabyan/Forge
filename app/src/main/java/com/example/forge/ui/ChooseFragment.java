package com.example.forge.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forge.R;

public class ChooseFragment extends Fragment {

    public ChooseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    }
}
