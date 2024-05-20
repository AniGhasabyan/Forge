package com.example.forge.ui.navbar.progress;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProgressViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;
    private String userUID;
    public ProgressViewModelFactory(String userRole, String userUID) {
        this.userRole = userRole;
        this.userUID = userUID;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProgressViewModel.class)) {
            return (T) new ProgressViewModel(userRole, userUID);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
