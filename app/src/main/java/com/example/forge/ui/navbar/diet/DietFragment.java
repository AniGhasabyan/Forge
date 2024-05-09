package com.example.forge.ui.navbar.diet;

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
import com.example.forge.ui.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

import com.example.forge.databinding.FragmentDietBinding;

public class DietFragment extends Fragment {

    private FragmentDietBinding binding;
    private List<Message> dietNotesList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private DietViewModel dietViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dietViewModel =
                new ViewModelProvider(this).get(DietViewModel.class);

        binding = FragmentDietBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDiet;
        dietViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = root.findViewById(R.id.recycler_view_diet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dietNotesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(dietNotesList);
        recyclerView.setAdapter(messageAdapter);

        dietViewModel.getDietNotes().observe(getViewLifecycleOwner(), dietNotes -> {
            dietNotesList.clear();
            dietNotesList.addAll(dietNotes);
            messageAdapter.notifyDataSetChanged();
        });

        Button addButton = root.findViewById(R.id.buttonAddNote);
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
        builder.setTitle("Add Diet Note");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNoteText = input.getText().toString().trim();
                if (!newNoteText.isEmpty()) {
                    dietViewModel.addDietNote(new Message(newNoteText));
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