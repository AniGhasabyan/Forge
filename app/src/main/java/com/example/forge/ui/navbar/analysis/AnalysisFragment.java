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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.databinding.FragmentAnalysisBinding;
import com.example.forge.Message;
import com.example.forge.ui.MessageAdapter;
import com.example.forge.ui.navbar.progress.ProgressViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private FragmentAnalysisBinding binding;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private AnalysisViewModel analysisViewModel;
    private String username;
    private String email;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");
        analysisViewModel =
                new ViewModelProvider(this, new AnalysisViewModelFactory(userRole))
                        .get(AnalysisViewModel.class);

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");
        }

        if (username == null) {
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("email", email);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_porch, bundle);
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
        sendButton.setOnClickListener(view -> onSendButtonClicked(userRole));

        analysisViewModel.loadAnalysisData(userRole);

        return root;
    }

    private void onSendButtonClicked(String userRole) {
        EditText editTextMessage = binding.editTextMessage;
        String messageText = editTextMessage.getText().toString().trim();

        if (!messageText.isEmpty()) {
            analysisViewModel.addMessage(new Message(messageText), userRole);
            editTextMessage.setText("");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
