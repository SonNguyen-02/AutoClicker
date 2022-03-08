package com.mct.auto_clicker;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.mct.auto_clicker.activities.AutoClickerActivity;
import com.mct.auto_clicker.baseui.overlays.OverlayController;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionDetector;
import com.mct.auto_clicker.overlays.mainmenu.MainMenu;

public class AutoClickerService extends AccessibilityService {

    private static final int NOTIFICATION_ID = 15;

    private static final String NOTIFICATION_CHANNEL_ID = "AutoClickerService";

    private static LocalService LOCAL_SERVICE_INSTANCE;

    private OverlayController rootOverlayController;

    private OnLocalServiceChangeListener listener;

    private Boolean isStarted = false;

    public interface OnLocalServiceChangeListener {
        void onChange();
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

        public void setOnStopListener(OnLocalServiceChangeListener onLocalServiceChangeListener) {
            if (listener != null) {
                listener.onChange();
                listener = null;
            }
            listener = onLocalServiceChangeListener;
        }

        public void start(Context context, Configure configure) {
            if (isStarted) {
                return;
            }
            isStarted = true;
            if (listener != null) {
                listener.onChange();
            }
            startForeground(NOTIFICATION_ID, createNotification(configure.getName()));
            ActionDetector actionDetector = new ActionDetector(gesture -> dispatchGesture(gesture, null, null));
            rootOverlayController = new MainMenu(context, configure, actionDetector);
            rootOverlayController.create(this::stop);
        }

        public void loadConfigure(Configure configure) {
            ((MainMenu) rootOverlayController).loadNewConfigure(configure);
        }

        public void stop() {
            if (!isStarted) {
                return;
            }
            isStarted = false;
            stopForeground(true);
            if (rootOverlayController != null) {
                rootOverlayController.dismiss();
                rootOverlayController = null;
            }
            if (listener != null) {
                listener.onChange();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("ddd", "onUnbind");
        isStarted = false;
        if (LOCAL_SERVICE_INSTANCE != null) {
            LOCAL_SERVICE_INSTANCE.stop();
            LOCAL_SERVICE_INSTANCE = null;
        }
        return super.onUnbind(intent);
    }

    @NonNull
    private Notification createNotification(String configureName) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent intent = new Intent(this, AutoClickerActivity.class);
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title, configureName))
                .setContentText(getString(R.string.notification_message))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                .setSmallIcon(R.drawable.ic_click_duration)
                .build();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    public void onInterrupt() {
    }
}
