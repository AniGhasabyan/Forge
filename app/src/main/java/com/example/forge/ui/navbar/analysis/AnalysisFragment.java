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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AnalysisFragment extends Fragment {

    private FragmentAnalysisBinding binding;
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private AnalysisViewModel analysisViewModel;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String username, email, userRole;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userRole = preferences.getString("UserRole", "Athlete");

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AtomicReference<String> userUID = new AtomicReference<>(user.getUid());

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");

            TextView usernameTextView = binding.textUsername;
            String menuAnalysis = getString(R.string.menu_analysis);
            usernameTextView.setText("This is " + username + "'s " + menuAnalysis);
            usernameTextView.setVisibility(View.VISIBLE);

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                }
                initializeViewModel(userUID.get());
            });
        } else {
            username = null;
            email = null;
            initializeViewModel(userUID.get());
        }

        if (username == null) {
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("email", email);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_porch, bundle);
        }

        messages = new ArrayList<>();
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recycler_view_analysis);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(messages, getContext(), userRole, userUID.get());
        recyclerView.setAdapter(messageAdapter);

        Button sendButton = binding.buttonSend;
        sendButton.setOnClickListener(view -> onSendButtonClicked(userRole, userUID.get()));

        return root;
    }

    private void initializeViewModel(String userUID) {
        analysisViewModel = new ViewModelProvider(this, new AnalysisViewModelFactory(userRole, userUID))
                .get(AnalysisViewModel.class);

        final TextView textView = binding.textAnalysis;
        analysisViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        analysisViewModel.getMessage().observe(getViewLifecycleOwner(), dietNotes -> {
            messages.clear();
            messages.addAll(dietNotes);
            messageAdapter.notifyDataSetChanged();
        });

        analysisViewModel.loadAnalysisData(userRole, userUID);
    }

    private void onSendButtonClicked(String userRole, String userUID) {
        EditText editTextMessage = binding.editTextMessage;
        String messageText = editTextMessage.getText().toString().trim();

        if (!messageText.isEmpty()) {
            analysisViewModel.addMessage(new Message(messageText), userRole, userUID);
            editTextMessage.setText("");
        }
    }

    private void getUserUIDByEmail(String email, OnSuccessListener<String> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String userUID = documentSnapshot.getString("uid");
                        onSuccessListener.onSuccess(userUID);
                    } else {
                        onSuccessListener.onSuccess(null);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
