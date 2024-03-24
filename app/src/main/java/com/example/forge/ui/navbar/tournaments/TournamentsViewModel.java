package com.example.forge.ui.navbar.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TournamentsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TournamentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is calendar of tournaments fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
