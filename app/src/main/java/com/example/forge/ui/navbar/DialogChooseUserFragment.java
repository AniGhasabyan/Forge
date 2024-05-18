package com.example.forge.ui.navbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.forge.R;
import com.example.forge.databinding.DialogChooseUserBinding;
import com.example.forge.ui.UserAdapter;

import java.util.ArrayList;

public class DialogChooseUserFragment extends DialogFragment {

    private DialogChooseUserBinding binding;
    private DialogChooseUserViewModel viewModel;
    private UserAdapter adapter;

    @Nullable@Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogChooseUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(DialogChooseUserViewModel.class);

        adapter = new UserAdapter(new ArrayList<>(), R.id.nav_dialog);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getUserList1().observe(getViewLifecycleOwner(), userList1 -> {
            adapter.setUserList(userList1);
        });

        viewModel.loadData(requireContext());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
