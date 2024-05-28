package com.example.forge.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;
import com.example.forge.ui.navbar.diet.DietViewModel;

import java.util.List;

public class ChosenMessageAdapter extends RecyclerView.Adapter<ChosenMessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;
    private OnMessageDeleteListener messageDeleteListener;
    private String userRole;
    private String userUID;
    private DietViewModel dietViewModel;

    public interface OnMessageDeleteListener {
        void onMessageDeleted(Message message, String userRole, String userUID);
    }

    public void setMessageDeleteListener(OnMessageDeleteListener listener) {
        this.messageDeleteListener = listener;
    }

    public ChosenMessageAdapter(List<Message> messages, Context context, String userRole, String userUID) {
        this.messages = messages;
        this.context = context;
        this.userRole = userRole;
        this.userUID = userUID;
    }

    public ChosenMessageAdapter(List<Message> messages, Context context, String userRole, String userUID, DietViewModel dietViewModel) {
        this.messages = messages;
        this.context = context;
        this.userRole = userRole;
        this.userUID = userUID;
        this.dietViewModel = dietViewModel;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        final Message message = messages.get(position);
        holder.bind(message);

        int place = message.getPlace();
        switch (place) {
            case 1:
                holder.firstPlace.setVisibility(View.VISIBLE);
                holder.secondPlace.setVisibility(View.GONE);
                holder.thirdPlace.setVisibility(View.GONE);
                holder.proudPlace.setVisibility(View.GONE);
                break;
            case 2:
                holder.firstPlace.setVisibility(View.GONE);
                holder.secondPlace.setVisibility(View.VISIBLE);
                holder.thirdPlace.setVisibility(View.GONE);
                holder.proudPlace.setVisibility(View.GONE);
                break;
            case 3:
                holder.firstPlace.setVisibility(View.GONE);
                holder.secondPlace.setVisibility(View.GONE);
                holder.thirdPlace.setVisibility(View.VISIBLE);
                holder.proudPlace.setVisibility(View.GONE);
                break;
            case 4:
                holder.firstPlace.setVisibility(View.GONE);
                holder.secondPlace.setVisibility(View.GONE);
                holder.thirdPlace.setVisibility(View.GONE);
                holder.proudPlace.setVisibility(View.VISIBLE);
                break;
            default:
                holder.firstPlace.setVisibility(View.GONE);
                holder.secondPlace.setVisibility(View.GONE);
                holder.thirdPlace.setVisibility(View.GONE);
                holder.proudPlace.setVisibility(View.GONE);
                break;
        }

        holder.itemView.setOnLongClickListener(v -> {
            showDeleteDialog(message);
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
        });
    }

    private void showDeleteDialog(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete For Yourself");
        builder.setMessage("Are you sure you want to delete this?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            dietViewModel.deleteMessage(message, userRole, userUID);
            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        ImageView firstPlace;
        ImageView secondPlace;
        ImageView thirdPlace;
        ImageView proudPlace;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_view_message);
            firstPlace = itemView.findViewById(R.id.firstPlace);
            secondPlace = itemView.findViewById(R.id.secondPlace);
            thirdPlace = itemView.findViewById(R.id.thirdPlace);
            proudPlace = itemView.findViewById(R.id.proudPlace);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
        }
    }
}