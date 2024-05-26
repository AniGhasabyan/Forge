package com.example.forge.ui.navbar.tournaments;

import android.app.Activity;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.databinding.FragmentTournamentsBinding;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChosenTournamentsFragment extends Fragment {

    private FragmentTournamentsBinding binding;
    private List<Message> tournamentList;
    private MessageAdapter messageAdapter;
    private TournamentsViewModel tournamentsViewModel;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        AtomicReference<String> userRole = new AtomicReference<>(preferences.getString("UserRole", "Athlete"));

        binding = FragmentTournamentsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AtomicReference<String> userUID = new AtomicReference<>(user.getUid());
        String username, email;

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");

            TextView usernameTextView = binding.textUsername;
            String menuTournaments = getString(R.string.menu_tournaments);
            usernameTextView.setText("This is " + username + "'s " + menuTournaments);
            usernameTextView.setVisibility(View.VISIBLE);

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                }
                initializeViewModel(String.valueOf(userRole), userUID.get());
            });
        } else {
            username = null;
            initializeViewModel(String.valueOf(userRole), userUID.get());
        }

        tournamentsViewModel = new ViewModelProvider(this, new TournamentsViewModelFactory(userRole.get(), userUID.get()))
                .get(TournamentsViewModel.class);

        binding.textTournaments.setText("Double click on a date in the calendar to add a tournament");

        RecyclerView recyclerView = binding.recyclerViewTournaments;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tournamentList = new ArrayList<>();
        messageAdapter = new MessageAdapter(tournamentList, getContext(), userRole.get(), userUID.get());
        recyclerView.setAdapter(messageAdapter);

        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals("UserRole")) {
                userRole.set(sharedPreferences.getString("UserRole", "Athlete"));
                tournamentsViewModel.loadTournaments(userRole.get(), userUID.get());
            }
        });

        CalendarView calendarView = binding.calendarView;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            boolean doubleClick = false;
            long lastDateClicked = 0;

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                long currentDateClicked = view.getDate();

                if (currentDateClicked == lastDateClicked && doubleClick) {
                    showDialogPrompt(year, month, dayOfMonth, username, userUID.get(), userRole.get());
                }

                lastDateClicked = currentDateClicked;
                doubleClick = true;

                view.postDelayed(() -> doubleClick = false, 1000);
            }
        });

        return root;
    }

    private void initializeViewModel(String userRole, String userUID) {
        tournamentsViewModel = new ViewModelProvider(this, new TournamentsViewModelFactory(userRole, userUID))
                .get(TournamentsViewModel.class);

        tournamentsViewModel.loadTournaments(userRole, userUID);

        tournamentsViewModel.getTournamentList().observe(getViewLifecycleOwner(), tournaments -> {
            tournamentList.clear();
            for (String tournament : tournaments) {
                tournamentList.add(new Message(tournament));
            }
            messageAdapter.notifyDataSetChanged();
        });
    }

    private void showDialogPrompt(int year, int month, int dayOfMonth, String username2, String userUID, String userRole) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Tournament Day");
        final EditText input = new EditText(requireContext());
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tournament = input.getText().toString().trim();
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                String tournamentDetails = date + "\n\n" + tournament;
                Bundle bundle = new Bundle();
                bundle.putString("newNoteText", tournamentDetails);
                bundle.putInt("destinationId", R.id.nav_tournaments);
                if (!tournament.isEmpty()) {
                    if(username2 == null && userRole.equals("Coach")){
                        DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                    } else if (username2 != null) {
                        tournamentsViewModel.addTournament(tournamentDetails, userRole, userUID, " - " + username2);
                        NavController navController = Navigation.findNavController(requireView());
                        navController.popBackStack();
                        navController.navigate(R.id.nav_tournaments);
                    } else {
                        tournamentsViewModel.addTournament(tournamentDetails, userRole, userUID, "");
                        NavController navController = Navigation.findNavController(requireView());
                        navController.popBackStack();
                        navController.navigate(R.id.nav_tournaments);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void getUserUIDByEmail(String email, OnSuccessListener<String> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userUID = documentSnapshot.getString("uid");
                        onSuccessListener.onSuccess(userUID);
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}