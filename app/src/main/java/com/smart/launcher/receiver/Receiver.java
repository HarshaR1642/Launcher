package com.smart.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smart.launcher.MainActivity;
import com.smart.launcher.utility.Constants;

public class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(Constants.TAG, "Broadcast Received " + action);
        if (action != null) {
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                // App installed
                String packageName = intent.getData().getSchemeSpecificPart();
                addToCustomLauncher(context, packageName);
            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                // App removed
                String packageName = intent.getData().getSchemeSpecificPart();
                removeFromCustomLauncher(context, packageName);
            }
        }
    }

    private void removeFromCustomLauncher(Context context, String packageName) {
    }

    private void addToCustomLauncher(Context context, String packageName) {
    }
}