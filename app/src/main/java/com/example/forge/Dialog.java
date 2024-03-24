package com.example.forge;

import android.app.AlertDialog;
import android.content.Context;

public class Dialog {
    private Context context;

    public Dialog(Context context) {
        this.context = context;
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
