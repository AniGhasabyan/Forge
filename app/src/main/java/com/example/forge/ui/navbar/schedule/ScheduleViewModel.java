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

    private MutableLiveData<String> mText;
    private MutableLiveData<Map<String, String>> scheduleData;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public ScheduleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is training schedule fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        scheduleData = new MutableLiveData<>();
        loadScheduleData();
    }

    public LiveData<Map<String, String>> loadScheduleData() {
        db.collection("users").document(user.getUid())
                .collection("schedule")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, String> scheduleMap = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dayOfWeek = document.getId();
                            String time = document.getString("time");
                            String username = document.getString("username");
                            scheduleMap.put(dayOfWeek, time + " - " + username);
                        }
                        scheduleData.postValue(scheduleMap);
                    } else {
                    }
                });

        return scheduleData;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void saveTime(String dayOfWeek, String time, String username, String userRole) {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);
        scheduleData.put("username", username);

        db.collection("users").document(user.getUid())
                .collection("schedule")
                .document(dayOfWeek)
                .set(scheduleData);
    }
}
