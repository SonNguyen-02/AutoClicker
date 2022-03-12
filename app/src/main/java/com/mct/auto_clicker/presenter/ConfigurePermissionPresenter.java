package com.mct.auto_clicker.presenter;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

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
        if (configure.getId() == 0 || getConfigure(configure.getId()) == null) {
            return mRepository.addConfigure(configure);
        } else {
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

    public boolean isConfigureChanged(@NonNull Configure configure) {
        if (configure.getId() == 0) {
            // config moi va phai co it nhat 1 action
            return !configure.getActions().isEmpty();
        }
        Configure rootConfigure = getConfigure(configure.getId());
        if (rootConfigure == null) {
            return !configure.getActions().isEmpty();
        }
        return !rootConfigure.equals(configure);
    }

    private boolean isNameExists(String name) {
        return mRepository.isConfigureNameExists(name);
    }

    public String getSuffixConfig(String name, boolean isCopySuffix) {
        int i = 1;
        name = clearSuffix(name.trim(), isCopySuffix);
        name += isCopySuffix ? " - copy" : " 0";
        while (true) {
            if (isNameExists(name)) {
                name = clearSuffix(name, isCopySuffix);
                name += isCopySuffix ? " - copy(" + i + ")" : " " + i;
                i++;
            } else {
                return name;
            }
        }
    }

    @NonNull
    private String clearSuffix(String name, boolean isCopySuffix) {
        if (isCopySuffix) {
            name = name.replaceAll(" - copy$", "").replaceAll(" - copy\\(\\d+\\)$", "").trim();
        } else {
            name = name.replaceAll(" \\d+$", "").trim();
        }
        return name;
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

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }
}
