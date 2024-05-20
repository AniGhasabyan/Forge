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

    public ScheduleViewModel(String userRole, String userUID) {
        mText = new MutableLiveData<>();
        mText.setValue("This is training schedule fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        scheduleData = new MutableLiveData<>();
        loadScheduleData(userRole, userUID);
    }

    public LiveData<Map<String, String>> loadScheduleData(String userRole, String userUID) {
        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("schedule")
                .document(userUID)
                .collection("times")
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

    public void saveTime(String dayOfWeek, String time, String username, String userRole, String userUID) {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);
        scheduleData.put("username", username);

        String oppositeRole = "";
        if(userRole.equals("Athlete")){
            oppositeRole = "Coach";
        } else if(userRole.equals("Coach")){
            oppositeRole = "Athlete";
        }

        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("schedule")
                .document(userUID)
                .collection("times")
                .add(scheduleData);
        if(!userUID.equals(user.getUid())) {
            db.collection(oppositeRole.toLowerCase()).document(userUID)
                    .collection("schedule")
                    .document(user.getUid())
                    .collection("times")
                    .add(scheduleData);
        }
    }
}
