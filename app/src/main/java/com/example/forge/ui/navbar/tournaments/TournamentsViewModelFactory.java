package com.example.forge.ui.navbar.tournaments;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TournamentsViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;

    public TournamentsViewModelFactory(String userRole) {
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TournamentsViewModel.class)) {
            return (T) new TournamentsViewModel(userRole);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
