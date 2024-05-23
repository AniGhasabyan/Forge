package com.example.forge.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;
import com.example.forge.User;
import com.example.forge.ui.navbar.diet.DietViewModel;
import com.example.forge.ui.navbar.diet.DietViewModelFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private int currentDestinationId;
    private String note;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Context context;
    private String userRole;
    private String dayOfWeek;

    public UserAdapter(List<User> userList, int currentDestinationId) {
        this.userList = userList;
        this.currentDestinationId = currentDestinationId;
    }

    public UserAdapter(List<User> userList, int currentDestinationId, String note, Context context) {
        this.userList = userList;
        this.currentDestinationId = currentDestinationId;
        this.note = note;
        this.context = context;
    }

    public UserAdapter(List<User> userList, int currentDestinationId, String note, Context context, String userRole, String dayOfWeek) {
        this.userList = userList;
        this.currentDestinationId = currentDestinationId;
        this.note = note;
        this.context = context;
        this.userRole = userRole;
        this.dayOfWeek = dayOfWeek;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("username", user.getUsername());
                bundle.putString("email", user.getEmail());

                db = FirebaseFirestore.getInstance();
                String currentUserUID = getCurrentUserUID();

                if (currentDestinationId == R.id.nav_home) {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.nav_choose, bundle);
                } else if (currentDestinationId == R.id.nav_porch) {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.nav_analysis, bundle);
                } else if (currentDestinationId == R.id.nav_diet){
                    Map<String, Object> noteMap = new HashMap<>();
                    String noteId = UUID.randomUUID().toString();
                    noteMap.put("id", noteId);
                    noteMap.put("text", note);

                    getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String userUID) {
                            db.collection("coach")
                                    .document(currentUserUID)
                                    .collection("diets")
                                    .document(userUID)
                                    .collection("diet notes")
                                    .add(noteMap);
                            db.collection("athlete")
                                    .document(userUID).collection("diets")
                                    .document(currentUserUID)
                                    .collection("diet notes")
                                    .add(noteMap);

                        }
                    });
                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_diet);
                } else if (currentDestinationId == R.id.nav_progress){
                    Map<String, Object> noteMap = new HashMap<>();
                    String noteId = UUID.randomUUID().toString();
                    noteMap.put("id", noteId);
                    noteMap.put("text", note);

                    getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String userUID) {
                            db.collection("coach")
                                    .document(currentUserUID)
                                    .collection("progress")
                                    .document(userUID)
                                    .collection("conquests")
                                    .add(noteMap);
                            db.collection("athlete")
                                    .document(userUID).collection("progress")
                                    .document(currentUserUID)
                                    .collection("conquests")
                                    .add(noteMap);

                        }
                    });
                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_progress);
                } else if (currentDestinationId == R.id.nav_tournaments){
                    Map<String, Object> noteData = new HashMap<>();
                    noteData.put("note", note);
                    getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String userUID) {
                            db.collection("coach")
                                    .document(currentUserUID)
                                    .collection("tournaments")
                                    .document(userUID)
                                    .collection("date")
                                    .add(noteData);
                            db.collection("athlete")
                                    .document(userUID).collection("tournaments")
                                    .document(currentUserUID)
                                    .collection("date")
                                    .add(noteData);

                        }
                    });
                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_tournaments);
                } else if (currentDestinationId == R.id.nav_schedule){
                    Map<String, Object> scheduleData = new HashMap<>();
                    scheduleData.put("time", note);

                    String oppositeRole = userRole.equals("Athlete") ? "Coach" : "Athlete";
                    getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String userUID) {
                            db.collection(userRole.toLowerCase())
                                    .document(currentUserUID)
                                    .collection("schedule")
                                    .document(userUID)
                                    .collection(dayOfWeek)
                                    .add(scheduleData);
                            db.collection(oppositeRole.toLowerCase())
                                    .document(userUID).collection("schedule")
                                    .document(currentUserUID)
                                    .collection(dayOfWeek)
                                    .add(scheduleData);

                        }
                    });

                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_schedule);
                }
            }
        });
    }

    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    private void getUserUIDByEmail(String email, OnSuccessListener<String> onSuccessListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String userUID = documentSnapshot.getString("uid");
                            onSuccessListener.onSuccess(userUID);
                        } else {
                            onSuccessListener.onSuccess(null);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final ImageView profilePicture;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            profilePicture = itemView.findViewById(R.id.profile_picture);
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
            if ("None".equals(user.getEmail())) {
                profilePicture.setVisibility(View.GONE);
            }
        }
    }
}
