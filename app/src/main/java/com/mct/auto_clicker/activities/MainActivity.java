package com.mct.auto_clicker.activities;

import static com.mct.auto_clicker.database.domain.Action.Zoom.ZOOM_IN;
import static com.mct.auto_clicker.database.domain.Action.Zoom.ZOOM_OUT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.FloatingMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addConfigure();

        Button btnStartDemo = findViewById(R.id.btn_start_demo);
        Button btnAllowSAW = findViewById(R.id.btn_allow_system_alert_window);
        Button btnAllowAS = findViewById(R.id.btn_allow_accessibility_service);

        btnAllowSAW.setOnClickListener(view -> {
            if (!Settings.canDrawOverlays(this)) {
                askPermission();
            } else {
                Toast.makeText(this, "SYSTEM_ALERT_WINDOW is granted", Toast.LENGTH_SHORT).show();
            }
        });
        btnAllowAS.setOnClickListener(view -> onAccessibilityClicked());
        btnStartDemo.setOnClickListener(view -> {
            if (!Settings.canDrawOverlays(this)) {
                askPermission();
                return;
            }
            startService(new Intent(MainActivity.this, FloatingMenu.class));
        });
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void onAccessibilityClicked() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    void addConfigure() {
        Repository.getInstance(this).deleteConfigures(Repository.getInstance(this).getAllConfigures());
        List<Action> actionList = new ArrayList<>();

        actionList.add(new Action.Click(0L, 0L, "click1", 0L, 1L, 540, 1000));
//        actionList.add(new Action.Zoom(0L, 0L, "zoom1", 100L, 600L, ZOOM_IN, 540, 300, 540, 1800));
//
//        actionList.add(new Action.Swipe(0L, 0L, "swipe1", 100L, 600L, 1, 500, 950, 500));
//
//        actionList.add(new Action.Swipe(0L, 0L, "swipe1", 100L, 600L, 950, 500, 1, 500));
//
//        actionList.add(new Action.Zoom(0L, 0L, "zoom1", 100L, 600L, ZOOM_OUT, 540, 300, 540, 1800));

        Repository.getInstance(this).addConfigure(new Configure(1L, "config 1", actionList, 10000L));

//        Repository.getInstance(this).deleteConfigure(Repository.getInstance(this).getConfigure(4L));

        List<Configure> mList = Repository.getInstance(this).getAllConfigures();
        Log.e("ddd", "onCreate: " + mList.toString());

//        List<Action> mActions = Repository.getInstance(this).getAllActions();
//        Log.e("ddd", "onCreate: " + mActions.toString());
    }
}