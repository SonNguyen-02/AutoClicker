package com.mct.auto_clicker;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionExecutor;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

public class AutoClickerService extends AccessibilityService {

    private Boolean isStarted = false;

    private static LocalService LOCAL_SERVICE_INSTANCE;
    private static RegisterServiceConnectionListener LOCAL_SERVICE_LISTENER;

    private ActionExecutor executor;

    public interface OnConfigureStopListener {
        void onStop();
    }

    public interface RegisterServiceConnectionListener {
        void callBack(LocalService localService);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("ddd", "onServiceConnected: ");
        LOCAL_SERVICE_INSTANCE = new LocalService();
        invokeListener();
    }

    public static void getLocalService(RegisterServiceConnectionListener serviceConnectionListener) {
        if (LOCAL_SERVICE_LISTENER != null) {
            LOCAL_SERVICE_LISTENER.callBack(null);
            LOCAL_SERVICE_LISTENER = null;
        }
        LOCAL_SERVICE_LISTENER = serviceConnectionListener;
        invokeListener();
    }

    private static void invokeListener() {
        if (LOCAL_SERVICE_LISTENER != null) {
            LOCAL_SERVICE_LISTENER.callBack(LOCAL_SERVICE_INSTANCE);
        }
    }

    public class LocalService {

        private Configure mConfigure;
        private long timeStart;
        private int countExec;

        public void init(@NonNull Configure configure, OnConfigureStopListener mOnConfigureStopListener) {
            this.mConfigure = configure;
            switch (mConfigure.getRunType()) {
                case RUN_TYPE_INFINITY:
                    executor = new ActionExecutor(gesture -> dispatchGesture(gesture, null, null), this::startConfigureLoop);
                    break;
                case RUN_TYPE_TIME:
                    timeStart = System.currentTimeMillis();
                    executor = new ActionExecutor(gesture -> {
                        if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                            dispatchGesture(gesture, null, null);
                        } else {
                            stopWithListener(mOnConfigureStopListener);
                        }
                    }, () -> {
                        if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                            startConfigureLoop();
                        } else {
                            stopWithListener(mOnConfigureStopListener);
                        }
                    });
                    break;
                case RUN_TYPE_AMOUNT:
                    countExec = 1;
                    executor = new ActionExecutor(gesture -> dispatchGesture(gesture, null, null), () -> {
                        if (countExec < mConfigure.getAmountExec()) {
                            countExec++;
                            startConfigureLoop();
                        } else {
                            stopWithListener(mOnConfigureStopListener);
                        }
                    });
                    break;
            }
            start(mConfigure);
        }

        public boolean isStart() {
            return isStarted;
        }

        private void startConfigureLoop() {
            isStarted = false;
            new Handler(Looper.getMainLooper()).postDelayed(() -> start(mConfigure), mConfigure.getTimeDelay());
        }

        private void stopWithListener(OnConfigureStopListener mOnConfigureStopListener) {
            if (mOnConfigureStopListener != null) {
                mOnConfigureStopListener.onStop();
            }
            stop();
        }

        public void start(Configure configure) {
            if (isStarted) {
                return;
            }
            isStarted = true;
            executor.executeActions(configure.getActions(), getApplicationContext());
        }

        public void stop() {
            if (!isStarted) {
                return;
            }
            mConfigure = null;
            isStarted = false;
            if (executor != null) {
                executor.removeListener();
                executor = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.e("ddd", "onDestroy: ");
        if (LOCAL_SERVICE_INSTANCE != null) {
            LOCAL_SERVICE_INSTANCE.stop();
            LOCAL_SERVICE_INSTANCE = null;
            invokeListener();
            LOCAL_SERVICE_LISTENER = null;
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
