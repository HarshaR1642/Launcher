package com.smart.launcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.launcher.R;
import com.smart.launcher.utility.Constants;

import java.util.List;

public class AppIconAdapter extends BaseAdapter {

    private final List<AppHelper> appList;
    private final Context context;

    public AppIconAdapter(Context context, List<AppHelper> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.app_layout, parent, false);
        }

        AppHelper app = appList.get(position);

        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);
        appName.setText(app.getName());
        appIcon.setImageDrawable(app.getIcon());

        convertView.setOnClickListener(view -> {
            Intent launchIntent = view.getContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            if (launchIntent != null) {
                view.getContext().startActivity(launchIntent);
            }
        });

        return convertView;
    }
}
