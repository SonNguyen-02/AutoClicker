package com.mct.auto_clicker;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionExecutor;

import java.util.function.Consumer;

public class AutoClickerService extends AccessibilityService {

    public static final int ACTION_EXISTS = 101;
    public static final String KEY_ACTION = "key_action";

    private Boolean isStarted = false;

    private static LocalService mLocalService;
    private ActionExecutor executor;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mLocalService = new LocalService();
    }

    public static LocalService getLocalService() {
        return mLocalService;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        int action = intent.getIntExtra(KEY_ACTION, 0);
        if (action == ACTION_EXISTS) {
            mLocalService.stop();
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class LocalService {

        private Configure mConfigure;
        private long timeStart;
        private int countExec;

        public void init(@NonNull Configure configure, Consumer<Boolean> consumer) {
            this.mConfigure = configure;
            countExec = 1;
            // run infinity configure
            if (mConfigure.isRunByTimeStop() == null) {
                executor = new ActionExecutor(gesture -> dispatchGesture(gesture, null, null), () -> {
                    isStarted = false;
                    start(mConfigure);
                });
                start(mConfigure);
                return;
            }
            // configure will stop apter stop time
            if (mConfigure.isRunByTimeStop()) {
                timeStart = System.currentTimeMillis();
                executor = new ActionExecutor(gesture -> {
                    if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                        dispatchGesture(gesture, null, null);
                    } else {
                        stop();
                        consumer.accept(isStarted);
                    }
                }, () -> {
                    if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                        isStarted = false;
                        start(mConfigure);
                    } else {
                        stop();
                        consumer.accept(isStarted);
                    }
                });
            }
            // configure will stop apter count time
            else {
                executor = new ActionExecutor(gesture -> dispatchGesture(gesture, null, null), () -> {
                    if (countExec < mConfigure.getAmountExec()) {
                        countExec++;
                        isStarted = false;
                        start(mConfigure);
                    } else {
                        stop();
                        consumer.accept(isStarted);
                    }
                });
            }
            start(mConfigure);
        }

        public boolean isStart() {
            return isStarted;
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
