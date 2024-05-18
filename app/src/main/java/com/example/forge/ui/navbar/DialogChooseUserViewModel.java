package com.example.forge.ui.navbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

public class DialogChooseUserViewModel extends ViewModel {
    private MutableLiveData<List<User>> userList1LiveData = new MutableLiveData<>();

    public LiveData<List<User>> getUserList1() {
        return userList1LiveData;
    }

    public void loadData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUID = getCurrentUserUID();
        if (currentUserUID == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference1;

        if (userRole.equals("Athlete")) {
            collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Coaches");
        } else {
            collectionReference1 = db.collection("users").document(currentUserUID).collection("Your Athletes");
        }

        List<User> userList1 = new ArrayList<>();

        collectionReference1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList1.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList1.add(user);
                }
                Log.d("PorchAnalysisViewModel", "User list size: " + userList1.size());
                userList1LiveData.setValue(userList1);
            } else {
                Log.e("PorchAnalysisViewModel", "Error fetching data: ", task.getException());
                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getCurrentUserUID() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }
}
