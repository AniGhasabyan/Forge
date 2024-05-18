package com.example.forge.ui.search;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;

    public SearchUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
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

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        User current = null;
        if (currentUser != null) {
            current = new User(currentUser.getDisplayName(), currentUser.getEmail());
        }
        User finalCurrent = current;

        if (currentUser != null && user.getEmail().equals(currentUser.getEmail())) {
            holder.imageViewCheckMark.setVisibility(GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.nav_profile);
                }
            });
        } else {
            holder.imageViewCheckMark.setVisibility(VISIBLE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            holder.imageViewCheckMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userRole = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                            .getString("UserRole", "Athlete");

                    if (userRole.equals("Athlete")) {
                        addSelectedUserToAthleteCollection(user, finalCurrent);
                    } else if (userRole.equals("Coach")) {
                        addSelectedUserToCoachCollection(user, finalCurrent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final ImageView imageViewCheckMark;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            imageViewCheckMark = itemView.findViewById(R.id.image_button_check_mark);
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
        }
    }

    private void addSelectedUserToCoachCollection(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Your Coaching Requests")
                                .document(userUID)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "User added to Your Coaching Requests", Toast.LENGTH_SHORT).show();
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Coaches Requested to Train You")
                                                .document(currentUserUID)
                                                .set(current)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        }
    }

    private void addSelectedUserToAthleteCollection(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Coaches You're Interested in")
                                .document(userUID)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "User added to Coaches You're Interested in", Toast.LENGTH_SHORT).show();
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Athletes Interested in Your Coaching")
                                                .document(currentUserUID)
                                                .set(current)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        }
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
                });
    }


    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

}