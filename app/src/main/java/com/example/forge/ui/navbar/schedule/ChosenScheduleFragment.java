package com.example.forge.ui.navbar.schedule;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.forge.R;
import com.example.forge.databinding.FragmentScheduleBinding;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ChosenScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private TextView mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView,
            fridayTextView, saturdayTextView, sundayTextView;
    private ScheduleViewModel scheduleViewModel;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AtomicReference<String> userUID = new AtomicReference<>(null);
        String username;
        String email = null;

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");

            TextView usernameTextView = binding.textUsername;
            String menuSchedule = getString(R.string.menu_schedule);
            usernameTextView.setText("This is " + username + "'s " + menuSchedule);
            usernameTextView.setVisibility(View.VISIBLE);

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                    initializeViewModel(userRole, userUID.get(), username);
                }
            });
        } else {
            username = null;
            if (user != null) {
                userUID.set(user.getUid());
                initializeViewModel(userRole, userUID.get(), username);
            }
        }

        mondayTextView = root.findViewById(R.id.mondayText);
        tuesdayTextView = root.findViewById(R.id.tuesdayText);
        wednesdayTextView = root.findViewById(R.id.wednesdayText);
        thursdayTextView = root.findViewById(R.id.thursdayText);
        fridayTextView = root.findViewById(R.id.fridayText);
        saturdayTextView = root.findViewById(R.id.saturdayText);
        sundayTextView = root.findViewById(R.id.sundayText);

        setDayClickListener(mondayTextView, username, userRole, userUID);
        setDayClickListener(tuesdayTextView, username, userRole, userUID);
        setDayClickListener(wednesdayTextView, username, userRole, userUID);
        setDayClickListener(thursdayTextView, username, userRole, userUID);
        setDayClickListener(fridayTextView, username, userRole, userUID);
        setDayClickListener(saturdayTextView, username, userRole, userUID);
        setDayClickListener(sundayTextView, username, userRole, userUID);

        setDayLongClickListener(mondayTextView, userRole, userUID);
        setDayLongClickListener(tuesdayTextView, userRole, userUID);
        setDayLongClickListener(wednesdayTextView, userRole, userUID);
        setDayLongClickListener(thursdayTextView, userRole, userUID);
        setDayLongClickListener(fridayTextView, userRole, userUID);
        setDayLongClickListener(saturdayTextView, userRole, userUID);
        setDayLongClickListener(sundayTextView, userRole, userUID);

        return root;
    }

    private void setDayLongClickListener(TextView textView, String userRole, AtomicReference<String> userUID) {
        textView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Clear Training Sessions")
                    .setMessage("Do you want to clear the training session(s) for " + textView.getTag() + "?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        String dayOfWeek = getDayOfWeekFromView(textView);
                        clearTrainingSessions(userRole, userUID.get(), dayOfWeek);
                        textView.setText("");
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        });
    }

    private void clearTrainingSessions(String userRole, String userUID, String dayOfWeek) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String oppositeRole = userRole.equals("Athlete") ? "Coach" : "Athlete";

        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("schedule").document(userUID)
                .collection(dayOfWeek)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
        db.collection(oppositeRole.toLowerCase()).document(userUID)
                .collection("schedule").document(userUID)
                .collection(dayOfWeek)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("schedule").document(user.getUid())
                .collection(dayOfWeek)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
        db.collection(oppositeRole.toLowerCase()).document(userUID)
                .collection("schedule").document(user.getUid())
                .collection(dayOfWeek)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
    }

    private void initializeViewModel(String userRole, String userUID, String username) {
        scheduleViewModel = new ViewModelProvider(this, new ScheduleViewModelFactory(userRole, userUID))
                .get(ScheduleViewModel.class);

        scheduleViewModel.loadScheduleData(userRole, userUID);

        scheduleViewModel.getScheduleData().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> scheduleMap) {
                updateTextView(mondayTextView, scheduleMap.get("monday"));
                updateTextView(tuesdayTextView, scheduleMap.get("tuesday"));
                updateTextView(wednesdayTextView, scheduleMap.get("wednesday"));
                updateTextView(thursdayTextView, scheduleMap.get("thursday"));
                updateTextView(fridayTextView, scheduleMap.get("friday"));
                updateTextView(saturdayTextView, scheduleMap.get("saturday"));
                updateTextView(sundayTextView, scheduleMap.get("sunday"));
            }
        });
    }

    private void updateTextView(TextView textView, String text) {
        if (textView != null && text != null) {
            textView.setText(text);
        }
    }

    private void setDayClickListener(TextView textView, String username, String userRole, AtomicReference<String> userUID) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(textView, username, userRole, userUID.get());
            }
        });
    }

    private void showTimePicker(final TextView textView, String username, String userRole, String userUID) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = hourOfDay + ":" + minute;
                        String currentText = textView.getText().toString().trim();
                        if (!currentText.isEmpty()) {
                            currentText += "\n" + selectedTime;
                        } else {
                            currentText = selectedTime;
                        }
                        textView.setText(currentText);
                        String dayOfWeek = getDayOfWeekFromView(textView);
                        Bundle bundle = new Bundle();
                        bundle.putString("newNoteText", currentText);
                        bundle.putInt("destinationId", R.id.nav_schedule);
                        bundle.putString("userRole", userRole);
                        bundle.putString("dayOfWeek", dayOfWeek);
                        if (Objects.equals(userUID, user.getUid())){
                            DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                            dialogFragment.setArguments(bundle);
                            dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                        } else if (userUID != null) {
                            saveTimeToViewModel(textView, selectedTime, username, userRole, userUID);
                        }
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void saveTimeToViewModel(TextView textView, String selectedTime, String username, String userRole, String userUID) {
        String dayOfWeek = getDayOfWeekFromView(textView);
        if (dayOfWeek != null) {
            scheduleViewModel.saveTime(dayOfWeek, selectedTime, username, userRole, userUID);
        }
    }

    private String getDayOfWeekFromView(View v) {
        String day = "";

        Object tag = v.getTag();
        if (tag instanceof String) {
            day = (String) tag;
        }

        return day;
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
        SharedPreferences preferences = getActivity().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("monday", mondayTextView.getText().toString());
        editor.putString("tuesday", tuesdayTextView.getText().toString());
        editor.putString("wednesday", wednesdayTextView.getText().toString());
        editor.putString("thursday", thursdayTextView.getText().toString());
        editor.putString("friday", fridayTextView.getText().toString());
        editor.putString("saturday", saturdayTextView.getText().toString());
        editor.putString("sunday", sundayTextView.getText().toString());
        editor.apply();
    }
}