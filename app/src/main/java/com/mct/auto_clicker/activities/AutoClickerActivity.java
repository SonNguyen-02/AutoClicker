package com.mct.auto_clicker.activities;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.getFormatTime;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.config.Constant;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.fragment.ConfigureListFragment;
import com.mct.auto_clicker.fragment.GeneralSettingFragment;
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
    private NavigationView mNavigationView;
    private TextView tvStopLoop, tvLoopDelay,
            tvActionDelay, tvClickDuration, tvSwipeDuration, tvZoomDuration,
            tvRandomTimeWait, tvRandomLocation;
    private Button btnPlay;
    private boolean isBackPress;

    private Configure requestedConfigure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_clicker);

        if (ConfigurePermissionPresenter.isAccessibilityServiceEnabled(this, AutoClickerService.class)) {
            startService(new Intent(this, AutoClickerService.class));
        }

        sharedPref = SettingSharedPreference.getInstance(this);
        permissionPresenter = new ConfigurePermissionPresenter(this);
        initUi();
        initAds();
        loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionPresenter.registerStopServiceListener(this::initBtnPlayState);
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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        initData();
        initToolBar();
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
        mNavigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initAds() {
        MobileAds.initialize(getApplicationContext(), initializationStatus -> {
        });
        AdView adView = findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void initBtnPlayState() {
        if (!permissionPresenter.isServiceStart()) {
            btnPlay.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_white_bg_primary_stroke_grey_corn_4));
            btnPlay.setText(R.string.start);
        } else {
            btnPlay.setBackground(ContextCompat.getDrawable(this, R.drawable.ripple_white_bg_orange_stroke_grey_corn_4));
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
                goToGeneralSetting();
                break;
            case R.id.rl_solving_trouble:
                showPermissionRequest(false);
                break;
            case R.id.btn_play:
                onPlayClicked();
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_configure_list:
                ConfigureListFragment fragmentConfig = ConfigureListFragment.newInstance();
                addFragmentToMainFrame(fragmentConfig, ConfigureListFragment.class.getName());
                return true;
            case R.id.menu_setting:
                goToGeneralSetting();
                return true;
            case R.id.menu_solving_trouble:
                showPermissionRequest(false);
                return true;
            case R.id.menu_remove_ads:

                return true;
            case R.id.menu_rate_app:
            case R.id.menu_feedback:
                AppRater.goToPlayStore(this);
                return true;
            case R.id.menu_share_app:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Auto clicker");
                    String shareMessage = "AutoClicker download link\n" + Constant.CH_PLAY_URL;
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share to"));
                } catch (Exception e) {
                    //e.toString();
                }
                return true;
            case R.id.menu_license:
                return true;
            case R.id.menu_privacy_policy:
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, Constant.PRIVACY_POLICY_URL);
                startActivity(intent);
                return true;
        }
        return false;
    }

    private void goToGeneralSetting() {
        lockScreenOrientation(true);
        GeneralSettingFragment fragment = GeneralSettingFragment.newInstance();
        addFragmentToMainFrame(fragment, GeneralSettingFragment.class.getName());
        if (permissionPresenter.isServiceStart()) {
            permissionPresenter.stopConfigure();
            fragment.setOnExistsSetting(() -> {
                if (requestedConfigure != null) {
                    permissionPresenter.loadConfigure(requestedConfigure);
                }
            });
        }
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
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out, R.anim.anim_left_in, R.anim.anim_left_out);
        transaction.add(R.id.rl_frame, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    public void removeFragmentFromMainFrame() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        lockScreenOrientation(false);
        getSupportFragmentManager().popBackStackImmediate();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void lockScreenOrientation(boolean lock) {
        if (lock) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            isBackPress = false;
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (isBackPress) {
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                    isBackPress = false;
                    finish();
                } else {
                    Toast.makeText(this, R.string.back_press, Toast.LENGTH_SHORT).show();
                    isBackPress = true;
                }
            } else {
                removeFragmentFromMainFrame();
                isBackPress = false;
            }
        }
    }

    private void onPlayClicked() {
        if (permissionPresenter.arePermissionsGranted()) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
            } else {
                Log.d("ddd", "The interstitial ad wasn't ready yet.");
            }
        }
        if (permissionPresenter.isServiceStart()) {
            permissionPresenter.stopConfigure();
            requestedConfigure = null;
        } else {
            // khi click vao start thi se hien rate dialog neu du dieu kien
            boolean isLaunchSuccess = AppRater.launch(this);
            if (!isLaunchSuccess) {
                onClicked(null);
            }
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
            permissionPresenter.loadConfigure(requestedConfigure);
        } else {
            ChooseConfigureDialog dialog = new ChooseConfigureDialog(this, this::onClicked, false);
            dialog.create(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionPresenter.unregisterStopServiceListener();
        if (handler != null) {
            handler.removeCallbacks(loadAd);
        }
    }

    private Handler handler;
    private final Runnable loadAd = this::loadAd;
    private InterstitialAd mInterstitialAd;

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("ddd", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("ddd", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // wait 5 min and load new ads
                                handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(loadAd, 5 * 60 * 1000);
                                Log.d("ddd", "The ad was shown.");
                            }
                        });
                        Log.i("ddd", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("ddd", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }
}