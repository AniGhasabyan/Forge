package com.example.forge.ui.navbar.schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.forge.ui.navbar.diet.DietViewModel;

public class ScheduleViewModelFactory implements ViewModelProvider.Factory{
    private String userRole;
    private String userUID;

    public ScheduleViewModelFactory(String userRole, String userUID) {
        this.userRole = userRole;
        this.userUID = userUID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ScheduleViewModel.class)) {
            return (T) new ScheduleViewModel(userRole, userUID);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
