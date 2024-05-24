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

    public TournamentsViewModel(String userRole, String userUID) {
        tournamentList = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        loadTournaments(userRole, userUID);
    }

    public LiveData<List<String>> getTournamentList() {
        return tournamentList;
    }

    public void loadTournaments(String userRole, String userUID) {
        tournamentList.setValue(new ArrayList<>());

        db.collection(userRole.toLowerCase()).document(user.getUid())
                .collection("tournaments")
                .document(userUID)
                .collection("date")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> tournaments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String tournament = document.getString("note");
                        tournaments.add(tournament);
                    }
                    tournamentList.setValue(tournaments);
                });
    }

    public void addTournament(String tournamentDetails, String userRole, String userUID) {
        if (db != null && user != null && userRole != null) {

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("tournaments")
                    .document(userUID)
                    .collection("date")
                    .add(new HashMap<String, Object>() {{
                        put("note", tournamentDetails);
                    }}).addOnSuccessListener(documentReference -> {
                        List<String> updatedTournaments = tournamentList.getValue();
                        if (updatedTournaments == null) {
                            updatedTournaments = new ArrayList<>();
                        }
                        updatedTournaments.add(tournamentDetails);
                        tournamentList.setValue(updatedTournaments);
                    });

            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("tournaments")
                        .document(user.getUid())
                        .collection("date")
                        .add(new HashMap<String, Object>() {{
                            put("note", tournamentDetails);
                        }});
                db.collection(userRole.toLowerCase()).document(user.getUid())
                        .collection("tournaments")
                        .document(user.getUid())
                        .collection("date")
                        .add(new HashMap<String, Object>() {{
                            put("note", tournamentDetails);
                        }}).addOnSuccessListener(documentReference -> {
                            List<String> updatedTournaments = tournamentList.getValue();
                            if (updatedTournaments == null) {
                                updatedTournaments = new ArrayList<>();
                            }
                            updatedTournaments.add(tournamentDetails);
                            tournamentList.setValue(updatedTournaments);
                        });
            }
        }
    }

}
