package com.example.forge.ui.navbar.progress;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private String username;
    private String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        progressViewModel =
                new ViewModelProvider(this).get(ProgressViewModel.class);

        binding = FragmentProgressBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");
        }

        final TextView textView = binding.textProgress;
        progressViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        if (username != null) {
            TextView usernameTextView = binding.textUsername;
            String menuProgress = getString(R.string.menu_progress);
            usernameTextView.setText("This is " + username + "'s " + menuProgress);
            usernameTextView.setVisibility(View.VISIBLE);
        }

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
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_conquest, null);
        builder.setView(dialogView);
        builder.setTitle("Add New Conquest");

        final EditText input = dialogView.findViewById(R.id.editTextNote);

        final RadioGroup radioGroupPlaces = dialogView.findViewById(R.id.radioGroupPlaces);
        final String[] places = {"1st", "2nd", "3rd"};

        SharedPreferences preferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");
        if (userRole.equals("Coach")) {
            List<String> athleteList = new ArrayList<>();

            if (athleteList.isEmpty()) {
                TextView textView = new TextView(requireContext());
                textView.setText("Your athletes will be shown here");
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setPadding(80, 40, 0, 40);
                ((ViewGroup) dialogView.findViewById(R.id.athleteRadioGroupContainer)).addView(textView);
            } else {
                RadioGroup athleteRadioGroup = new RadioGroup(requireContext());
                athleteRadioGroup.setOrientation(RadioGroup.VERTICAL);
                for (String athlete : athleteList) {
                    RadioButton radioButton = new RadioButton(requireContext());
                    radioButton.setText(athlete);
                    athleteRadioGroup.addView(radioButton);
                }
                ((ViewGroup) dialogView.findViewById(R.id.athleteRadioGroupContainer)).addView(athleteRadioGroup);
            }
        }

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNoteText = input.getText().toString().trim();
                int selectedRadioButtonId = radioGroupPlaces.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedRadioButtonId);

                String place;
                if (selectedRadioButton != null) {
                    int radioButtonIndex = radioGroupPlaces.indexOfChild(selectedRadioButton);
                    if (radioButtonIndex >= 0 && radioButtonIndex < places.length) {
                        place = " - " + places[radioButtonIndex];
                    } else {
                        place = "- Proud of my result";
                    }
                } else {
                    place = "";
                }

                if (!newNoteText.isEmpty()) {
                    progressViewModel.addProgressNote(new Message(newNoteText + place));
                } else {
                    Toast.makeText(getContext(), "Conquest text cannot be empty", Toast.LENGTH_SHORT).show();
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