package com.example.forge.ui.navbar.analysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.forge.R;
import com.example.forge.databinding.FragmentPorchAnalysisBinding;

public class PorchAnalysisFragment extends Fragment {

    private FragmentPorchAnalysisBinding binding;
    private String username;
    private String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPorchAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");
        }

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        if (userRole.equals("Athlete")) {
            binding.tVPorch.setText("Your coaches will be shown here");
        } else if (userRole.equals("Coach")) {
            binding.tVPorch.setText("Your athletes will be shown here");
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
