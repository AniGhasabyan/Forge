package com.example.forge.ui.navbar.home;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
        holder.imageButtonReject.setVisibility(VISIBLE);

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

                int removedPosition = holder.getAdapterPosition();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList.remove(removedPosition);
                        notifyItemRemoved(removedPosition);
                    }
                });

                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_home, bundle);
            }
        });
        holder.imageButtonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userRole.equals("Athlete")) {
                    rejectAthleteOffer(user);
                } else if (userRole.equals("Coach")) {
                    rejectCoachOffer(user);
                }

                int removedPosition = holder.getAdapterPosition();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userList.remove(removedPosition);
                        notifyItemRemoved(removedPosition);
                    }
                });

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

    private void rejectAthleteOffer(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Coaches Requested to Train You")
                                .document(userUID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Your Coaching Requests")
                                                .document(currentUserUID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Handle success
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

    private void rejectCoachOffer(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Athletes Interested in Your Coaching")
                                .document(userUID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Coaches You're Interested in")
                                                .document(currentUserUID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Handle success
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

    private void foo1(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Your Coaches")
                                .document(userUID)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "User added to Your Coaches", Toast.LENGTH_SHORT).show();
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Your Athletes")
                                                .document(currentUserUID)
                                                .set(current)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        db.collection("users")
                                                                .document(currentUserUID)
                                                                .collection("Coaches Requested to Train You")
                                                                .document(userUID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        db.collection("users")
                                                                                .document(userUID)
                                                                                .collection("Your Coaching Requests")
                                                                                .document(currentUserUID)
                                                                                .delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        // Handle success
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

    private void foo2(User user, User current) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            getUserUIDByEmail(user.getEmail(), new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String userUID) {
                    if (userUID != null) {
                        db.collection("users")
                                .document(currentUserUID)
                                .collection("Your Athletes")
                                .document(userUID)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "User added to Your Athletes", Toast.LENGTH_SHORT).show();
                                        db.collection("users")
                                                .document(userUID)
                                                .collection("Your Coaches")
                                                .document(currentUserUID)
                                                .set(current)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        db.collection("users")
                                                                .document(currentUserUID)
                                                                .collection("Athletes Interested in Your Coaching")
                                                                .document(userUID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        db.collection("users")
                                                                                .document(userUID)
                                                                                .collection("Coaches You're Interested in")
                                                                                .document(currentUserUID)
                                                                                .delete()
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        // Handle success
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
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewUsername;
        private final ImageView profilePicture;
        private final ImageButton imageViewCheckMark;
        private final ImageButton imageButtonReject;
        private String cachedImageUrl;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            imageViewCheckMark = itemView.findViewById(R.id.image_button_check_mark);
            imageButtonReject = itemView.findViewById(R.id.image_button_reject);
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