package com.example.forge.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.Message;
import com.example.forge.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;
    private OnMessageDeleteListener messageDeleteListener;
    private String userRole;
    private String userUID;

    public interface OnMessageDeleteListener {
        void onMessageDeleted(Message message, String userRole, String userUID);
    }

    public void setMessageDeleteListener(OnMessageDeleteListener listener) {
        this.messageDeleteListener = listener;
    }

    public MessageAdapter(List<Message> messages, Context context, String userRole, String userUID) {
        this.messages = messages;
        this.context = context;
        this.userRole = userRole;
        this.userUID = userUID;
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

        holder.itemView.setOnLongClickListener(v -> {
            showDeleteDialog(message);
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            // Handle click event if needed
        });
    }

    private void showDeleteDialog(final Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Message");
        builder.setMessage("Are you sure you want to delete this message?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            int position = messages.indexOf(message);
            messages.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();

            if (messageDeleteListener != null) {
                messageDeleteListener.onMessageDeleted(message, userRole, userUID);
            }
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

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_view_message);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
        }
    }
}
