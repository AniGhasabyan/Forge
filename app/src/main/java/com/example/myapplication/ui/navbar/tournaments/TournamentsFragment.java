package com.example.myapplication.ui.navbar.tournaments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentTournamentsBinding;

public class TournamentsFragment extends Fragment{

    private FragmentTournamentsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TournamentsViewModel tournamentsViewModel =
                new ViewModelProvider(this).get(TournamentsViewModel.class);

        binding = FragmentTournamentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTournaments;
        tournamentsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
