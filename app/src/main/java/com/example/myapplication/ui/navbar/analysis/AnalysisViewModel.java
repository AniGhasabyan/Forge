package com.example.myapplication.ui.navbar.analysis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnalysisViewModel  extends ViewModel {

    private final MutableLiveData<String> mText;

    public AnalysisViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is competition analysis fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
