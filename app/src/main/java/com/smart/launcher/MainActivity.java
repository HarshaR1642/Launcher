package com.smart.launcher;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.smart.launcher.adapter.AppHelper;
import com.smart.launcher.adapter.AppIconAdapter;
import com.smart.launcher.adapter.AppViewModel;
import com.smart.launcher.receiver.Receiver;
import com.smart.launcher.utility.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AppViewModel appViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver();
        hideSystemUI();
        loadApp();
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    protected void registerReceiver() {

        Receiver receiver = new Receiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        loadApp();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");

        registerReceiver(receiver, filter);
    }

    public void hideSystemUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
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
             if (packageName.equals(Constants.PACKAGE_NAME) || packageName.equals("com.service.keylessrn.activity")) {
                appListHelper.add(new AppHelper(packageName, appName, appIcon));
            }
        }

        GridView appGrid = findViewById(R.id.appGrid);
        appGrid.setAdapter(new AppIconAdapter(this, appListHelper));
    }
}