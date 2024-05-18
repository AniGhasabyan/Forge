package com.example.forge.ui.navbar.tournaments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.databinding.FragmentTournamentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TournamentsFragment extends Fragment {

    private FragmentTournamentsBinding binding;
    private List<Message> tournamentList;
    private MessageAdapter messageAdapter;
    private TournamentsViewModel tournamentsViewModel;
    private String username;
    private String email;
    private String userRole;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        binding = FragmentTournamentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textTournaments.setText("Double click on a date in the calendar to add a tournament");

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");
        }

        if (username != null) {
            TextView usernameTextView = binding.textUsername;
            String menuTournaments = getString(R.string.menu_tournaments);
            usernameTextView.setText("This is " + username + "'s " + menuTournaments);
            usernameTextView.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = binding.recyclerViewTournaments;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tournamentList = new ArrayList<>();
        messageAdapter = new MessageAdapter(tournamentList);
        recyclerView.setAdapter(messageAdapter);

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

        tournamentsViewModel = new ViewModelProvider(this, new TournamentsViewModelFactory(preferences.getString("UserRole", "Athlete")))
                .get(TournamentsViewModel.class);

        tournamentsViewModel.getTournamentList().observe(getViewLifecycleOwner(), tournaments -> {
            tournamentList.clear();
            for (String tournament : tournaments) {
                tournamentList.add(new Message(tournament));
            }
            messageAdapter.notifyDataSetChanged();
        });

        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("UserRole")) {
                userRole = sharedPreferences.getString("UserRole", "Athlete");
                tournamentsViewModel.loadTournaments(userRole);
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
                    String tournamentDetails = date + "\n\n" + tournament;
                    tournamentsViewModel.addTournament(tournamentDetails, userRole);
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
