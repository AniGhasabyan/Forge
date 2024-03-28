package com.example.forge.ui.navbar.tournaments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.ui.Message;
import com.example.forge.ui.Adapter;
import com.example.forge.databinding.FragmentTournamentsBinding;

import java.util.ArrayList;
import java.util.List;

public class TournamentsFragment extends Fragment {

    private FragmentTournamentsBinding binding;
    private List<Message> tournamentList;
    private Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TournamentsViewModel tournamentsViewModel =
                new ViewModelProvider(this).get(TournamentsViewModel.class);

        binding = FragmentTournamentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTournaments;
        tournamentsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycler_view_tournaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tournamentList = new ArrayList<>();
        adapter = new Adapter(tournamentList);
        recyclerView.setAdapter(adapter);

        CalendarView calendarView = binding.calendarView;

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            boolean doubleClick = false;
            long lastDateClicked = 0;

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                long currentDateClicked = view.getDate();

                if (currentDateClicked == lastDateClicked && doubleClick) {
                    showDialogPrompt(year, month, dayOfMonth);
                }

                lastDateClicked = currentDateClicked;
                doubleClick = true;

                view.postDelayed(() -> doubleClick = false, 1000);
            }
        });

        return root;
    }

    private void showDialogPrompt(int year, int month, int dayOfMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Tournament Day");
        final EditText input = new EditText(requireContext());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tournament = input.getText().toString().trim();
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                if (!tournament.isEmpty()) {
                    tournamentList.add(0, new Message(date + "\n" + "\n" + tournament));
                    adapter.notifyItemInserted(0);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
