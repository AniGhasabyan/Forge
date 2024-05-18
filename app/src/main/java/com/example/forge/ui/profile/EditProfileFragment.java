package com.example.forge.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnSave;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etUsername = view.findViewById(R.id.editTextUsername);
        etEmail = view.findViewById(R.id.editTextEmail);
        etNewPassword = view.findViewById(R.id.editTextNewPassword);
        etConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        btnSave = view.findViewById(R.id.buttonSave);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUsername = currentUser.getDisplayName();
            String currentEmail = currentUser.getEmail();

            etUsername.setText(currentUsername);
            etEmail.setText(currentEmail);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });

        return view;
    }

    private void saveProfileChanges() {
        final String newUsername = etUsername.getText().toString().trim();
        final String newEmail = etEmail.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!newUsername.equals(currentUser.getDisplayName())) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();

                currentUser.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference userRef = db.collection("users").document(currentUser.getUid());

                                    userRef.update("username", newUsername)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Failed to update username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(confirmPassword)) {
                if (newPassword.equals(confirmPassword)) {
                    currentUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to update password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
