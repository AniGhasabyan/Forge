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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;
import com.example.forge.User;
import com.example.forge.ui.navbar.diet.DietViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private int currentDestinationId;
    private String note;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Context context;

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

                if (currentDestinationId == R.id.nav_home) {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.nav_choose, bundle);
                } else if (currentDestinationId == R.id.nav_porch) {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.nav_analysis, bundle);
                } else if (currentDestinationId == R.id.nav_diet){

                    NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_diet);
                }
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
