package com.example.hometrainng.activity.ui.tab5;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Tab5ViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public Tab5ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tab5 Model");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
