package com.example.forge.ui.navbar.analysis;

import android.os.Bundle;
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
import com.example.forge.databinding.FragmentAnalysisBinding;
import com.example.forge.Message;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.ui.navbar.diet.DietViewModel;

import java.util.ArrayList;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private FragmentAnalysisBinding binding;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private AnalysisViewModel analysisViewModel;
    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        analysisViewModel =
                new ViewModelProvider(this).get(AnalysisViewModel.class);

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
        }

        final TextView textView = binding.textAnalysis;
        analysisViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        if (username != null) {
            TextView usernameTextView = binding.textUsername;
            String menuAnalysis = getString(R.string.menu_analysis);
            usernameTextView.setText("This is " + username + "'s " + menuAnalysis);
            usernameTextView.setVisibility(View.VISIBLE);
        }

        messages = new ArrayList<>();
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycler_view_analysis);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);

        analysisViewModel.getMessage().observe(getViewLifecycleOwner(), dietNotes -> {
            messages.clear();
            messages.addAll(dietNotes);
            messageAdapter.notifyDataSetChanged();
        });

        Button sendButton = binding.buttonSend;
        sendButton.setOnClickListener(view -> onSendButtonClicked());

        return root;
    }

    private void onSendButtonClicked() {
        EditText editTextMessage = binding.editTextMessage;
        String messageText = editTextMessage.getText().toString().trim();

        if (!messageText.isEmpty()) {
            analysisViewModel.addMessage(new Message(messageText));
            editTextMessage.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
