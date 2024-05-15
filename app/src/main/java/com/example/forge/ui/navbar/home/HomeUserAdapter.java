package com.example.forge.ui.navbar.home;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
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

public class HomeUserAdapter extends RecyclerView.Adapter<HomeUserAdapter.UserViewHolder> {

    private final List<User> userList;
    private final Context context;

    public HomeUserAdapter(Context context, List<User> userList) {
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

        Bundle bundle = new Bundle();
        bundle.putString("username", user.getUsername());

        User current = null;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            current = new User(currentUser.getDisplayName(), currentUser.getEmail());
        }

        holder.imageViewCheckMark.setVisibility(VISIBLE);

        User finalCurrent = current;

        String userRole = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("UserRole", "Athlete");
        holder.imageViewCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole.equals("Athlete")) {
                    foo1(user, finalCurrent);
                } else if (userRole.equals("Coach")) {
                    foo2(user, finalCurrent);
                }

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_home, bundle);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void foo1(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            db.collection("users")
                    .document(currentUserUID)
                    .collection("Your Coaches")
                    .document(user.getEmail())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "User added to Your Coaches", Toast.LENGTH_SHORT).show();
                            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String userUID) {
                                    if (userUID != null) {
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Your Athletes")
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
                                    } else {
                                    }
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

    private void foo2(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            db.collection("users")
                    .document(currentUserUID)
                    .collection("Your Athletes")
                    .document(user.getEmail())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "User added to Your Athletes", Toast.LENGTH_SHORT).show();
                            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String userUID) {
                                    if (userUID != null) {
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Your Coaches")
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
                                    } else {
                                    }
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

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
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
                });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final ImageButton imageViewCheckMark;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            imageViewCheckMark = itemView.findViewById(R.id.image_button_check_mark);
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
        }
    }
}
