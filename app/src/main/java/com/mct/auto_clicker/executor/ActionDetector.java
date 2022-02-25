package com.mct.auto_clicker.executor;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.mainmenu.MainMenu;

public class ActionDetector {

    private Configure mConfigure;
    private long timeStart;
    private int countExec;
    private boolean isStart = false;

    private ActionExecutor actionExecutor;

    private final ExecuteCallback callback;

    private MainMenu.OnStopListener onStopListener;

    public interface ExecuteCallback {
        void onExecAction(GestureDescription gesture);
    }

    public ActionDetector(Context context, ExecuteCallback callback) {
        actionExecutor = new ActionExecutor(context);
        this.callback = callback;
    }

    public void init(Configure configure) {
        mConfigure = configure;
        switch (mConfigure.getRunType()) {
            case RUN_TYPE_INFINITY:
                actionExecutor.setGestureExecutionListener(callback::onExecAction);
                actionExecutor.setOnExecutionComplete(this::startConfigure);
                break;
            case RUN_TYPE_TIME:
                actionExecutor.setGestureExecutionListener(gesture -> {
                    if (System.currentTimeMillis() - timeStart <= mConfigure.getTimeStop()) {
                        callback.onExecAction(gesture);
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
        actionExecutor.setCanExecute(true);
        actionExecutor.executeActions(mConfigure.getActions());
    }

    public void stop() {
        if (!isStart) {
            return;
        }
        isStart = false;
        actionExecutor.setCanExecute(false);
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
