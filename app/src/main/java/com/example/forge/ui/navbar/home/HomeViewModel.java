package com.example.forge.ui.navbar.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.forge.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<User>> userList1LiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> userList2LiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> userList3LiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<User>> getUserList1() {
        return userList1LiveData;
    }

    public LiveData<List<User>> getUserList2() {
        return userList2LiveData;
    }

    public LiveData<List<User>> getUserList3() {
        return userList3LiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadData(Context context) {
        isLoading.setValue(true);

        SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID != null) {
            CollectionReference collectionReference1, collectionReference2, collectionReference3;
            if (userRole.equals("Athlete")) {
                collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Coaches");
                collectionReference2 = db.collection("users").document(currentUserUID).collection("Coaches Requested to Train You");
                collectionReference3 = db.collection("users").document(currentUserUID).collection("Coaches You're Interested in");
            } else {
                collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Athletes");
                collectionReference2 = db.collection("users").document(currentUserUID).collection("Athletes Interested in Your Coaching");
                collectionReference3 = db.collection("users").document(currentUserUID).collection("Your Coaching Requests");
            }

            List<User> userList1 = new ArrayList<>();
            List<User> userList2 = new ArrayList<>();
            List<User> userList3 = new ArrayList<>();

            collectionReference1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList1.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList1.add(user);
                    }
                    userList1LiveData.setValue(userList1);
                } else {
                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            collectionReference2.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList2.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList2.add(user);
                    }
                    userList2LiveData.setValue(userList2);
                } else {
                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            collectionReference3.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userList3.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User user = document.toObject(User.class);
                        userList3.add(user);
                    }
                    userList3LiveData.setValue(userList3);
                } else {
                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                isLoading.setValue(false);
            });
        }
    }

    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }
}