package com.smart.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.smart.launcher.adapter.AppHelper;
import com.smart.launcher.adapter.AppIconAdapter;
import com.smart.launcher.adapter.AppViewModel;
import com.smart.launcher.receiver.Receiver;
import com.smart.launcher.utility.Constants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private boolean hasAnimated = false, isOpen = false;
  int backCount = 0;

  AppViewModel appViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    loadApp();
    setReceiver();
  }

  private void setReceiver() {
    DownloadReceiver receiver = new DownloadReceiver();
    registerReceiver(
      receiver,
      new IntentFilter("android.intent.action.DOWNLOAD_STARTED")
    );
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
          if (
            action.equals(Intent.ACTION_PACKAGE_ADDED) ||
            action.equals(Intent.ACTION_PACKAGE_REMOVED)
          ) {
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
    getWindow()
      .getDecorView()
      .setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_IMMERSIVE |
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
        View.SYSTEM_UI_FLAG_FULLSCREEN
      );
  }

  public void updateUI(Intent intent) {
    Log.i(
      "Receiver Says:🔊",
      "Got Download Started Intent ->" +
      intent.getExtras().getString("Current_MB") +
      " ->" +
      intent.getExtras().getString("Total_MB")
    );

    TextView ct = findViewById(R.id.currentMBText), tt = findViewById(
      R.id.totalMBText
    );
    if (!hasAnimated) {
      Log.i("Activity Says:🔊", "Animating🚀");

      Animation animation = new Animation() {

        @Override
        protected void applyTransformation(
          float interpolatedTime,
          Transformation t
        ) {
          findViewById(R.id.notiCard)
            .setTranslationY(
              (dp_to_px(MainActivity.this, 65) * interpolatedTime) -
              dp_to_px(MainActivity.this, 65)
            );
        }
      };
      animation.setDuration(500);
      findViewById(R.id.notiCard).startAnimation(animation);
      tt.setText(intent.getExtras().getString("Total_MB").concat(" MB"));

      hasAnimated = true;
    }

    ct.setText(intent.getExtras().getString("Current_MB").concat(" MB"));

    if (
      intent
        .getExtras()
        .getString("Total_MB")
        .equals(intent.getExtras().getString("Current_MB"))
    ) {
      findViewById(R.id.pendingIcon)
        .setBackgroundResource(R.drawable.baseline_check_circle_outline_24);
      Animation animation = new Animation() {

        @Override
        protected void applyTransformation(
          float interpolatedTime,
          Transformation t
        ) {
          findViewById(R.id.notiCard)
            .setTranslationY(
              (dp_to_px(MainActivity.this, 65) * (1 - interpolatedTime)) -
              dp_to_px(MainActivity.this, 65)
            );
        }
      };
      animation.setDuration(500);
      hasAnimated = false;
      new android.os.Handler(Looper.myLooper())
      .postDelayed(
          () -> findViewById(R.id.notiCard).startAnimation(animation),
          1000
        );
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
      if (
        packageName.equals(Constants.PACKAGE_NAME) ||
        packageName.equals("com.service.keylessrn.activity")
      ) {
        appListHelper.add(new AppHelper(packageName, appName, appIcon));
      }
    }

    GridView appGrid = findViewById(R.id.appGrid);
    appGrid.setAdapter(new AppIconAdapter(this, appListHelper));
  }

  private void launchApp() {
    Intent launchIntent = getPackageManager()
      .getLaunchIntentForPackage(Constants.PACKAGE_NAME);
    if (launchIntent != null) {
      startActivity(launchIntent);
    }
  }

  @Override
  public void onBackPressed() {
    backCount++;
    if (backCount >= 7) {
      backCount = 0;
      stopLockTask();
    }
  }

  //Helper Methods
  public void showDownloadStatus(View view) {
    Animation animation = new Animation() {

      @Override
      protected void applyTransformation(
        float interpolatedTime,
        Transformation t
      ) {
        RelativeLayout notiParent = findViewById(R.id.notiCard);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) notiParent.getLayoutParams();

        if (!isOpen) {
          layoutParams.height =
            (int) (
              (dp_to_px(MainActivity.this, 85) * interpolatedTime) +
              dp_to_px(MainActivity.this, 65)
            );
        } else {
          layoutParams.height =
            (int) (
              (dp_to_px(MainActivity.this, 85) * (1 - interpolatedTime)) +
              dp_to_px(MainActivity.this, 65)
            );
        }

        notiParent.requestLayout();
      }
    };
    animation.setDuration(500);
    findViewById(R.id.notiCard).startAnimation(animation);
    isOpen = !isOpen;
  }

  private static int dp_to_px(Context context, int dp) {
    return (int) TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      dp,
      context.getResources().getDisplayMetrics()
    );
  }

  public class DownloadReceiver extends Receiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i("Receiver Says:🔊", "Received");
      if (intent.getAction().equals("android.intent.action.DOWNLOAD_STARTED")) {
        updateUI(intent);
      }
    }
  }
}
