package com.example.forge.ui.navbar.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TournamentsViewModel extends ViewModel {

    private MutableLiveData<List<String>> tournamentList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public TournamentsViewModel(String userRole) {
        tournamentList = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        loadTournaments(userRole);
    }

    public LiveData<List<String>> getTournamentList() {
        return tournamentList;
    }

    public void loadTournaments(String userRole) {
        if (db == null || user == null || userRole == null) return;

        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("tournaments").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> tournaments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tournament = document.getString("note");
                        tournaments.add(tournament);
                    }
                    tournamentList.setValue(tournaments);
                });
    }

    public void addTournament(String tournamentDetails, String userRole) {
        if (db != null && user != null && userRole != null) {
            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("tournaments")
                    .add(new HashMap<String, Object>() {{
                        put("note", tournamentDetails);
                    }});
            loadTournaments(userRole);
        }
    }
}
