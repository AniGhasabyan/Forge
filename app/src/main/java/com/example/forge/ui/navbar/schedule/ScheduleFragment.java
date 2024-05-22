package com.example.forge.ui.navbar.schedule;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;
import com.example.forge.databinding.FragmentScheduleBinding;
import com.example.forge.ui.UserAdapter;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.example.forge.ui.navbar.diet.DietViewModel;
import com.example.forge.ui.navbar.diet.DietViewModelFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ScheduleFragment extends Fragment {
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

        AtomicReference<String> userUID = new AtomicReference<>(user.getUid());
        String username, email;

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
                }
            });
        } else {
            username = null;
            email = null;
        }

        scheduleViewModel = new ViewModelProvider(this, new ScheduleViewModelFactory(userRole, userUID.get()))
                .get(ScheduleViewModel.class);

        mondayTextView = root.findViewById(R.id.mondayText);
        tuesdayTextView = root.findViewById(R.id.tuesdayText);
        wednesdayTextView = root.findViewById(R.id.wednesdayText);
        thursdayTextView = root.findViewById(R.id.thursdayText);
        fridayTextView = root.findViewById(R.id.fridayText);
        saturdayTextView = root.findViewById(R.id.saturdayText);
        sundayTextView = root.findViewById(R.id.sundayText);

        View.OnClickListener dayClickListener = v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireActivity(),
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = selectedHour + ":" + selectedMinute;
                        TextView textView = (TextView) v;

                        String currentText = textView.getText().toString().trim();
                        if (!currentText.isEmpty()) {
                            currentText += "\n" + selectedTime;
                        } else {
                            currentText = selectedTime;
                        }

                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textView.setText(currentText);

                        Bundle bundle = new Bundle();
                        bundle.putString("newNoteText", currentText + " - " + username);
                        bundle.putInt("destinationId", R.id.nav_schedule);
                        if (username != null) {
                            scheduleViewModel.saveTime(getDayOfWeekFromView(v), selectedTime, username, userRole, userUID.get());
                            textView.setText(currentText + " - " + username);
                        } else {
                            DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                            dialogFragment.setArguments(bundle);
                            dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                        }
                    },
                    hour,
                    minute,
                    true
            );
            timePickerDialog.show();
        };

        mondayTextView.setOnClickListener(dayClickListener);
        tuesdayTextView.setOnClickListener(dayClickListener);
        wednesdayTextView.setOnClickListener(dayClickListener);
        thursdayTextView.setOnClickListener(dayClickListener);
        fridayTextView.setOnClickListener(dayClickListener);
        saturdayTextView.setOnClickListener(dayClickListener);
        sundayTextView.setOnClickListener(dayClickListener);

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        AtomicReference<String> userUID = new AtomicReference<>(user.getUid());
        String email;

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email", "");

            getUserUIDByEmail(email, useruid -> {
                if (useruid != null) {
                    userUID.set(useruid);
                }
            });
        } else {
            email = null;
        }

        observeScheduleData(userRole, userUID.get());
    }

    private void observeScheduleData(String userRole, String userUID) {
        float textSize = 20;
        scheduleViewModel.loadScheduleData(userRole, userUID, "monday").observe(getViewLifecycleOwner(), scheduleMap -> {
            mondayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            mondayTextView.setText(scheduleMap.get("monday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "tuesday").observe(getViewLifecycleOwner(), scheduleMap -> {
            tuesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            tuesdayTextView.setText(scheduleMap.get("tuesday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "wednesday").observe(getViewLifecycleOwner(), scheduleMap -> {
            wednesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            wednesdayTextView.setText(scheduleMap.get("wednesday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "thursday").observe(getViewLifecycleOwner(), scheduleMap -> {
            thursdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            thursdayTextView.setText(scheduleMap.get("thursday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "friday").observe(getViewLifecycleOwner(), scheduleMap -> {
            fridayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            fridayTextView.setText(scheduleMap.get("friday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "saturday").observe(getViewLifecycleOwner(), scheduleMap -> {
            saturdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            saturdayTextView.setText(scheduleMap.get("saturday"));
        });

        scheduleViewModel.loadScheduleData(userRole, userUID, "sunday").observe(getViewLifecycleOwner(), scheduleMap -> {
            sundayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            sundayTextView.setText(scheduleMap.get("sunday"));
        });
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
}
