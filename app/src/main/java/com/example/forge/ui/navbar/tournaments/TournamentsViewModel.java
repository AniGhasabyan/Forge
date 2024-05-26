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
        tournamentList.setValue(new ArrayList<>());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        loadTournaments(userRole, userUID);
    }

    public LiveData<List<String>> getTournamentList() {
        return tournamentList;
    }

    public void loadTournaments(String userRole, String userUID) {
        if (tournamentList.getValue() != null && !tournamentList.getValue().isEmpty()) {
            return;
        }
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

    public void addTournament(String note, String userRole, String userUID, String username) {
        if (db != null && user != null && userRole != null) {

            Map<String, Object> noteData = new HashMap<>();
            noteData.put("note", note);

            String oppositeRole = "";
            if(userRole.equals("Athlete")){
                oppositeRole = "Coach";
            } else if(userRole.equals("Coach")){
                oppositeRole = "Athlete";
            }

            Map<String, Object> tournamentsData_m = new HashMap<>();
            tournamentsData_m.put("note", note + username);
            Map<String, Object> tournamentsData_y = new HashMap<>();
            tournamentsData_y.put("note", note + " - " + user.getDisplayName());

            db.collection(userRole.toLowerCase()).document(user.getUid())
                    .collection("tournaments")
                    .document(userUID)
                    .collection("date")
                    .add(noteData);

            if(!userUID.equals(user.getUid())) {
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("tournaments")
                        .document(user.getUid())
                        .collection("date")
                        .add(noteData);
                db.collection(userRole.toLowerCase()).document(user.getUid())
                        .collection("tournaments")
                        .document(user.getUid())
                        .collection("date")
                        .add(tournamentsData_m)
                        .addOnSuccessListener(documentReference -> {
                            List<String> updatedTournaments = tournamentList.getValue();
                            if (updatedTournaments == null) {
                                updatedTournaments = new ArrayList<>();
                            }
                            updatedTournaments.add(note);
                            tournamentList.setValue(updatedTournaments);
                        });
                db.collection(oppositeRole.toLowerCase()).document(userUID)
                        .collection("tournaments")
                        .document(userUID)
                        .collection("date")
                        .add(tournamentsData_y);
            }
        }
    }

}
