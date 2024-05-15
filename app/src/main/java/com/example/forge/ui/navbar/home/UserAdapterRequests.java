package com.example.forge.ui.navbar.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;

import android.os.Bundle;

import java.util.List;

public class UserAdapterRequests extends RecyclerView.Adapter<UserAdapterRequests.UserViewHolder> {

    private List<User> userList;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("username", user.getUsername());
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
