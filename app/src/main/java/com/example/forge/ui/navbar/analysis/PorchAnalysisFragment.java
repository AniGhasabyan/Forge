package com.example.forge.ui.navbar.analysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.forge.R;
import com.example.forge.databinding.FragmentPorchAnalysisBinding;
import com.example.forge.ui.UserAdapter;

import java.util.ArrayList;

public class PorchAnalysisFragment extends Fragment {

    private FragmentPorchAnalysisBinding binding;
    private PorchAnalysisViewModel viewModel;
    private UserAdapter adapter1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPorchAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(PorchAnalysisViewModel.class);

        NavController navController = NavHostFragment.findNavController(this);
        int currentDestinationId = navController.getCurrentDestination().getId();

        adapter1 = new UserAdapter(new ArrayList<>(), currentDestinationId, requireContext());
        binding.recyclerViewPorch.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPorch.setAdapter(adapter1);

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        if (userRole.equals("Athlete")) {
            binding.tVPorch.setText("Your coaches will be shown here");
        } else if (userRole.equals("Coach")) {
            binding.tVPorch.setText("Your athletes will be shown here");
        }

        viewModel.getUserList1().observe(getViewLifecycleOwner(), userList1 -> {
            adapter1.setUserList(userList1);
            binding.tVPorch.setVisibility(userList1.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.loadData(requireContext());

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavController navController = NavHostFragment.findNavController(PorchAnalysisFragment.this);
                        if (navController.popBackStack()) {
                            navController.popBackStack();
                        } else {
                            requireActivity().finish();
                        }
                    }
                }
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
