package com.mct.auto_clicker.executor;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.presenter.MySharedPreference;
import com.mct.auto_clicker.utils.ScreenUtils;

import java.util.List;
import java.util.Random;

public class ActionExecutor {

    /**
     * Handler on the main thread.
     */
    private final Handler mainThreadHandler;
    /**
     * Handler on the worker thread.
     */
    private final Handler workerThreadHandler;

    private GestureExecutionListener mGestureExecutionListener;

    private final OnExecutionComplete mOnExecutionComplete;

    public interface GestureExecutionListener {
        void onExecution(GestureDescription gesture);
    }

    public interface OnExecutionComplete {
        void onComplete();
    }

    public ActionExecutor(GestureExecutionListener mGestureExecutionListener, OnExecutionComplete mOnExecutionComplete) {
        this.mGestureExecutionListener = mGestureExecutionListener;
        this.mOnExecutionComplete = mOnExecutionComplete;
        mainThreadHandler = new Handler(Looper.getMainLooper());
        workerThreadHandler = new Handler(Looper.myLooper());
    }

    public void removeListener() {
        mGestureExecutionListener = null;
    }

    @WorkerThread
    public void executeActions(@NonNull List<Action> actions, Context mContext) {
        if (mGestureExecutionListener == null) {
            return;
        }
        if (actions.isEmpty()) {
            onComplete();
            return;
        }
        Action action = actions.get(0);
        int randWaitTime = 0;
        if (action instanceof Action.Click) {
            if (((Action.Click) action).isAntiDetection()) {
                if (MySharedPreference.getInstance(mContext).getIncreaseRandomWaitTime() > 0) {
                    Random random = new Random(System.currentTimeMillis());
                    randWaitTime = random.nextInt(MySharedPreference.getInstance(mContext).getIncreaseRandomWaitTime());
                    Log.e("ddd", "executeActions: " + randWaitTime);
                }
            }
            executeClick((Action.Click) action, mContext);
        }
        if (action instanceof Action.Swipe) {
            executeSwipe((Action.Swipe) action);
        }
        if (action instanceof Action.Zoom) {
            executeZoom((Action.Zoom) action);
        }

        List<Action> actionsLeft = actions.subList(1, actions.size());

        workerThreadHandler.postDelayed(() -> executeActions(actionsLeft, mContext), action.getTotalDuration() + randWaitTime);

    }

    @WorkerThread
    private void executeClick(@NonNull Action.Click click, Context mContext) {
        Path path = new Path();
        if (click.isAntiDetection() && MySharedPreference.getInstance(mContext).getRandomLocation() > 0) {
            int randLocation = MySharedPreference.getInstance(mContext).getRandomLocation();
            int x = click.getX() + (int) (Math.random() * (randLocation + 1)) - (randLocation / 2);
            int y = click.getY() + (int) (Math.random() * (randLocation + 1)) - (randLocation / 2);
            Point screenSize = ScreenUtils.getScreenSize(mContext);
            if (x < 0 || x > screenSize.x) {
                x = click.getX();
            }
            if (y < 0 || y > screenSize.y) {
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

    private void onComplete() {
        mainThreadHandler.post(() -> {
            if (mOnExecutionComplete != null) {
                mOnExecutionComplete.onComplete();
            }
        });
    }

    private void execGesture(GestureDescription gestureDescription) {
        mainThreadHandler.post(() -> {
            if (mGestureExecutionListener != null) {
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
