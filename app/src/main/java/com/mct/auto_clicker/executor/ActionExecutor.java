package com.mct.auto_clicker.executor;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.presenter.SettingSharedPreference;
import com.mct.auto_clicker.utils.ScreenMetrics;

import java.util.List;
import java.util.Random;

public class ActionExecutor {

    private final ScreenMetrics screenMetrics;
    /**
     * Handler on the main thread.
     */
    private final Handler mainThreadHandler;
    /**
     * Handler on the worker thread.
     */
    private final Handler workerThreadHandler;

    private int randomActionDelayTime;

    private int randomLocation;

    private final SettingSharedPreference settingSharedPreference;

    private GestureExecutionListener mGestureExecutionListener;

    private OnExecutionComplete mOnExecutionComplete;

    private List<Action> actionsLeft = null;

    private final Runnable execAction = () -> executeActions(actionsLeft);

    public interface GestureExecutionListener {
        void onExecution(GestureDescription gesture);
    }

    public interface OnExecutionComplete {
        void onComplete();
    }

    public ActionExecutor(Context mContext, ScreenMetrics screenMetrics) {
        this.screenMetrics = screenMetrics;
        mainThreadHandler = new Handler(Looper.getMainLooper());
        workerThreadHandler = new Handler(Looper.myLooper());
        settingSharedPreference = SettingSharedPreference.getInstance(mContext);
        initRandom();
    }

    public void setGestureExecutionListener(GestureExecutionListener mGestureExecutionListener) {
        this.mGestureExecutionListener = mGestureExecutionListener;
    }

    public void setOnExecutionComplete(OnExecutionComplete mOnExecutionComplete) {
        this.mOnExecutionComplete = mOnExecutionComplete;
    }

    public void initRandom() {
        randomActionDelayTime = settingSharedPreference.getIncreaseRandomActionDelayTime();
        randomLocation = settingSharedPreference.getRandomLocation();
    }

    @WorkerThread
    public void executeActions(@NonNull List<Action> actions) {
        if (actions.isEmpty()) {
            mainThreadHandler.post(() -> {
                if (mOnExecutionComplete != null) {
                    mOnExecutionComplete.onComplete();
                }
            });
            return;
        }
        Action action = actions.get(0);
        // make sure action duration min is 10
        int randWaitTime = 10;
        if (action instanceof Action.Click) {
            if (((Action.Click) action).isAntiDetection()) {
                if (randomActionDelayTime > 0) {
                    randWaitTime += new Random().nextInt(randomActionDelayTime);
                    Log.e("ddd", "executeActions: " + randWaitTime);
                }
            }
            executeClick((Action.Click) action);
        }
        if (action instanceof Action.Swipe) {
            executeSwipe((Action.Swipe) action);
        }
        if (action instanceof Action.Zoom) {
            executeZoom((Action.Zoom) action);
        }

        actionsLeft = actions.subList(1, actions.size());

        workerThreadHandler.postDelayed(execAction, action.getTotalDuration() + randWaitTime);

    }

    public void stopExecute() {
        actionsLeft = null;
        workerThreadHandler.removeCallbacks(execAction);
        removeListener();
    }

    public void removeListener() {
        mGestureExecutionListener = null;
        mOnExecutionComplete = null;
    }

    @WorkerThread
    private void executeClick(@NonNull Action.Click click) {
        Path path = new Path();
        if (click.isAntiDetection() && randomLocation > 0) {
            int x = click.getX() + (int) (Math.random() * (randomLocation + 1)) - (randomLocation / 2);
            int y = click.getY() + (int) (Math.random() * (randomLocation + 1)) - (randomLocation / 2);
            if (x < 0 || x > screenMetrics.getScreenSize().x) {
                x = click.getX();
            }
            if (y < 0 || y > screenMetrics.getScreenSize().y) {
                y = click.getY();
            }
            path.moveTo(x, y);
            Log.e("ddd", "executeClick: " + x + "|" + y);
        } else {
            path.moveTo(click.getX(), click.getY());
        }
        GestureDescription gesture = createGestureDescription(new GestureDescription.StrokeDescription(path, 0, click.getActionDuration()));
        execGesture(gesture);
    }

    @WorkerThread
    private void executeSwipe(@NonNull Action.Swipe swipe) {
        Path path = new Path();
        path.moveTo(swipe.getFromX(), swipe.getFromY());
        path.lineTo(swipe.getToX(), swipe.getToY());
        GestureDescription gesture = createGestureDescription(new GestureDescription.StrokeDescription(path, 0, swipe.getActionDuration()));
        execGesture(gesture);
    }

    @WorkerThread
    private void executeZoom(@NonNull Action.Zoom zoom) {
        Path path1 = new Path();
        Path path2 = new Path();

        float[] midPoint = zoom.getMidPoint();

        if (zoom.getZoomType() == Action.Zoom.ZOOM_IN) {
            path1.moveTo(midPoint[0], midPoint[1]);
            path1.lineTo(zoom.getX1(), zoom.getY1());
            path2.moveTo(midPoint[0], midPoint[1]);
            path2.lineTo(zoom.getX2(), zoom.getY2());
        } else {
            path1.moveTo(zoom.getX1(), zoom.getY1());
            path1.lineTo(midPoint[0], midPoint[1]);
            path2.moveTo(zoom.getX2(), zoom.getY2());
            path2.lineTo(midPoint[0], midPoint[1]);
        }
        GestureDescription gesture = createGestureDescription(
                new GestureDescription.StrokeDescription(path1, 0, zoom.getActionDuration()),
                new GestureDescription.StrokeDescription(path2, 0, zoom.getActionDuration())
        );
        execGesture(gesture);
    }

    private void execGesture(GestureDescription gestureDescription) {
        mainThreadHandler.post(() -> {
            if (actionsLeft != null && mGestureExecutionListener != null) {
                mGestureExecutionListener.onExecution(gestureDescription);
            }
        });
    }

    private GestureDescription createGestureDescription(@NonNull GestureDescription.StrokeDescription... strokes) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        return builder.build();
    }

}
