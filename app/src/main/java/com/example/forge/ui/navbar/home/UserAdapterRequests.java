package com.example.forge.ui.navbar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.forge.R;
import com.example.forge.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdapterRequests extends RecyclerView.Adapter<UserAdapterRequests.UserViewHolder> {

    private final List<User> userList;
    private Context context;

    public UserAdapterRequests(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
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
            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            showRemoveUserDialog(position);
            return true;
        });
    }

    public static void getUserUIDByEmail(String email, OnSuccessListener<String> onSuccessListener) {
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

    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    private void showRemoveUserDialog(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Remove User")
                .setMessage("Are you sure you want to remove this user?")
                .setPositiveButton("Yes", (dialog, which) -> removeUser(position))
                .setNegativeButton("No", null)
                .show();
    }

    private void removeUser(int position) {
        User user = userList.get(position);
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String userRole = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                .getString("UserRole", "Athlete");

                        if (userRole.equals("Athlete")) {
                            removeAthleteCoachRelation(db, currentUserUID, userUID);
                        } else if (userRole.equals("Coach")) {
                            removeCoachAthleteRelation(db, currentUserUID, userUID);
                        }

                        ((Activity) context).runOnUiThread(() -> {
                            userList.remove(position);
                            notifyItemRemoved(position);
                        });
                    }
                }
            });
        }
    }

    private void removeAthleteCoachRelation(FirebaseFirestore db, String currentUserUID, String userUID) {
        db.collection("users")
                .document(currentUserUID)
                .collection("Coaches You're Interested in")
                .document(userUID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("users")
                            .document(userUID)
                            .collection("Athletes Interested in Your Coaching")
                            .document(currentUserUID)
                            .delete()
                            .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void removeCoachAthleteRelation(FirebaseFirestore db, String currentUserUID, String userUID) {
        db.collection("users")
                .document(currentUserUID)
                .collection("Your Coaching Requests")
                .document(userUID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("users")
                            .document(userUID)
                            .collection("Coaches Requested to Train You")
                            .document(currentUserUID)
                            .delete()
                            .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> newList) {
        userList.clear();
        userList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final ImageView profilePicture;
        private String cachedImageUrl;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            cachedImageUrl = null;
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
            if ("None".equals(user.getEmail())) {
                profilePicture.setVisibility(View.GONE);
            } else {
                profilePicture.setVisibility(View.VISIBLE);
                Context context = itemView.getContext();
                if (cachedImageUrl != null) {
                    Glide.with(context).load(cachedImageUrl).into(profilePicture);
                } else {
                    getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String userUID) {
                            if (userUID != null) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + userUID);
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    Glide.with(context).load(imageUrl).into(profilePicture);
                                    cachedImageUrl = imageUrl;
                                }).addOnFailureListener(e -> {
                                });
                            }
                        }
                    });
                }
            }
        }

        private void getUserUIDByEmail(String email, OnSuccessListener<String> onSuccessListener) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String userUID = documentSnapshot.getString("uid");
                            onSuccessListener.onSuccess(userUID);
                        } else {
                            onSuccessListener.onSuccess(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }
}
