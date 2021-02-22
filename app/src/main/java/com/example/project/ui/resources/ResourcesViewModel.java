package com.example.project.ui.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ResourcesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ResourcesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Here you can download past projects.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
