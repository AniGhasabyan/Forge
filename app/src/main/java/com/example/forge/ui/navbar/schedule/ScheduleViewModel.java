package com.example.forge.ui.navbar.schedule;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ScheduleViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public ScheduleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is training schedule fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void saveTime(String dayOfWeek, String time) {
        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("time", time);

        db.collection("users").document(user.getUid())
                .collection("schedule")
                .document(dayOfWeek)
                .set(scheduleData);
    }
}
