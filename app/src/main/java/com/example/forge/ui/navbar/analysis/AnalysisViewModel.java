package com.example.forge.ui.navbar.analysis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AnalysisViewModel extends ViewModel {
    private MutableLiveData<List<Message>> message;
    private MutableLiveData<String> mText;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public AnalysisViewModel(String userRole, String userUID) {
        message = new MutableLiveData<>();
        message.setValue(new ArrayList<>());

        mText = new MutableLiveData<>();
        mText.setValue("This is analysis fragment");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        loadAnalysisData(userRole, userUID);
    }

    public void loadAnalysisData(String userRole, String userUID){
        db.collection(userRole.toLowerCase()).document(userUID)
                .collection("analyses")
                .document(user.getUid())
                .collection("message")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> notes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("text");
                        String noteSender = document.getString("sender");
                        if(!Objects.equals(noteSender, user.getDisplayName())){
                            notes.add(new Message(noteSender + "\n" + noteContent));
                        } else {
                            notes.add(new Message(noteContent));
                        }
                    }
                    message.postValue(notes);
                });
    }

    public LiveData<List<Message>> getMessage() {
        return message;
    }

    public void addMessage(Message note, String userRole, String userUID) {
        List<Message> currentNotes = message.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            message.setValue(currentNotes);

            String oppositeRole = "";
            if (userRole.equals("Athlete")) {
                oppositeRole = "Coach";
            } else if (userRole.equals("Coach")) {
                oppositeRole = "Athlete";
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("sender", user.getDisplayName());
            messageData.put("text", note.getText());

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
