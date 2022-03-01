package com.mct.auto_clicker;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.baseui.overlays.OverlayController;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionDetector;
import com.mct.auto_clicker.executor.ActionExecutor;
import com.mct.auto_clicker.overlays.mainmenu.MainMenu;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

public class AutoClickerService extends AccessibilityService {

    private static LocalService LOCAL_SERVICE_INSTANCE;

    private OverlayController rootOverlayController;

    private Boolean isStarted = false;

    public interface OnServiceStopListener {
        void onStop();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("ddd", "onServiceConnected: ");
        LOCAL_SERVICE_INSTANCE = new LocalService();
    }

    public static LocalService getLocalService() {
        return LOCAL_SERVICE_INSTANCE;
    }

    public class LocalService {

        public boolean isStart() {
            return isStarted;
        }

        public void start(Context context, Configure configure, OnServiceStopListener stopListener) {
            if (isStarted) {
                return;
            }
            isStarted = true;
            ActionDetector actionDetector = new ActionDetector(getApplicationContext(),
                    gesture -> dispatchGesture(gesture, null, null));
            rootOverlayController = new MainMenu(context, configure, actionDetector);
            rootOverlayController.create(() -> {
                stop();
                stopListener.onStop();
            });
        }

        public void loadConfigure(Configure configure) {
            ((MainMenu) rootOverlayController).loadNewConfigure(configure);
        }

        public void stop() {
            if (!isStarted) {
                return;
            }
            isStarted = false;
            if (rootOverlayController != null) {
                rootOverlayController.dismiss();
                rootOverlayController = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.e("ddd", "onDestroy: ");
        isStarted = false;
        if (LOCAL_SERVICE_INSTANCE != null) {
            LOCAL_SERVICE_INSTANCE.stop();
            LOCAL_SERVICE_INSTANCE = null;
        }
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {
    }
}
