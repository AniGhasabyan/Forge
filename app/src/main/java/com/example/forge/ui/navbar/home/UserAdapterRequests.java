package com.example.forge.ui.navbar.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.forge.R;
import com.example.forge.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserAdapterRequests extends RecyclerView.Adapter<UserAdapterRequests.UserViewHolder> {

    private final List<User> userList;

    public UserAdapterRequests(List<User> userList) {
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
