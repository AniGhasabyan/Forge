package com.example.forge.ui.navbar.analysis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnalysisViewModel extends ViewModel {
    private MutableLiveData<List<Message>> messages;
    private MutableLiveData<String> mText;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public AnalysisViewModel(String userRole, String userUID) {
        messages = new MutableLiveData<>();
        messages.setValue(new ArrayList<>());

        mText = new MutableLiveData<>();
        mText.setValue("This is analysis fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadAnalysisData(userRole, userUID);
    }

    public void loadAnalysisData(String userRole, String userUID) {
        db.collection(userRole.toLowerCase()).document(userUID)
                .collection("analyses")
                .document(user.getUid())
                .collection("message")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> notes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("text");
                        String noteSender = document.getString("sender");
                        Date timestamp = document.getDate("timestamp");
                        if (!Objects.equals(noteSender, user.getDisplayName())) {
                            notes.add(new Message(noteSender + "\n" + noteContent, false, timestamp));
                        } else {
                            notes.add(new Message(noteContent, true, timestamp));
                        }
                    }
                    Collections.sort(notes, (m1, m2) -> {
                        if (m1.getTimestamp() == null && m2.getTimestamp() == null) {
                            return 0;
                        }
                        if (m1.getTimestamp() == null) {
                            return 1; // Null dates go last
                        }
                        if (m2.getTimestamp() == null) {
                            return -1; // Null dates go last
                        }
                        return m2.getTimestamp().compareTo(m1.getTimestamp());
                    });
                    messages.postValue(notes);
                });
    }

    public LiveData<List<Message>> getMessage() {
        return messages;
    }

    public void addMessage(Message note, String userRole, String userUID) {
        List<Message> currentNotes = messages.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            Collections.sort(currentNotes, (m1, m2) -> {
                if (m1.getTimestamp() == null && m2.getTimestamp() == null) {
                    return 0;
                }
                if (m1.getTimestamp() == null) {
                    return 1; // Null dates go last
                }
                if (m2.getTimestamp() == null) {
                    return -1; // Null dates go last
                }
                return m2.getTimestamp().compareTo(m1.getTimestamp());
            });
            messages.setValue(currentNotes);

            String oppositeRole = "";
            if (userRole.equals("Athlete")) {
                oppositeRole = "Coach";
            } else if (userRole.equals("Coach")) {
                oppositeRole = "Athlete";
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("sender", user.getDisplayName());
            messageData.put("text", note.getText());
            messageData.put("timestamp", note.getTimestamp());

            db.collection(userRole.toLowerCase()).document(userUID)
                    .collection("analyses")
                    .document(user.getUid())
                    .collection("message")
                    .add(messageData);
            if (!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(user.getUid())
                        .collection("analyses")
                        .document(userUID)
                        .collection("message")
                        .add(messageData);
            }
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}
