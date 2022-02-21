package com.mct.auto_clicker.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.FloatingMenu;
import com.mct.auto_clicker.presenter.MySharedPreference;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_clicker);

        initToolBar();

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
            startService(new Intent(AutoClickerActivity.this, FloatingMenu.class));
        });
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auto_clicker_activity, menu);
        return true;
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
        MySharedPreference.getInstance(this)
                .setRandomLocation(5)
                .setIncreaseRandomWaitTime(20)
                .commit();
        actionList.add(new Action.Click(0L, 0L, "click1", 50L, 50L, 540, 1000, true));
//        actionList.add(new Action.Zoom(0L, 0L, "zoom1", 100L, 600L, Action.Zoom.ZOOM_IN, 540, 300, 540, 1800));
//
//        actionList.add(new Action.Swipe(0L, 0L, "swipe1", 100L, 600L, 1, 500, 950, 500));
//
//        actionList.add(new Action.Swipe(0L, 0L, "swipe1", 100L, 600L, 950, 500, 1, 500));
//
//        actionList.add(new Action.Zoom(0L, 0L, "zoom1", 100L, 600L, Action.Zoom.ZOOM_OUT, 540, 300, 540, 1800));

        Repository.getInstance(this).addConfigure(new Configure(1L, "config 1", actionList, 0, 10000L));

//        Repository.getInstance(this).deleteConfigure(Repository.getInstance(this).getConfigure(4L));

        List<Configure> mList = Repository.getInstance(this).getAllConfigures();
        Log.e("ddd", "onCreate: " + mList.toString());

//        List<Action> mActions = Repository.getInstance(this).getAllActions();
//        Log.e("ddd", "onCreate: " + mActions.toString());
    }
}