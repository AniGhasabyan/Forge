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
import java.util.List;

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
        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("progress")
                .document(userUID)
                .collection("conquests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Message> notes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String noteContent = document.getString("text");
                        notes.add(new Message(noteContent));
                    }
                    progressNotes.postValue(notes);
                });
    }

    public LiveData<List<Message>> getProgressNotes() {
        return progressNotes;
    }

    public void addProgressNote(Message note, String userRole, String userUID) {
        List<Message> currentNotes = progressNotes.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            progressNotes.setValue(currentNotes);

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("progress")
                    .document(userUID)
                    .collection("conquests")
                    .add(note);
            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("progress")
                        .document(user.getUid())
                        .collection("conquests")
                        .add(note);
                db.collection(userRole.toLowerCase()).document(user.getUid())
                        .collection("progress")
                        .document(user.getUid())
                        .collection("conquests")
                        .add(note);
            }
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}