package com.example.forge.ui.navbar.progress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.Message;

import java.util.List;

public class ConquestAdapter extends RecyclerView.Adapter<ConquestAdapter.ConquestViewHolder> {

    private List<Message> conquests;
    private Context context;
    private String userRole;
    private String userUID;

    public ConquestAdapter(List<Message> conquests, Context context, String userRole, String userUID) {
        this.conquests = conquests;
        this.context = context;
        this.userRole = userRole;
        this.userUID = userUID;
    }

    @NonNull
    @Override
    public ConquestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ConquestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConquestViewHolder holder, int position) {
        Message conquest = conquests.get(position);
        holder.textViewConquest.setText(conquest.getText());

        switch (conquest.getPlace()) {
            case 1:
                holder.imageViewPlace.setImageResource(R.drawable.medal_first_place);
                break;
            case 2:
                holder.imageViewPlace.setImageResource(R.drawable.medal_second_place);
                break;
            case 3:
                holder.imageViewPlace.setImageResource(R.drawable.medal_third_place);
                break;
            default:
                holder.imageViewPlace.setImageResource(R.drawable.medal_star_place);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return conquests.size();
    }

    public static class ConquestViewHolder extends RecyclerView.ViewHolder {
        TextView textViewConquest;
        ImageView imageViewPlace;

        public ConquestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewConquest = itemView.findViewById(R.id.text_view_message);
            imageViewPlace = itemView.findViewById(R.id.place);
        }
    }
}
