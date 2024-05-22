package com.example.forge.ui.navbar.progress;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.Message;
import com.example.forge.databinding.FragmentProgressBinding;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private List<Message> progressNotesList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ProgressViewModel progressViewModel;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        binding = FragmentProgressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AtomicReference<String> userUID = new AtomicReference<>(user.getUid());
        String username;
        String email = null;

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");

            TextView usernameTextView = binding.textUsername;
            String menuProgress = getString(R.string.menu_progress);
            usernameTextView.setText("This is " + username + "'s " + menuProgress);
            usernameTextView.setVisibility(View.VISIBLE);

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                }
                initializeViewModel(userRole, userUID.get());
            });
        } else {
            username = null;
            initializeViewModel(userRole, userUID.get());
        }

        progressNotesList = new ArrayList<>();
        recyclerView = binding.getRoot().findViewById(R.id.recyclerViewConquests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageAdapter = new MessageAdapter(progressNotesList, getContext(), userRole, userUID.get());
        recyclerView.setAdapter(messageAdapter);

        Button addButton = binding.buttonAddConquest;
        addButton.setOnClickListener(v -> showAddNoteDialog(userRole, username, userUID.get()));

        return root;
    }

    private void initializeViewModel(String userRole, String userUID) {
        progressViewModel = new ViewModelProvider(this, new ProgressViewModelFactory(userRole, userUID))
                .get(ProgressViewModel.class);

        final TextView textView = binding.textProgress;
        progressViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        progressViewModel.getProgressNotes().observe(getViewLifecycleOwner(), progressNotes -> {
            progressNotesList.clear();
            progressNotesList.addAll(progressNotes);
            messageAdapter.notifyDataSetChanged();
        });

        progressViewModel.loadProgressNotes(userRole, userUID);
    }

    private void showAddNoteDialog(String userRole, String username2, String userUID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_conquest, null);
        builder.setView(dialogView);
        builder.setTitle("Add New Conquest");

        final EditText input = dialogView.findViewById(R.id.editTextNote);

        final RadioGroup radioGroupPlaces = dialogView.findViewById(R.id.radioGroupPlaces);
        final String[] places = {"1st", "2nd", "3rd"};

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNoteText = input.getText().toString().trim();
                int selectedRadioButtonId = radioGroupPlaces.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedRadioButtonId);

                String place;
                if (selectedRadioButton != null) {
                    int radioButtonIndex = radioGroupPlaces.indexOfChild(selectedRadioButton);
                    if (radioButtonIndex >= 0 && radioButtonIndex < places.length) {
                        place = " - " + places[radioButtonIndex];
                    } else {
                        place = "- Proud of my result";
                    }
                } else {
                    place = "";
                }

                if (!newNoteText.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("newNoteText", newNoteText + place);
                    bundle.putInt("destinationId", R.id.nav_progress);
                    if (username2 == null && userRole.equals("Coach")) {
                        DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                    } else if (username2 != null) {
                        progressViewModel.addProgressNote(new Message(newNoteText + place), userRole, userUID);
                    } else {
                        progressViewModel.addProgressNote(new Message(newNoteText + place), userRole, user.getUid());
                    }
                } else {
                    Toast.makeText(getContext(), "Conquest text cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
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
