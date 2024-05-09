package com.example.forge.ui.navbar.progress;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.forge.databinding.FragmentProgressBinding;
import com.example.forge.ui.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    private @NonNull FragmentProgressBinding binding;
    private List<Message> progressNotesList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ProgressViewModel progressViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        progressViewModel =
                new ViewModelProvider(this).get(ProgressViewModel.class);

        binding = FragmentProgressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProgress;
        progressViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = root.findViewById(R.id.recyclerViewConquests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressNotesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(progressNotesList);
        recyclerView.setAdapter(messageAdapter);

        progressViewModel.getProgressNotes().observe(getViewLifecycleOwner(), dietNotes -> {
            progressNotesList.clear();
            progressNotesList.addAll(dietNotes);
            messageAdapter.notifyDataSetChanged();
        });

        Button addButton = root.findViewById(R.id.buttonAddConquest);
        addButton.setOnClickListener(v -> {
            showAddNoteDialog();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Conquest");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNoteText = input.getText().toString().trim();
                if (!newNoteText.isEmpty()) {
                    progressViewModel.addProgressNote(new Message(newNoteText));
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
}