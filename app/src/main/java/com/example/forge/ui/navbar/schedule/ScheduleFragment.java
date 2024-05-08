package com.example.forge.ui.navbar.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.forge.R;

import java.util.Calendar;

public class ScheduleFragment extends Fragment {

    private TextView mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView,
            fridayTextView, saturdayTextView, sundayTextView;

    private ScheduleViewModel scheduleViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);

        mondayTextView = root.findViewById(R.id.mondayText);
        tuesdayTextView = root.findViewById(R.id.tuesdayText);
        wednesdayTextView = root.findViewById(R.id.wednesdayText);
        thursdayTextView = root.findViewById(R.id.thursdayText);
        fridayTextView = root.findViewById(R.id.fridayText);
        saturdayTextView = root.findViewById(R.id.saturdayText);
        sundayTextView = root.findViewById(R.id.sundayText);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

        View.OnClickListener dayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        requireActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                                String selectedTime = selectedHour + ":" + selectedMinute;
                                TextView textView = (TextView) v;

                                String currentText = textView.getText().toString().trim();
                                if (!currentText.isEmpty()) {
                                    currentText += "\n" + selectedTime;
                                } else {
                                    currentText = selectedTime;
                                }

                                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                                textView.setText(currentText);

                                scheduleViewModel.saveTime(getDayOfWeekFromView(v), selectedTime);
                            }
                        },
                        hour,
                        minute,
                        true
                );
                timePickerDialog.show();
            }
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

    private String getDayOfWeekFromView(View v) {
        String day = "";

        Object tag = v.getTag();
        if (tag instanceof String) {
            day = (String) tag;
        }

        return day;
    }
}
