package com.example.hometrainng.activity.ui.tab4;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Tab4ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Tab4ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Ths is Tab4 Model");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
