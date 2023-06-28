package com.smart.launcher.adapter;

import android.graphics.drawable.Drawable;

public class AppHelper {
    String packageName;
    String name;
    Drawable icon;

    public AppHelper(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
    }

    public AppHelper(String packageName, String name, Drawable icon) {
        this.packageName = packageName;
        this.name = name;
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }
}
