package com.example.forge.ui.navbar.progress;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgressViewModel extends ViewModel {
    private MutableLiveData<List<Message>> progressNotes;
    private MutableLiveData<String> mText;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public ProgressViewModel(String userRole, String userUID) {
        progressNotes = new MutableLiveData<>();
        progressNotes.setValue(new ArrayList<>());

        mText = new MutableLiveData<>();
        mText.setValue("This is progression fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadProgressNotes(userRole, userUID);
    }

    public void loadProgressNotes(String userRole, String userUID) {
        if (progressNotes.getValue() != null && !progressNotes.getValue().isEmpty()) {
            return;
        }
        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("progress")
                .document(userUID)
                .collection("conquests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> notes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("text");
                        int place = document.getLong("place").intValue();
                        notes.add(new Message(noteContent, place));
                    }
                    progressNotes.postValue(notes);
                });
    }

    public LiveData<List<Message>> getProgressNotes() {
        return progressNotes;
    }

    public void addProgressNote(Message note, String userRole, String userUID, String username, int place) {
        List<Message> currentNotes = progressNotes.getValue();
        if (currentNotes != null) {
            String noteContent = note.getText();

            currentNotes.add(0, note);
            progressNotes.setValue(currentNotes);

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            Map<String, Object> noteProgress_m = new HashMap<>();
            noteProgress_m.put("text", noteContent + " - " + username);
            noteProgress_m.put("place", place);
            Map<String, Object> noteProgress_y = new HashMap<>();
            noteProgress_y.put("text", noteContent + " - " + user.getDisplayName());
            noteProgress_y.put("place", place);
            Map<String, Object> noteProgress = new HashMap<>();
            noteProgress.put("text", noteContent);
            noteProgress.put("place", place);

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("progress")
                    .document(userUID)
                    .collection("conquests")
                    .add(noteProgress);
            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("progress")
                        .document(user.getUid())
                        .collection("conquests")
                        .add(noteProgress);
                db.collection(userRole.toLowerCase()).document(user.getUid())
                        .collection("progress")
                        .document(user.getUid())
                        .collection("conquests")
                        .add(noteProgress_m);
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("progress")
                        .document(userUID)
                        .collection("conquests")
                        .add(noteProgress_y);
            }
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}