package com.mct.auto_clicker.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ScreenMetrics {

    private final Context context;

    public static final int TYPE_COMPAT_OVERLAY;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            TYPE_COMPAT_OVERLAY = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            TYPE_COMPAT_OVERLAY = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
    }

    /**
     * The display to get the value from. It will always be the first one available.
     */
    private final Display display;

    /**
     * The listener upon orientation changes.
     */
    private OrientationListener orientationListener;

    public interface OrientationListener {
        void onChange();
    }

    private static Integer navigationHeight;
    /**
     * The rotation of the display.
     */
    private int rotation;

    /**
     * The orientation of the display.
     */
    private int orientation;

    /**
     * The screen size.
     */
    private Point screenSize;

    /**
     * Listen to the configuration changes and calls [orientationListener] when needed.
     */
    private final BroadcastReceiver configChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateScreenMetrics();
        }
    };

    public ScreenMetrics(@NonNull Context context) {
        this.context = context;
        display = context.getSystemService(WindowManager.class).getDefaultDisplay();
//        display = context.getSystemService(DisplayManager.class).getDisplay(0);
        orientation = computeOrientation();
        screenSize = computeScreenSize();
    }

    public int getOrientation() {
        return orientation;
    }

    public Point getScreenSize() {
        return screenSize;
    }

    /**
     * Register a new orientation listener.
     * If a previous listener was registered, the new one will replace it.
     *
     * @param listener the listener to be registered.
     */
    public void registerOrientationListener(OrientationListener listener) {
        if (listener == orientationListener) {
            return;
        }

        unregisterOrientationListener();
        orientationListener = listener;
        context.registerReceiver(configChangedReceiver, new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED));
    }

    /**
     * Unregister a previously registered listener.
     */
    public void unregisterOrientationListener() {
        if (orientationListener != null) {
            context.unregisterReceiver(configChangedReceiver);
            orientationListener = null;
        }
    }

    /**
     * Update orientation and screen size, if needed. Should be called after a configuration change.
     */
    private void updateScreenMetrics() {
        int newOrientation = computeOrientation();
        if (orientation != newOrientation) {
            orientation = newOrientation;
            screenSize = computeScreenSize();
            if (orientationListener != null) {
                Log.e("ddd", "updateScreenMetrics: " + screenSize.x + "|" + screenSize.y);
                orientationListener.onChange();
            }
        }
    }

    private Point computeScreenSize() {
        Point size;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowMetrics metrics = context.getSystemService(WindowManager.class).getCurrentWindowMetrics();
            // Gets all excluding insets
            final WindowInsets windowInsets = metrics.getWindowInsets();
            //| WindowInsets.Type.displayCutout()
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars());
            Log.e("ddd", "computeScreenSize: inset " + insets);
            // let remove bottom nav if device has
            int insetsWidth = 0, insetsHeight = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    insetsWidth = insets.right + insets.left;
                    insetsHeight = insets.bottom;
                    initNavigationHeight(insetsHeight);
                    break;
                case Surface.ROTATION_180:
                    insetsWidth = insets.right + insets.left;
                    insetsHeight = insets.top;
                    initNavigationHeight(insetsHeight);
                    break;
                case Surface.ROTATION_90:
                    insetsWidth = insets.right;
                    insetsHeight = insets.top + insets.bottom;
                    initNavigationHeight(insetsWidth);
                    break;
                case Surface.ROTATION_270:
                    insetsWidth = insets.left;
                    insetsHeight = insets.top + insets.bottom;
                    initNavigationHeight(insetsWidth);
                    break;
            }
            // size that Display#getSize reports
            final Rect bounds = metrics.getBounds();
            size = new Point(bounds.width() - insetsWidth, bounds.height() - insetsHeight);
        } else {
            size = new Point();
            display.getSize(size);
//            display.getRealSize(size);
        }

        // Some phone can be messy with the size change with the orientation. Correct it here.
        if (orientation == Configuration.ORIENTATION_PORTRAIT && size.x > size.y ||
                orientation == Configuration.ORIENTATION_LANDSCAPE && size.x < size.y) {
            size = new Point(size.y, size.x);
        }
        return size;
    }

    /**
     * @return the orientation of the screen.
     */
    private int computeOrientation() {
        rotation = Objects.requireNonNull(display).getRotation();
        Log.e("ddd", "computeOrientation: " + rotation);
        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                return Configuration.ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                return Configuration.ORIENTATION_LANDSCAPE;
            default:
                return Configuration.ORIENTATION_UNDEFINED;
        }
    }

    public static boolean isHasNavigationHeight() {
        return navigationHeight != null && navigationHeight > 0;
    }

    private static void initNavigationHeight(int value) {
        if (navigationHeight == null) {
            navigationHeight = value;
        }
    }
}
