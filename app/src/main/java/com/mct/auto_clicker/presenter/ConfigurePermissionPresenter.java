package com.mct.auto_clicker.presenter;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.utils.ScreenMetrics;

import java.util.List;

public class ConfigurePermissionPresenter {

    private final Context mContext;
    private final Repository mRepository;
    private final SettingSharedPreference mSharedPreference;

    public ConfigurePermissionPresenter(Context context) {
        this.mContext = context;
        mRepository = Repository.getInstance(context);
        mSharedPreference = SettingSharedPreference.getInstance(context);
    }

    public boolean isOverlayPermissionValid() {
        return Settings.canDrawOverlays(mContext);
    }

    public boolean isAccessibilityPermissionValid() {
        return AutoClickerService.getLocalService() != null;
    }

    public boolean arePermissionsGranted() {
        return isOverlayPermissionValid() && isAccessibilityPermissionValid();
    }

    public long getCountConfigures() {
        return mRepository.getCountConfigures();
    }

    public List<Configure> getAllConfigure() {
        return mRepository.getAllConfigures();
    }

    public long createConfigure(Configure configure) {
        return mRepository.addConfigure(configure);
    }

    public long createConfigure(String name) {
        Configure configure = getNewConfigure(name);
        if (configure != null) {
            return mRepository.addConfigure(configure);
        }
        return -1L;
    }

    public long saveConfigure(@NonNull Configure configure) {
        if (configure.getId() == 0) {
            return mRepository.addConfigure(configure);
        } else {
            // next day need fix
            mRepository.updateConfigure(configure);
        }
        return configure.getId();
    }

    public Configure getNewConfigure(String name) {
        int type = mSharedPreference.getTypeStopTheLoop();
        int timeDelay = mSharedPreference.getLoopDelay();
        ScreenMetrics screenMetrics = new ScreenMetrics(mContext);
        Configure configure = null;
        switch (type) {
            case RUN_TYPE_INFINITY:
                configure = new Configure(0, name, null, screenMetrics.getOrientation(), timeDelay);
                break;
            case RUN_TYPE_AMOUNT:
                int amount = mSharedPreference.getStopLoopByAmount();
                configure = new Configure(0, name, null, screenMetrics.getOrientation(), timeDelay, amount);
                break;
            case RUN_TYPE_TIME:
                long timeStop = mSharedPreference.getStopLoopByTime();
                configure = new Configure(0, name, null, screenMetrics.getOrientation(), timeDelay, timeStop);
                break;
        }
        return configure;
    }

    public void renameConfigure(@NonNull Configure configure, String name) {
        configure.setName(name);
        mRepository.updateConfigure(configure);
    }

    public void deleteConfigure(Configure configure) {
        mRepository.deleteConfigure(configure);
    }

    public void deleteConfigures(List<Configure> configures) {
        mRepository.deleteConfigures(configures);
    }

    public Configure getConfigure(long configureId) {
        return mRepository.getConfigure(configureId);
    }

    public void registerStopServiceListener(AutoClickerService.OnLocalServiceChangeListener stopListener) {
        if (AutoClickerService.getLocalService() != null) {
            AutoClickerService.getLocalService().setOnStopListener(stopListener);
        }
    }

    public void unregisterStopServiceListener() {
        if (AutoClickerService.getLocalService() != null) {
            AutoClickerService.getLocalService().setOnStopListener(null);
        }
    }

    public void loadConfigure(Configure configure) {
        if (AutoClickerService.getLocalService() != null) {
            if (!AutoClickerService.getLocalService().isStart()) {
                AutoClickerService.getLocalService().start(mContext, configure);
            } else {
                AutoClickerService.getLocalService().loadConfigure(configure);
            }
        }
    }

    public boolean isServiceStart() {
        if (AutoClickerService.getLocalService() != null) {
            return AutoClickerService.getLocalService().isStart();
        }
        return false;
    }

    public void stopConfigure() {
        if (AutoClickerService.getLocalService() != null) {
            AutoClickerService.getLocalService().stop();
        }
    }
}
