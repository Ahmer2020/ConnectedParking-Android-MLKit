package com.bbits.park;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class GlobalData extends ViewModel {
    // Create a LiveData with a String
    private MutableLiveData<String> currentName;

    public MutableLiveData<String> getCurrentName() {
        if (currentName == null) {
            currentName = new MutableLiveData<String>();
        }
        return currentName;
    }

//    MutableLiveData<String> listen = new MutableLiveData<>();
//
//listen.setValue("Changed value"); //Initilize with a value
//
//listen.observe(this,{new Observer<String>() {
//        @Override
//        public void onChanged(String changedValue) {
//            //Do something with the changed value
//        }
//    }});

}
