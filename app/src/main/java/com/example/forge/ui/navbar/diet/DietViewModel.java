package com.example.forge.ui.navbar.diet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DietViewModel extends ViewModel {
    private MutableLiveData<List<Message>> dietNotes;
    private MutableLiveData<String> mText;
    private FirebaseFirestore db;

    public DietViewModel() {
        dietNotes = new MutableLiveData<>();
        dietNotes.setValue(new ArrayList<>());

        mText = new MutableLiveData<>();
        mText.setValue("This is diet fragment");

        db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<Message>> getDietNotes() {
        return dietNotes;
    }

    public void addDietNote(Message note) {
        List<Message> currentNotes = dietNotes.getValue();
        if (currentNotes != null) {
            currentNotes.add(0, note);
            dietNotes.setValue(currentNotes);

            db.collection("diets")
                    .add(note);
        }
    }

    public LiveData<String> getText() {
        return mText;
    }
}