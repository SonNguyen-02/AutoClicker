package com.mct.auto_clicker.activities;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.dialog.DialogHelper.getFormatTime;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.dialog.SettingEditDialog;
import com.mct.auto_clicker.dialog.SettingStopLoopDialog;
import com.mct.auto_clicker.overlays.FloatingMenu;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class AutoClickerActivity extends AppCompatActivity implements View.OnClickListener {

    private SettingSharedPreference sharedPref;

    private DrawerLayout mDrawerLayout;
    private TextView tvStopLoop, tvLoopDelay,
            tvActionDelay, tvClickDuration, tvSwipeDuration, tvZoomDuration,
            tvRandomTimeWait, tvRandomLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_clicker);
        sharedPref = SettingSharedPreference.getInstance(this);
        initUi();

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

    private void initUi() {
        findViewById(R.id.rl_stop_loop).setOnClickListener(this);
        findViewById(R.id.rl_loop_delay).setOnClickListener(this);
        findViewById(R.id.rl_action_delay).setOnClickListener(this);
        findViewById(R.id.rl_click_duration).setOnClickListener(this);
        findViewById(R.id.rl_swipe_duration).setOnClickListener(this);
        findViewById(R.id.rl_zoom_duration).setOnClickListener(this);
        findViewById(R.id.rl_increase_random_action_delay_time).setOnClickListener(this);
        findViewById(R.id.rl_random_location).setOnClickListener(this);
        findViewById(R.id.rl_remove_ads).setOnClickListener(this);
        findViewById(R.id.rl_general_setting).setOnClickListener(this);
        findViewById(R.id.rl_solving_trouble).setOnClickListener(this);

        tvStopLoop = findViewById(R.id.tv_stop_loop);
        tvLoopDelay = findViewById(R.id.tv_loop_delay);
        tvActionDelay = findViewById(R.id.tv_action_delay);
        tvClickDuration = findViewById(R.id.tv_click_duration);
        tvSwipeDuration = findViewById(R.id.tv_swipe_duration);
        tvZoomDuration = findViewById(R.id.tv_zoom_duration);
        tvRandomTimeWait = findViewById(R.id.tv_random_time_wait);
        tvRandomLocation = findViewById(R.id.tv_random_location);
        initData();
    }

    private void initData() {
        tvStopLoop.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds));
        int typeLoop = sharedPref.getTypeStopTheLoop();
        switch (typeLoop) {
            case RUN_TYPE_INFINITY:
                tvStopLoop.setText(R.string.setting_content_desc_infinite);
                break;
            case RUN_TYPE_AMOUNT:
                int amount = sharedPref.getStopLoopByAmount();
                tvStopLoop.setText(getTextFormat(R.string.setting_content_desc_n_time, amount));
                break;
            case RUN_TYPE_TIME:
                String timeFormat = getFormatTime(sharedPref.getStopLoopByTime());
                tvStopLoop.setText(getTextFormat(R.string.setting_content_desc_countdown_timer, timeFormat));
                break;
        }
        tvLoopDelay.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getLoopDelay()));
        tvActionDelay.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getActionDelay()));
        tvClickDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getClickExecTime()));
        tvSwipeDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getSwipeExecTime()));
        tvZoomDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getZoomExecTime()));
        tvRandomTimeWait.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getIncreaseRandomActionDelayTime()));
        tvRandomLocation.setText(getTextFormat(R.string.setting_content_desc_n_px, sharedPref.getRandomLocation()));
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
        SettingSharedPreference.getInstance(this)
                .setRandomLocation(5)
                .setIncreaseRandomActionDelayTime(20)
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        OverlayDialogController dialog;
        switch (view.getId()) {
            case R.id.rl_stop_loop:
                dialog = new SettingStopLoopDialog(this);
                dialog.create(() -> {
                    int typeLoop = sharedPref.getTypeStopTheLoop();
                    switch (typeLoop) {
                        case RUN_TYPE_INFINITY:
                            tvStopLoop.setText(R.string.setting_content_desc_infinite);
                            break;
                        case RUN_TYPE_AMOUNT:
                            int amount = sharedPref.getStopLoopByAmount();
                            tvStopLoop.setText(getTextFormat(R.string.setting_content_desc_n_time, amount));
                            break;
                        case RUN_TYPE_TIME:
                            String timeFormat = getFormatTime(sharedPref.getStopLoopByTime());
                            tvStopLoop.setText(getTextFormat(R.string.setting_content_desc_countdown_timer, timeFormat));
                            break;
                    }
                });
                break;
            case R.id.rl_loop_delay:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.CF_DELAY_LOOP);
                dialog.create(() -> tvLoopDelay.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getLoopDelay())));
                break;
            case R.id.rl_action_delay:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ACT_DELAY_ACTION);
                dialog.create(() -> tvActionDelay.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getActionDelay())));
                break;
            case R.id.rl_click_duration:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ACT_CLICK_DURATION);
                dialog.create(() -> tvClickDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getClickExecTime())));
                break;
            case R.id.rl_swipe_duration:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ACT_SWIPE_DURATION);
                dialog.create(() -> tvSwipeDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getSwipeExecTime())));
                break;
            case R.id.rl_zoom_duration:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ACT_ZOOM_DURATION);
                dialog.create(() -> tvZoomDuration.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getZoomExecTime())));
                break;
            case R.id.rl_increase_random_action_delay_time:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ADT_INCREASE_RAND_ACTION_DELAY_TIME);
                dialog.create(() -> tvRandomTimeWait.setText(getTextFormat(R.string.setting_content_desc_n_milliseconds, sharedPref.getIncreaseRandomActionDelayTime())));
                break;
            case R.id.rl_random_location:
                dialog = new SettingEditDialog(this, SettingEditDialog.EditType.ADT_RAND_LOCATION);
                dialog.create(() -> tvRandomLocation.setText(getTextFormat(R.string.setting_content_desc_n_px, sharedPref.getRandomLocation())));
                break;
            case R.id.rl_remove_ads:
                break;
            case R.id.rl_general_setting:
                break;
            case R.id.rl_solving_trouble:
                break;
        }
    }

    private String getTextFormat(@StringRes int strRes, Object... arguments) {
        return MessageFormat.format(getString(strRes), arguments);
    }
}