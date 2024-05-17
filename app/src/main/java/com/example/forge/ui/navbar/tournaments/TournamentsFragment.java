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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.Message;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.databinding.FragmentTournamentsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentsFragment extends Fragment {

    private FragmentTournamentsBinding binding;
    private List<Message> tournamentList;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TournamentsViewModel tournamentsViewModel =
                new ViewModelProvider(this).get(TournamentsViewModel.class);

        binding = FragmentTournamentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
        }

        final TextView textView = binding.textTournaments;
        tournamentsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        if (username != null) {
            TextView usernameTextView = binding.textUsername;
            usernameTextView.setText("This is " + username);
            usernameTextView.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycler_view_tournaments);
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

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid())
                .collection("tournaments").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("note");
                        tournamentList.add(new Message(noteContent));
                    }

                    messageAdapter.notifyItemInserted(0);
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

                Map<String, Object> noteData = new HashMap<>();

                if (!tournament.isEmpty()) {
                    tournamentList.add(0, new Message(date + "\n" + "\n" + tournament));
                    messageAdapter.notifyItemInserted(0);

                    noteData.put("note", date  + "\n" + "\n" + tournament);

                    db.collection("users").document(user.getUid())
                            .collection("tournaments")
                            .add(noteData);
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