package com.example.forge.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;

import android.os.Bundle;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private int currentDestinationId;

    public UserAdapter(List<User> userList, int currentDestinationId) {
        this.userList = userList;
        this.currentDestinationId = currentDestinationId;
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

                NavController navController = Navigation.findNavController(view);
                if (currentDestinationId == R.id.nav_home) {
                    navController.navigate(R.id.nav_choose, bundle);
                } else if (currentDestinationId == R.id.nav_porch) {
                    navController.navigate(R.id.nav_analysis, bundle);
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

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.text_view_username);
        }

        public void bind(User user) {
            textViewUsername.setText(user.getUsername());
        }
    }
}
