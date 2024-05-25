package com.example.forge.ui.profile;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileViewModel extends AndroidViewModel {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private MutableLiveData<String> profileImageUrl = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    public LiveData<String> getProfileImageUrl() {
        return profileImageUrl;
    }

    public void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference imageRef = storageRef.child("profile_images/" + auth.getCurrentUser().getUid());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageUrlToFirestore(imageUrl);
                    });
                });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> profileImageUrl.setValue(imageUrl));
    }

    public void loadProfilePicture() {
        StorageReference imageRef = storageRef.child("profile_images/" + auth.getCurrentUser().getUid());

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            profileImageUrl.setValue(imageUrl);
        });
    }

    public void deleteUserAccount() {
        auth.getCurrentUser().delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        auth.signOut();
                    }
                });
    }

    public void logoutUser() {
        auth.signOut();
    }
}

