package com.mct.auto_clicker.overlays.mainmenu.executor;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.accessibilityservice.GestureDescription;
import android.os.Handler;
import android.os.Looper;

import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.mainmenu.MainMenu;

public class ActionManager {

    private Configure mConfigure;
    private long timeStart;
    private int countExec;
    private boolean isStart = false;

    private ActionExecutor actionExecutor;

    private final ExecuteCallback callback;

    private MainMenu.OnStopListener onStopListener;

    public interface ExecuteCallback {
        void onExecAction(GestureDescription gesture, int actionCode);
    }

    public ActionManager(ExecuteCallback callback) {
        this.callback = callback;
    }

    public void init(ActionExecutor actionExecutor) {
        this.actionExecutor = actionExecutor;
    }

    public void init(Configure configure) {
        mConfigure = configure;
        switch (mConfigure.getRunType()) {
            case RUN_TYPE_INFINITY:
                actionExecutor.setGestureExecutionListener(callback::onExecAction);
                actionExecutor.setOnExecutionComplete(this::startConfigure);
                break;
            case RUN_TYPE_TIME:
                actionExecutor.setGestureExecutionListener((gesture, actionCode) -> {
                    if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                        callback.onExecAction(gesture, actionCode);
                    } else {
                        stopConfigure();
                    }
                });
                actionExecutor.setOnExecutionComplete(() -> {
                    if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                        this.startConfigure();
                    } else {
                        stopConfigure();
                    }
                });
                break;
            case RUN_TYPE_AMOUNT:
                actionExecutor.setGestureExecutionListener(callback::onExecAction);
                actionExecutor.setOnExecutionComplete(() -> {
                    if (countExec < mConfigure.getAmountExec()) {
                        countExec++;
                        this.startConfigure();
                    } else {
                        stopConfigure();
                    }
                });
                break;
        }
    }

    private void startConfigure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (actionExecutor != null) {
                actionExecutor.executeActions(mConfigure.getActions());
            }
        }, mConfigure.getTimeDelay());
    }

    private void stopConfigure() {
        stop();
        if (onStopListener != null) {
            onStopListener.onStop();
        }
    }

    public boolean isStart() {
        return isStart;
    }

    public void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        countExec = 1;
        timeStart = System.currentTimeMillis();
        actionExecutor.initRandom();
        actionExecutor.executeActions(mConfigure.getActions());
    }

    public void stop() {
        if (!isStart) {
            return;
        }
        isStart = false;
        actionExecutor.stopExecute();
    }

    public void release() {
        stop();
        if (actionExecutor != null) {
            actionExecutor.removeListener();
            actionExecutor = null;
        }
        mConfigure = null;
    }

    public void setOnStopListener(MainMenu.OnStopListener onStopListener) {
        this.onStopListener = onStopListener;
    }

}
