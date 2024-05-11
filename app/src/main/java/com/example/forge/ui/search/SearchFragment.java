package com.example.forge.ui.search;

import android.content.Context; // Add this import statement
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private Button buttonSearch;
    private RecyclerView recyclerViewSearch;
    private SearchUserAdapter searchUserAdapter;
    private List<User> userList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search_users, container, false);

        editTextSearch = root.findViewById(R.id.edit_text_search);
        buttonSearch = root.findViewById(R.id.button_search);
        recyclerViewSearch = root.findViewById(R.id.recyclerViewSearch);

        userList = new ArrayList<>();
        searchUserAdapter = new SearchUserAdapter(getContext(), userList);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearch.setAdapter(searchUserAdapter);

        buttonSearch.setOnClickListener(v -> searchUsers());

        return root;
    }

    private void searchUsers() {
        String email = editTextSearch.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextSearch.setError("Invalid email address");
            editTextSearch.requestFocus();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        searchUserAdapter.notifyDataSetChanged();
                        if (userList.isEmpty()) {
                            Toast.makeText(getContext(), "No user with this email exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    editTextSearch.setText("");
                });
    }
}
