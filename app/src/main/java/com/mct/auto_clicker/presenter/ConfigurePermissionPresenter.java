package com.mct.auto_clicker.presenter;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Configure;

import java.util.List;

public class ConfigurePermissionPresenter {

    private final Context mContext;
    private final Repository mRepository;
    private final SettingSharedPreference mSharedPreference;
    private static AutoClickerService.LocalService CLICKER_SERVICE = null;

    public ConfigurePermissionPresenter(Context context) {
        this.mContext = context;
        mRepository = Repository.getInstance(context);
        mSharedPreference = SettingSharedPreference.getInstance(context);
        AutoClickerService.getLocalService(localService -> CLICKER_SERVICE = localService);
    }

    public boolean isOverlayPermissionValid() {
        return Settings.canDrawOverlays(mContext);
    }

    public boolean isAccessibilityPermissionValid() {
        return CLICKER_SERVICE != null;
    }

    public boolean arePermissionsGranted() {
        return isOverlayPermissionValid() && isAccessibilityPermissionValid();
    }

    public List<Configure> getAllConfigure() {
        return mRepository.getAllConfigures();
    }

    public long createConfigure(String name) {
        Configure configure = null;
        int type = mSharedPreference.getTypeStopTheLoop();
        int timeDelay = mSharedPreference.getLoopDelay();
        switch (type) {
            case RUN_TYPE_INFINITY:
                configure = new Configure(0, name, null, timeDelay);
                break;
            case RUN_TYPE_AMOUNT:
                int amount = mSharedPreference.getStopLoopByAmount();
                configure = new Configure(0, name, null, timeDelay, amount);
                break;
            case RUN_TYPE_TIME:
                long timeStop = mSharedPreference.getStopLoopByTime();
                configure = new Configure(0, name, null, timeDelay, timeStop);
                break;
        }
        if (configure != null) {
            return mRepository.addConfigure(configure);
        }
        return -1L;
    }

    public void renameConfigure(@NonNull Configure configure, String name) {
        configure.setName(name);
        mRepository.updateConfigure(configure);
    }

    public void deleteConfigure(Configure configure) {
        mRepository.deleteConfigure(configure);
    }

    public Configure getConfigure(long configureId) {
        return mRepository.getConfigure(configureId);
    }

    public void loadConfigure(Configure configure, AutoClickerService.OnConfigureStopListener listener) {
        if (CLICKER_SERVICE != null) {
            CLICKER_SERVICE.init(configure, listener);
        }
    }

    public void stopConfigure() {
        if (CLICKER_SERVICE != null) {
            CLICKER_SERVICE.stop();
        }
    }
}
