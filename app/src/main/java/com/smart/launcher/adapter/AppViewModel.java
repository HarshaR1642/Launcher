package com.smart.launcher.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.smart.launcher.receiver.Receiver;

import java.util.ArrayList;

public class AppViewModel extends ViewModel {

    MutableLiveData<ArrayList<AppHelper>> userLiveData;
    ArrayList<AppHelper> userArrayList;

    public AppViewModel() {
        userLiveData = new MutableLiveData<>();

        // call your Rest API in init method
        init();
    }

    public MutableLiveData<ArrayList<AppHelper>> getUserMutableLiveData() {
        return userLiveData;
    }

    public void init() {
        userLiveData.setValue(userArrayList);
    }

    public Receiver getReceiver() {
        return new Receiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                        // App installed
                        String packageName = intent.getData().getSchemeSpecificPart();

                        userArrayList.add(new AppHelper(packageName, ""));
                        userLiveData.setValue(userArrayList);
                    } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        // App removed
                        String packageName = intent.getData().getSchemeSpecificPart();
                        userArrayList.add(new AppHelper(packageName, ""));
                        userLiveData.setValue(userArrayList);
                    }
                }
            }
        };
    }
}