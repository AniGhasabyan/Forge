package com.example.forge.ui.navbar.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.databinding.FragmentHomeBinding;
import com.example.forge.ui.navbar.home.HomeUserAdapter;
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
    private RecyclerView recyclerView;
    private HomeUserAdapter userAdapter;
    private List<User> userList;
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

        return root;
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
