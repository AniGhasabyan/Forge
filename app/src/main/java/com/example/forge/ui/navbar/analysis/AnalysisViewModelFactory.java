package com.example.forge.ui.navbar.analysis;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.forge.ui.navbar.progress.ProgressViewModel;

public class AnalysisViewModelFactory implements ViewModelProvider.Factory {
    private String userRole;
    public AnalysisViewModelFactory(String userRole) {
        this.userRole = userRole;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AnalysisViewModel.class)) {
            return (T) new AnalysisViewModel(userRole);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
