package com.example.forge.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forge.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.SignInMethodQueryResult;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private Button buttonSearch;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_users, container, false);

        editTextSearch = root.findViewById(R.id.edit_text_search);
        buttonSearch = root.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> searchUsers());

        return root;
    }

    private void searchUsers() {
        String email = editTextSearch.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String displayName = mAuth.getCurrentUser().getDisplayName();
                        if (task.getResult().getSignInMethods().size() == 0) {
                            Toast.makeText(requireContext(), "User does not exist with this email.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "User exists with this email. Display name: " + displayName, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(requireContext(), "User does not exist with this email.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Error searching for user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
