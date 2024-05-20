package com.example.forge.ui.navbar.diet;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.Message;
import com.example.forge.ui.MessageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.example.forge.databinding.FragmentDietBinding;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DietFragment extends Fragment {

    private FragmentDietBinding binding;
    private List<Message> dietNotesList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private DietViewModel dietViewModel;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        binding = FragmentDietBinding.inflate(inflater, container, false);
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
            String menuDiet = getString(R.string.menu_diet);
            usernameTextView.setText("This is " + username + "'s " + menuDiet);
            usernameTextView.setVisibility(View.VISIBLE);

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                }
            });

        } else {
            username = null;
            email = null;
        }

        dietViewModel = new ViewModelProvider(this, new DietViewModelFactory(userRole, userUID.get()))
                .get(DietViewModel.class);

        final TextView textView = binding.textDiet;
        dietViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = root.findViewById(R.id.recycler_view_diet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dietNotesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(dietNotesList, getContext(), userRole, userUID.get());
        recyclerView.setAdapter(messageAdapter);


        dietViewModel.getDietNotes().observe(getViewLifecycleOwner(), dietNotes -> {
            dietNotesList.clear();
            dietNotesList.addAll(dietNotes);
            messageAdapter.notifyDataSetChanged();
        });

        dietViewModel.loadDietNotes(userRole, userUID.get());

        Button addButton = root.findViewById(R.id.buttonAddNote);
        addButton.setOnClickListener(v -> {
            showAddNoteDialog(userRole, username, userUID.get());
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showAddNoteDialog(String userRole, String username2, String userUID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Diet Note");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNoteText = input.getText().toString().trim();
                if (!newNoteText.isEmpty()) {
                    if(username2 == null && userRole.equals("Coach")){
                        DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                        dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                    } else if (username2 != null){
                        dietViewModel.addDietNote(new Message(newNoteText), userRole, userUID);
                    } else {
                        dietViewModel.addDietNote(new Message(newNoteText), userRole, user.getUid());
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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
}