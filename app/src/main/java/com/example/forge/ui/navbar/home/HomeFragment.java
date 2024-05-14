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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.forge.User;
import com.example.forge.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeUserAdapter adapter1, adapter2, adapter3;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        adapter1 = new HomeUserAdapter(requireContext(), new ArrayList<>());
        adapter2 = new HomeUserAdapter(requireContext(), new ArrayList<>());
        adapter3 = new HomeUserAdapter(requireContext(), new ArrayList<>());

        binding.recyclerViewHome1.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHome1.setAdapter(adapter1);
        binding.recyclerViewHome2.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHome2.setAdapter(adapter2);
        binding.recyclerViewHome3.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHome3.setAdapter(adapter3);

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");
        if (userRole.equals("Athlete")) {
            binding.tVHome1.setText("Your Coaches");
            binding.tVHome2.setText("Coaches Requested to Train You");
            binding.tVHome3.setText("Coaches You're Interested in");
        } else if (userRole.equals("Coach")) {
            binding.tVHome1.setText("Your Athletes");
            binding.tVHome2.setText("Athletes Interested in Your Coaching");
            binding.tVHome3.setText("Your Coaching Requests");
        }

        observeViewModel();

        viewModel.loadData(requireContext());

        return root;
    }

    private void observeViewModel() {
        viewModel.getUserList1().observe(getViewLifecycleOwner(), userList1 -> {
            adapter1.setUserList(userList1);
            binding.textViewEmpty1.setVisibility(userList1.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getUserList2().observe(getViewLifecycleOwner(), userList2 -> {
            adapter2.setUserList(userList2);
            binding.textViewEmpty2.setVisibility(userList2.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getUserList3().observe(getViewLifecycleOwner(), userList3 -> {
            adapter3.setUserList(userList3);
            binding.textViewEmpty3.setVisibility(userList3.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
