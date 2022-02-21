package com.mct.auto_clicker;

import static com.mct.auto_clicker.database.domain.Configure.TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.TYPE_TIME;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionExecutor;

import java.util.function.Consumer;

public class AutoClickerService extends AccessibilityService {

    private Boolean isStarted = false;

    private static LocalService mLocalService;
    private ActionExecutor executor;

    public interface OnConfigureStopListener {
        void onStop();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("ddd", "onServiceConnected: ");
        mLocalService = new LocalService();
    }

    public static LocalService getLocalService() {
        return mLocalService;
    }

    public class LocalService {

        private Configure mConfigure;
        private long timeStart;
        private int countExec;

        public void init(@NonNull Configure configure, OnConfigureStopListener mOnConfigureStopListener) {
            this.mConfigure = configure;
            switch (mConfigure.getRunType()) {
                case TYPE_INFINITY:
                    executor = new ActionExecutor(gesture -> dispatchGesture(gesture, null, null), this::startConfigureLoop);
                    break;
                case TYPE_TIME:
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
                case TYPE_AMOUNT:
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

        private void stopWithListener(@NonNull OnConfigureStopListener mOnConfigureStopListener) {
            mOnConfigureStopListener.onStop();
            stop();
        }

        public void start(Configure configure) {
            if (isStarted) {
                return;
            }
            isStarted = true;
            executor.executeActions(configure.getActions());
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
        mLocalService = null;
        super.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {
    }
}
