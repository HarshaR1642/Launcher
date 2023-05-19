package com.smart.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.smart.launcher.adapter.AppHelper;
import com.smart.launcher.adapter.AppIconAdapter;
import com.smart.launcher.utility.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableLockMode();
        loadApp();
    }

    private void enableLockMode() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm.isLockTaskPermitted(getPackageName())) {
            startLockTask();
            launchApp();
            Log.i(Constants.TAG, "Lock mode enabled");
        } else {
            Intent intent = new Intent(Constants.ACTION_ENABLE_LOCK_MODE);
            intent.setComponent(new ComponentName(Constants.SERVICE_APP_PACKAGE, Constants.SERVICE_APP_RECEIVER_CLASS));
            sendBroadcast(intent);
            Log.i(Constants.TAG, "Lock mode not enabled");
        }
    }

    private void loadApp() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = packageManager.queryIntentActivities(intent, 0);

        List<AppHelper> appListHelper = new ArrayList<>();
        for (ResolveInfo resolveInfo : appList) {
            Drawable appIcon = resolveInfo.loadIcon(packageManager);
            String appName = resolveInfo.loadLabel(packageManager).toString();
            String packageName = resolveInfo.activityInfo.packageName;
            if(packageName.equals(Constants.PACKAGE_NAME) || packageName.equals("com.service.keylessrn.activity")) {
                appListHelper.add(new AppHelper(packageName, appName, appIcon));
            }
        }

        GridView appGrid = findViewById(R.id.appGrid);
        appGrid.setAdapter(new AppIconAdapter(this, appListHelper));
    }

    private void launchApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(Constants.PACKAGE_NAME);
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }

    int backCount = 0;
    @Override
    public void onBackPressed() {
        backCount++;
        if(backCount >= 7){
            backCount = 0;
            stopLockTask();
        }
    }
}