package com.example.forge.ui.navbar.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TournamentsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TournamentsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Double click on a date in the calendar to add a tournament");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
