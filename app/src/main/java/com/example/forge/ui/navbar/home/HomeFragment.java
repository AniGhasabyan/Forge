package com.example.forge.ui.navbar.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.databinding.FragmentHomeBinding;
import com.example.forge.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private HomeUserAdapter adapter1, adapter2, adapter3;
    private List<User> userList1, userList2, userList3;
    private String userRole;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userRole = preferences.getString("UserRole", "Athlete");

        if (userRole.equals("Athlete")) {
            binding.tVHome1.setText("Your Coaches");
            binding.tVHome2.setText("Coaches Requested to Train You");
            binding.tVHome3.setText("Coaches You're Interested in");
        } else if (userRole.equals("Coach")) {
            binding.tVHome1.setText("Your Athletes");
            binding.tVHome2.setText("Athletes Interested in Your Coaching");
            binding.tVHome3.setText("Your Coaching Requests");
        }

        recyclerView1 = root.findViewById(R.id.recycler_view_home_1);
        recyclerView2 = root.findViewById(R.id.recycler_view_home_2);
        recyclerView3 = root.findViewById(R.id.recycler_view_home_3);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));

        userList1 = new ArrayList<>();
        userList2 = new ArrayList<>();
        userList3 = new ArrayList<>();

        adapter1 = new HomeUserAdapter(requireContext(), userList1);
        adapter2 = new HomeUserAdapter(requireContext(), userList2);
        adapter3 = new HomeUserAdapter(requireContext(), userList3);

        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);
        recyclerView3.setAdapter(adapter3);

        binding.textViewEmpty1.setVisibility(userList1.isEmpty() ? View.VISIBLE : View.GONE);
        binding.textViewEmpty2.setVisibility(userList2.isEmpty() ? View.VISIBLE : View.GONE);
        binding.textViewEmpty3.setVisibility(userList3.isEmpty() ? View.VISIBLE : View.GONE);

        loadData();

        return root;
    }

    private void loadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            CollectionReference collectionReference1, collectionReference2, collectionReference3;
            if (userRole.equals("Athlete")) {
                collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Coaches");
                collectionReference2 = db.collection("users").document(currentUserUID).collection("Coaches Requested to Train You");
                collectionReference3 = db.collection("users").document(currentUserUID).collection("Coaches You're Interested in");
            } else {
                collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Athletes");
                collectionReference2 = db.collection("users").document(currentUserUID).collection("Athletes Interested in Your Coaching");
                collectionReference3 = db.collection("users").document(currentUserUID).collection("Your Coaching Requests");
            }

            collectionReference1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList1.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList1.add(user);
                    }
                    adapter1.notifyDataSetChanged();
                    binding.textViewEmpty1.setVisibility(userList1.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            collectionReference2.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList2.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList2.add(user);
                    }
                    adapter2.notifyDataSetChanged();
                    binding.textViewEmpty2.setVisibility(userList2.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            collectionReference3.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList3.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList3.add(user);
                    }
                    adapter3.notifyDataSetChanged();
                    binding.textViewEmpty3.setVisibility(userList3.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
