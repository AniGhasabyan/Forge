package com.example.forge.ui.navbar.diet;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DietViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;
    private String userUID;

    public DietViewModelFactory(String userRole, String userUID) {
        this.userRole = userRole;
        this.userUID = userUID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DietViewModel.class)) {
            return (T) new DietViewModel(userRole, userUID);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
