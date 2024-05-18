package com.example.forge.ui.navbar.progress;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProgressViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;
    public ProgressViewModelFactory(String userRole) {
        this.userRole = userRole;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProgressViewModel.class)) {
            return (T) new ProgressViewModel(userRole);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
