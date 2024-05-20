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
import java.util.List;

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
                        notes.add(new Message(noteContent));
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
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            db.collection(userRole.toLowerCase()).document(userUID)
                    .collection("analyses")
                    .document(user.getUid())
                    .collection("message")
                    .add(note);
            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(user.getUid())
                        .collection("analyses")
                        .document(userUID)
                        .collection("message")
                        .add(note);
            }
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}
