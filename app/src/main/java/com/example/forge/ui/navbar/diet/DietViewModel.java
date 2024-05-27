package com.example.forge.ui.navbar.diet;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.Message;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DietViewModel extends ViewModel {
    private MutableLiveData<List<Message>> dietNotes;
    private MutableLiveData<String> mText;
    private MutableLiveData<Map<String, String>> dietData;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public DietViewModel(String userRole, String userUID) {
        dietNotes = new MutableLiveData<>();
        dietNotes.setValue(new ArrayList<>());

        mText = new MutableLiveData<>();
        mText.setValue("This is diet fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadDietNotes(userRole, userUID);
    }

    public void loadDietNotes(String userRole, String userUID) {
        if (dietNotes.getValue() != null && !dietNotes.getValue().isEmpty()) {
            return;
        }
        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("diets")
                .document(userUID)
                .collection("diet notes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> notes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("text");
                        notes.add(new Message(noteContent));
                    }
                    Collections.sort(notes, (m1, m2) -> {
                        if (m1.getTimestamp() == null && m2.getTimestamp() == null) {
                            return 0;
                        }
                        if (m1.getTimestamp() == null) {
                            return 1;
                        }
                        if (m2.getTimestamp() == null) {
                            return -1;
                        }
                        return m2.getTimestamp().compareTo(m1.getTimestamp());
                    });
                    dietNotes.postValue(notes);
                });
    }

    public LiveData<List<Message>> getDietNotes() {
        return dietNotes;
    }

    public void addDietNote(Message note, String userRole, String userUID, String username) {
        List<Message> currentNotes = dietNotes.getValue();
        if (currentNotes != null) {
            String noteContent = note.getText();

            currentNotes.add(0, note);
            Collections.sort(currentNotes, (m1, m2) -> {
                if (m1.getTimestamp() == null && m2.getTimestamp() == null) {
                    return 0;
                }
                if (m1.getTimestamp() == null) {
                    return 1;
                }
                if (m2.getTimestamp() == null) {
                    return -1;
                }
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            });
            dietNotes.setValue(currentNotes);

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            Map<String, Object> noteData_m = new HashMap<>();
            noteData_m.put("text", noteContent + username);
            noteData_m.put("timestamp", note.getTimestamp());
            Map<String, Object> noteData_y = new HashMap<>();
            noteData_y.put("text", noteContent + " - " + user.getDisplayName());
            noteData_y.put("timestamp", note.getTimestamp());
            Map<String, Object> noteData = new HashMap<>();
            noteData.put("text", noteContent);
            noteData.put("timestamp", note.getTimestamp());

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("diets")
                    .document(userUID)
                    .collection("diet notes")
                    .add(noteData);

            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("diets")
                        .document(user.getUid())
                        .collection("diet notes")
                        .add(noteData);
                db.collection(userRole.toLowerCase()).document(user.getUid())
                        .collection("diets")
                        .document(user.getUid())
                        .collection("diet notes")
                        .add(noteData_m);
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("diets")
                        .document(userUID)
                        .collection("diet notes")
                        .add(noteData_y);
            }
        }
    }


    public LiveData<String> getText() {
        return mText;
    }
}