package com.mct.auto_clicker.activities;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.getFormatTime;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.dialog.ChooseConfigureDialog;
import com.mct.auto_clicker.overlays.dialog.SettingEditDialog;
import com.mct.auto_clicker.overlays.dialog.SettingStopLoopDialog;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

import java.text.MessageFormat;

public class AutoClickerActivity extends AppCompatActivity implements View.OnClickListener,
        ConfigureListFragment.OnConfigureClickedListener, PermissionsDialogFragment.PermissionDialogListener {

    private SettingSharedPreference sharedPref;
    private ConfigurePermissionPresenter permissionPresenter;

    private DrawerLayout mDrawerLayout;
    private TextView tvStopLoop, tvLoopDelay,
            tvActionDelay, tvClickDuration, tvSwipeDuration, tvZoomDuration,
            tvRandomTimeWait, tvRandomLocation;
    private Button btnPlay;
    private int layerCount = 0;
    private boolean isBackPress;

    private Configure requestedConfigure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_clicker);
        sharedPref = SettingSharedPreference.getInstance(this);
        permissionPresenter = new ConfigurePermissionPresenter(this);

        initUi();

        initToolBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initBtnPlayState();
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

        btnPlay = findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
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

    private void initBtnPlayState() {
        if (!permissionPresenter.isServiceStart()) {
            btnPlay.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_white_bg_primary));
            btnPlay.setText(R.string.start);
        } else {
            btnPlay.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_white_bg_orange));
            btnPlay.setText(R.string.stop);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auto_clicker_activity, menu);
        return true;
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
                GeneralSettingFragment fragment = GeneralSettingFragment.newInstance();
                addFragmentToMainFrame(fragment, GeneralSettingFragment.class.getName());
                break;
            case R.id.rl_solving_trouble:
                showPermissionRequest(false);
                break;
            case R.id.btn_play:
                onPlayClicked();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_configure_list) {
            ConfigureListFragment fragment = ConfigureListFragment.newInstance();
            addFragmentToMainFrame(fragment, ConfigureListFragment.class.getName());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPermissionRequest(boolean registerListener) {
        PermissionsDialogFragment fragment;
        fragment = registerListener ? PermissionsDialogFragment.newInstance(this) : PermissionsDialogFragment.newInstance();

        fragment.show(getSupportFragmentManager(), PermissionsDialogFragment.class.getName());
    }

    private String getTextFormat(@StringRes int strRes, @NonNull Object arguments) {
        return MessageFormat.format(getString(strRes), arguments.toString());
    }

    private void addFragmentToMainFrame(Fragment fragment, String name) {
        if (layerCount == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        layerCount++;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out, R.anim.anim_left_in, R.anim.anim_left_out);
        transaction.add(R.id.rl_frame, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    public void removeFragmentFromMainFrame() {
        layerCount--;
        getSupportFragmentManager().popBackStack();
        if (layerCount == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (layerCount == 0) {
                if (isBackPress) {
                    super.onBackPressed();
                    isBackPress = false;
                } else {
                    Toast.makeText(this, "Nhấn back thêm lần nữa để thoát.", Toast.LENGTH_SHORT).show();
                    isBackPress = true;
                }
            } else {
                removeFragmentFromMainFrame();
                isBackPress = false;
            }
        }
    }

    private void onPlayClicked() {
        if (permissionPresenter.isServiceStart()) {
            permissionPresenter.stopConfigure();
            requestedConfigure = null;
            initBtnPlayState();
        } else {
            onClicked(null);
        }
    }

    @Override
    public void onClicked(Configure configure) {

        requestedConfigure = configure;

        if (!permissionPresenter.arePermissionsGranted()) {
            showPermissionRequest(true);
            return;
        }
        onPermissionsGranted();
    }

    @Override
    public void onPermissionsGranted() {
        if (requestedConfigure != null) {
            permissionPresenter.loadConfigure(requestedConfigure, this::initBtnPlayState);
            initBtnPlayState();
        } else {
            ChooseConfigureDialog dialog = new ChooseConfigureDialog(this, this::onClicked, false);
            dialog.create(null);
        }
    }


}