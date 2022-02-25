package com.mct.auto_clicker.overlays.mainmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayMenuController;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionDetector;

public class MainMenu extends OverlayMenuController {

    private ActionDetector actionDetector;

    public MainMenu(Context context, Configure configure, @NonNull ActionDetector actionDetector) {
        super(context);
        this.actionDetector = actionDetector;
        this.actionDetector.init(configure);
    }

    @NonNull
    @Override
    protected ViewGroup onCreateMenu(@NonNull LayoutInflater layoutInflater) {
        return (ViewGroup) layoutInflater.inflate(R.layout.floating_menu, null);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onMenuItemClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                onConfigureStateChanged(false);
                view.setOnTouchListener(this::onPlayTouched);
                break;
            case R.id.btn_add_touch:
                break;
            case R.id.btn_add_swipe:
                break;
            case R.id.btn_add_zoom_out:
                break;
            case R.id.btn_add_zoom_in:
                break;
            case R.id.btn_remove:
                break;
            case R.id.btn_setting:
                break;
        }
    }

    private boolean checkAction = false;

    private boolean onPlayTouched(View view, @NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (actionDetector.isStart()) {
                checkAction = true;
                onConfigureStateChanged(true);
            } else {
                checkAction = false;
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!checkAction && !actionDetector.isStart()) {
                onConfigureStateChanged(false);
            }
            return true;
        }
        return false;
    }

    private void onConfigureStateChanged(boolean isStart) {
        if (actionDetector == null) return;
        if (isStart) {
            actionDetector.stop();
            setMenuItemViewImageResource(R.id.btn_play, R.drawable.ic_start);
            setMenuItemViewEnabled(true,
                    R.id.btn_add_touch,
                    R.id.btn_add_swipe,
                    R.id.btn_add_zoom_out,
                    R.id.btn_add_zoom_in,
                    R.id.btn_remove,
                    R.id.btn_setting
            );
        } else {
            actionDetector.start();
            actionDetector.setOnStopListener(() -> onConfigureStateChanged(true));
            setMenuItemViewImageResource(R.id.btn_play, R.drawable.ic_pause);
            setMenuItemViewEnabled(false,
                    R.id.btn_add_touch,
                    R.id.btn_add_swipe,
                    R.id.btn_add_zoom_out,
                    R.id.btn_add_zoom_in,
                    R.id.btn_remove,
                    R.id.btn_setting
            );
        }
    }

    @Override
    protected void onStageMenuChange(boolean stageMenu) {
        super.onStageMenuChange(stageMenu);
        if (stageMenu) {
            setMenuItemViewVisible(View.GONE, R.id.btn_exists);
            setMenuItemViewVisible(View.VISIBLE,
                    R.id.btn_add_touch,
                    R.id.btn_add_swipe,
                    R.id.btn_add_zoom_out,
                    R.id.btn_add_zoom_in,
                    R.id.btn_remove,
                    R.id.btn_setting
            );
        } else {
            setMenuItemViewVisible(View.VISIBLE, R.id.btn_exists);
            setMenuItemViewVisible(View.GONE,
                    R.id.btn_add_touch,
                    R.id.btn_add_swipe,
                    R.id.btn_add_zoom_out,
                    R.id.btn_add_zoom_in,
                    R.id.btn_remove,
                    R.id.btn_setting
            );
        }
    }

    @Override
    public void onDismissed() {
        super.onDismissed();
        if (actionDetector != null) {
            actionDetector.release();
            actionDetector = null;
        }
    }

    public interface OnStopListener {
        void onStop();
    }
}
