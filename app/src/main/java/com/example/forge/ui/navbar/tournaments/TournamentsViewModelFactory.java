package com.example.forge.ui.navbar.tournaments;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TournamentsViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;
    private String userUID;

    public TournamentsViewModelFactory(String userRole, String userUID) {
        this.userRole = userRole;
        this.userUID = userUID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TournamentsViewModel.class)) {
            return (T) new TournamentsViewModel(userRole, userUID);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
