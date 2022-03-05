package com.mct.auto_clicker.overlays.mainmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayMenuController;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.executor.ActionDetector;
import com.mct.auto_clicker.overlays.dialog.SettingActionDialog;
import com.mct.auto_clicker.overlays.dialog.SettingConfigureDialog;
import com.mct.auto_clicker.overlays.dialog.WarningExistsDialog;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends OverlayMenuController implements ActionHandle.OnViewChangeListener {

    private static final int DEFAULT_ACTION_SPACE = 200;
    private Configure configure;
    private ActionDetector actionDetector;

    private final SettingSharedPreference settingSharedPreference;

    private final List<ActionHandle> listActionHandle;

    public MainMenu(Context context, Configure configure, @NonNull ActionDetector actionDetector) {
        super(context);
        this.configure = configure;
        this.actionDetector = actionDetector;
        listActionHandle = new ArrayList<>();
        settingSharedPreference = SettingSharedPreference.getInstance(context);
        ActionHandle.setActionBtnSize(settingSharedPreference.getButtonActionSize());
    }

    @NonNull
    @Override
    protected ViewGroup onCreateMenu(@NonNull LayoutInflater layoutInflater) {
        return (ViewGroup) layoutInflater.inflate(R.layout.floating_menu, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    public void loadNewConfigure(@NonNull Configure configure) {
        if (actionDetector.isStart()) {
            onConfigureStateChanged(true);
        }
        listActionHandle.forEach(actionHandle -> removeActionView(actionHandle, false));
        listActionHandle.clear();
        this.configure = configure;
        initView();
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
                addDefaultActionTouch();
                break;
            case R.id.btn_add_swipe:
                addDefaultActionSwipe();
                break;
            case R.id.btn_add_zoom_out:
                addDefaultActionZoom(Action.Zoom.ZOOM_OUT);
                break;
            case R.id.btn_add_zoom_in:
                addDefaultActionZoom(Action.Zoom.ZOOM_IN);
                break;
            case R.id.btn_remove:
                removeLastActionView();
                break;
            case R.id.btn_setting:
                SettingConfigureDialog dialog = new SettingConfigureDialog(context, configure, this::loadNewConfigure);
                dialog.create(null);
                break;
        }
    }


    private void addDefaultActionTouch() {
        Action action = new Action.Click(0L, configure.getId(),
                "Click #" + System.currentTimeMillis(),
                settingSharedPreference.getActionDelay(),
                settingSharedPreference.getClickExecTime(),
                getScreenMetrics().getScreenSize().x / 2,
                getScreenMetrics().getScreenSize().y / 2,
                true);
        addActionView(action, true);
    }

    private void addDefaultActionSwipe() {
        Action action = new Action.Swipe(0L, configure.getId(),
                "Swipe" + System.currentTimeMillis(),
                settingSharedPreference.getActionDelay(),
                settingSharedPreference.getSwipeExecTime(),
                getScreenMetrics().getScreenSize().x / 2 - DEFAULT_ACTION_SPACE,
                getScreenMetrics().getScreenSize().y / 2,
                getScreenMetrics().getScreenSize().x / 2 + DEFAULT_ACTION_SPACE,
                getScreenMetrics().getScreenSize().y / 2);
        addActionView(action, true);
    }

    private void addDefaultActionZoom(int zoomType) {
        Action action = new Action.Zoom(0L, configure.getId(),
                "Zoom #" + System.currentTimeMillis(),
                settingSharedPreference.getActionDelay(),
                settingSharedPreference.getZoomExecTime(),
                zoomType,
                getScreenMetrics().getScreenSize().x / 2,
                getScreenMetrics().getScreenSize().y / 2 - DEFAULT_ACTION_SPACE,
                getScreenMetrics().getScreenSize().x / 2,
                getScreenMetrics().getScreenSize().y / 2 + DEFAULT_ACTION_SPACE);
        addActionView(action, true);
    }

    private void initView() {
        if (configure == null) return;
        configure.setOrientation(getScreenMetrics().getOrientation(), getScreenMetrics().getScreenSize());
        List<Action> listAction = configure.getActions();
        if (listAction != null && !listAction.isEmpty()) {
            listAction.forEach(action -> addActionView(action, false));
        }
    }

    private void addActionView(@NonNull Action action, boolean isCreateNew) {
        if (isCreateNew) configure.getActions().add(action);
        ActionHandle actionHandle = new ActionHandle(context, action, getNumericalOrderAction(), this, this::showSettingActionDialog);
        listActionHandle.add(actionHandle);
        if (!(action instanceof Action.Click)) {
            getWindowManager().addView(actionHandle.getDivider(), actionHandle.getParamsDivider());
            getWindowManager().addView(actionHandle.getView1(), actionHandle.getParamsView1());
            getWindowManager().addView(actionHandle.getView2(), actionHandle.getParamsView2());
        } else {
            getWindowManager().addView(actionHandle.getView1(), actionHandle.getParamsView1());
        }
    }

    private void showSettingActionDialog(@NonNull ActionHandle actionHandle) {
        new SettingActionDialog(context, actionHandle.getAction(), actionHandle.getIndex(), () -> {
            removeActionView(actionHandle, true);
            listActionHandle.remove(actionHandle);
            for (int i = actionHandle.getIndex() - 1; i < listActionHandle.size(); i++) {
                listActionHandle.get(i).setIndex(i + 1);
            }
        }).create(null);
    }

    private void removeLastActionView() {
        if (!listActionHandle.isEmpty()) {
            removeActionView(listActionHandle.remove(listActionHandle.size() - 1), true);
        }
    }

    private void removeActionView(ActionHandle actionHandle, boolean removeRootAction) {
        if (actionHandle != null) {
            if (removeRootAction) {
                configure.getActions().remove(actionHandle.getAction());
            }
            if (actionHandle.getView1() != null) {
                getWindowManager().removeView(actionHandle.getView1());
            }
            if (actionHandle.getView2() != null) {
                getWindowManager().removeView(actionHandle.getView2());
                getWindowManager().removeView(actionHandle.getDivider());
            }
        }
    }


    private int getNumericalOrderAction() {
        return listActionHandle.size() + 1;
    }

    private boolean checkAction = false;

    private boolean onPlayTouched(View view, @NonNull MotionEvent event) {
        if (listActionHandle.isEmpty()) return false;
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
        if (listActionHandle.isEmpty()) return;
        listActionHandle.forEach(actionHandle -> actionHandle.setEnable(isStart));
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
            actionDetector.init(configure);
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
            setActionsVisible(View.VISIBLE);
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
            setActionsVisible(View.GONE);
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

    private void setActionsVisible(int visibility) {
        listActionHandle.forEach(actionHandle -> {
            if (actionHandle.getView1() != null)
                actionHandle.getView1().setVisibility(visibility);
            if (actionHandle.getView2() != null) {
                actionHandle.getView2().setVisibility(visibility);
                actionHandle.getDivider().setVisibility(visibility);
            }
        });
    }

    @Override
    protected void onOrientationChanged() {
        super.onOrientationChanged();
        configure.setOrientation(getScreenMetrics().getOrientation(), getScreenMetrics().getScreenSize());
        listActionHandle.forEach(ActionHandle::update);
    }

    @Override
    protected boolean onExistsTouched() {
        if (actionDetector.isStart()) {
            onConfigureStateChanged(true);
        }
        ConfigurePermissionPresenter configurePresenter = new ConfigurePermissionPresenter(context);
        if (configurePresenter.isConfigureChanged(configure)) {
            new WarningExistsDialog(context, (dialogInterface, i) -> {
                if (i == AlertDialog.BUTTON_POSITIVE) {
                    dismiss();
                }
                if (i == AlertDialog.BUTTON_NEUTRAL) {
                    configurePresenter.saveConfigure(configure);
                    dismiss();
                }
            }).create(null);
            return false;
        }
        return true;
    }

    @Override
    public void onDismissed() {
        super.onDismissed();
        listActionHandle.forEach(actionHandle -> removeActionView(actionHandle, false));
        listActionHandle.clear();
        configure = null;
        if (actionDetector != null) {
            actionDetector.release();
            actionDetector = null;
        }
    }

    @Override
    public void onMove(View view, WindowManager.LayoutParams layoutParams, int x, int y) {
        setLayoutPosition(view, layoutParams, x, y);
        onChange(view, layoutParams);
    }

    @Override
    public void onChange(View view, WindowManager.LayoutParams layoutParams) {
        getWindowManager().updateViewLayout(view, layoutParams);
    }

    public interface OnStopListener {
        void onStop();
    }
}
