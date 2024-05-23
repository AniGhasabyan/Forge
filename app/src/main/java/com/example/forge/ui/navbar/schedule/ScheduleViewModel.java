package com.example.forge.ui.navbar.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ScheduleViewModel extends ViewModel {

    private MutableLiveData<Map<String, String>> scheduleData;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public ScheduleViewModel(String userRole, String userUID) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        scheduleData = new MutableLiveData<>();
        loadScheduleData(userRole, userUID);
    }

    public LiveData<Map<String, String>> getScheduleData() {
        return scheduleData;
    }

    public void loadScheduleData(String userRole, String userUID) {
        Map<String, String> scheduleMap = new HashMap<>();

        String[] daysOfWeek = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        for (String dayOfWeek : daysOfWeek) {
            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("schedule")
                    .document(userUID)
                    .collection(dayOfWeek)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        StringBuilder times = new StringBuilder();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String time = document.getString("time");
                            if (time != null) {
                                if (times.length() > 0) {
                                    times.append("\n");
                                }
                                times.append(time);
                            }
                        }
                        scheduleMap.put(dayOfWeek, times.toString());
                        scheduleData.postValue(scheduleMap);
                    });
        }
    }

    public void saveTime(String dayOfWeek, String time, String username, String userRole, String userUID) {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);
        scheduleData.put("username", username);

        String oppositeRole = userRole.equals("Athlete") ? "Coach" : "Athlete";

        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("schedule")
                .document(userUID)
                .collection(dayOfWeek)
                .add(scheduleData);

        if (!userUID.equals(user.getUid())) {
            db.collection(oppositeRole.toLowerCase()).document(userUID)
                    .collection("schedule")
                    .document(user.getUid())
                    .collection(dayOfWeek)
                    .add(scheduleData);
        }
    }
}
