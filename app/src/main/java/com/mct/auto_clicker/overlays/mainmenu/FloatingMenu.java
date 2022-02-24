package com.mct.auto_clicker.overlays.mainmenu;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.Repository;
import com.mct.auto_clicker.database.domain.Configure;

public class FloatingMenu extends Service implements View.OnClickListener {
    private WindowManager mWindowManager;
    private View myFloatingView;
    private AutoClickerService.LocalService mLocalService;

    private ImageButton btnStartStop;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AutoClickerService.getLocalService(localService -> mLocalService = localService);
        //getting the widget layout from xml using layout inflater
        myFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_menu, null);

        int layout_params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_params = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_params = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //setting the layout parameters
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layout_params,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(myFloatingView, params);

        //adding an touchlistener to make drag movement of the floating widget
        myFloatingView.findViewById(R.id.btn_move).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TOUCH", "THIS IS TOUCHED");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //this code is helping the widget to move around the screen with fingers
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(myFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        btnStartStop = myFloatingView.findViewById(R.id.btn_start_stop);

        // dùng onclick sẽ không stop được khi click quá nhanh
        btnStartStop.setOnTouchListener(new View.OnTouchListener() {

            boolean checkAction = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mLocalService.isStart()) {
                        checkAction = true;
                        mLocalService.stop();
                        btnStartStop.setImageResource(R.drawable.ic_start);
                    } else {
                        checkAction = false;
                    }
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!checkAction && !mLocalService.isStart()) {
                        Configure configure = Repository.getInstance(getApplicationContext()).getConfigure(1L);
                        mLocalService.init(configure, () -> btnStartStop.setImageResource(R.drawable.ic_start));
                        btnStartStop.setImageResource(R.drawable.ic_pause);
                    }
                    return true;
                }
                return false;
            }
        });

        ImageButton btnExists = myFloatingView.findViewById(R.id.btn_exists);
        btnExists.setOnClickListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myFloatingView != null) mWindowManager.removeView(myFloatingView);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.btn_start_stop:
                break;
            case R.id.btn_exists:
                if (mLocalService != null) {
                    mLocalService.stop();
                }
                stopSelf();
        }
    }

}