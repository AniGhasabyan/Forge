package com.example.myapplication.ui.navbar.diet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DietViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DietViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is diet fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
