package com.example.forge.ui.profile;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.forge.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final StorageReference storageRef;
    private final MutableLiveData<String> profileImageUrl = new MutableLiveData<>();
    private String cachedImageUrl;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        cachedImageUrl = null;
    }

    public LiveData<String> getProfileImageUrl() {
        return profileImageUrl;
    }

    public void uploadImageToFirebaseStorage(Uri imageUri) {
        isLoading.setValue(true);
        StorageReference imageRef = storageRef.child("profile_images/" + auth.getCurrentUser().getUid());
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveImageUrlToFirestore(imageUrl);
                    isLoading.setValue(false);
                }));
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    cachedImageUrl = imageUrl;
                    profileImageUrl.setValue(imageUrl);
                });
    }

    public void loadProfilePicture() {
        isLoading.setValue(true);
        if (cachedImageUrl != null) {
            profileImageUrl.setValue(cachedImageUrl);
            isLoading.setValue(false);
        } else {
            StorageReference imageRef = storageRef.child("profile_images/" + auth.getCurrentUser().getUid());
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                cachedImageUrl = uri.toString();
                profileImageUrl.setValue(cachedImageUrl);
                isLoading.setValue(false);
            }).addOnFailureListener(e -> {
                profileImageUrl.setValue(null);
                isLoading.setValue(false);
            });
        }
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void deleteUserAccount(Activity activity) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    deleteUserFromCollections(userId, task -> {
                        if (task.isSuccessful()) {
                            auth.getCurrentUser().delete()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            auth.signOut();
                                            Intent intent = new Intent(activity, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            activity.startActivity(intent);
                                            activity.finish();
                                        } else {
                                            Toast.makeText(activity, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(activity, "Failed to delete user data from Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }

    private void deleteUserFromCollections(String userId, OnCompleteListener<Void> onCompleteListener) {
        String[] collections = new String[]{
                "Your Coaches",
                "Coaches Requested to Train You",
                "Coaches You're Interested in",
                "Your Athletes",
                "Athletes Interested in Your Coaching",
                "Your Coaching Requests"
        };

        db.collection("users").get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                return Tasks.forException(task.getException());
            }

            List<Task<Void>> deleteTasks = new ArrayList<>();
            for (DocumentSnapshot userDoc : task.getResult()) {
                for (String collection : collections) {
                    deleteTasks.add(userDoc.getReference().collection(collection).document(userId).delete());
                }
            }

            return Tasks.whenAll(deleteTasks);
        }).addOnCompleteListener(onCompleteListener);
    }

    public void logoutUser(Activity activity) {
        auth.signOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}
