package com.example.forge.ui.navbar.diet;

import android.content.Context;
import android.content.SharedPreferences;

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
                    dietNotes.postValue(notes);
                });
    }

    public LiveData<List<Message>> getDietNotes() {
        return dietNotes;
    }

    public void addDietNote(Message note, String userRole, String userUID) {
        List<Message> currentNotes = dietNotes.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            dietNotes.setValue(currentNotes);

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("diets")
                    .document(userUID)
                    .collection("diet notes")
                    .add(note);
            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("diets")
                        .document(user.getUid())
                        .collection("diet notes")
                        .add(note);
            }
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}