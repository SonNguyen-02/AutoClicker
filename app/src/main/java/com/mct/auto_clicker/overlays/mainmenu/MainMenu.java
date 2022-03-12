package com.mct.auto_clicker.overlays.mainmenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.MenuItemAdapter;
import com.mct.auto_clicker.baseui.overlays.OverlayMenuController;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.overlays.dialog.SettingActionDialog;
import com.mct.auto_clicker.overlays.dialog.SettingConfigureDialog;
import com.mct.auto_clicker.overlays.dialog.WarningExistsDialog;
import com.mct.auto_clicker.overlays.mainmenu.executor.ActionDivider;
import com.mct.auto_clicker.overlays.mainmenu.executor.ActionManager;
import com.mct.auto_clicker.overlays.mainmenu.executor.ActionExecutor;
import com.mct.auto_clicker.overlays.mainmenu.executor.ActionHandle;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends OverlayMenuController implements ActionHandle.OnViewChangeListener, MenuItemAdapter.MenuItemListener {

    private static final int DEFAULT_ACTION_SPACE = 250;
    private Configure configure;
    private ActionManager actionManager;

    private final SettingSharedPreference settingSharedPreference;

    private final List<ActionHandle> listActionHandle;

    private boolean isShowing = true;

    private MenuItemAdapter menuItemAdapter;

    public MainMenu(Context context, Configure configure, @NonNull ActionManager actionManager) {
        super(context);
        this.configure = configure;
        this.actionManager = actionManager;
        actionManager.init(new ActionExecutor(context, getScreenMetrics()));
        listActionHandle = new ArrayList<>();
        settingSharedPreference = SettingSharedPreference.getInstance(context);

        int buttonActionSize = MenuPreference.getInstance(context).getButtonActionSize();
        ActionHandle.setActionBtnSize(buttonActionSize);
        ActionDivider.setStrokeWidth(buttonActionSize);
        int alpha = MenuPreference.getInstance(context).getButtonActionAlpha();
        ActionHandle.setAlpha(alpha / 100f);
        ActionDivider.setAlpha(alpha);
    }

    @NonNull
    @Override
    protected ViewGroup onCreateMenu(@NonNull LayoutInflater layoutInflater) {
        return (ViewGroup) layoutInflater.inflate(R.layout.floating_menu, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        initMenu();
        initView();
    }

    public void loadNewConfigure(@NonNull Configure configure) {
        if (actionManager.isStart()) {
            onConfigureStateChanged(true);
        }
        listActionHandle.forEach(actionHandle -> removeActionView(actionHandle, false));
        listActionHandle.clear();
        this.configure = configure;
        initView();
    }

    @Override
    protected void onMenuItemClicked(@NonNull MenuItemType item) {
        switch (item) {
            case MENU_ITEM_ADD_TOUCH:
                addDefaultActionTouch();
                break;
            case MENU_ITEM_ADD_SWIPE:
                addDefaultActionSwipe();
                break;
            case MENU_ITEM_ADD_ZOOM_IN:
                addDefaultActionZoom(Action.Zoom.ZOOM_IN);
                break;
            case MENU_ITEM_ADD_ZOOM_OUT:
                addDefaultActionZoom(Action.Zoom.ZOOM_OUT);
                break;
            case MENU_ITEM_REMOVE:
                removeLastActionView();
                break;
            case MENU_ITEM_SETTING:
                SettingConfigureDialog dialog = new SettingConfigureDialog(context, configure, this::loadNewConfigure);
                dialog.create(null);
                break;
            case MENU_ITEM_OPEN_RECENT:
                break;
            case MENU_ITEM_GO_HOME:
                break;
            case MENU_ITEM_GO_BACK:
                break;
            case MENU_ITEM_OPEN_NOTIFICATIONS:
                break;
            case MENU_ITEM_SHOW_HIDDEN:
                isShowing = !isShowing;
                menuItemAdapter.setShowing(isShowing);
                setActionsVisible(isShowing ? View.VISIBLE : View.GONE);
                break;
            case MENU_ITEM_EXISTS:
                onExistsClick();
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
                "Swipe #" + System.currentTimeMillis(),
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

    private void onExistsClick() {
        if (actionManager.isStart()) {
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
            return;
        }
        dismiss();
    }

    @Override
    protected void onStageMenuChange(boolean stageMenu) {
        super.onStageMenuChange(stageMenu);
        menuItemAdapter.initState(stageMenu, actionManager.isStart(), isShowing);
    }

    private void initMenu() {
        float alpha = MenuPreference.getInstance(context).getButtonMenuAlpha() / 100f;
        getMenuLayout().setAlpha(alpha);
        RecyclerView rcvMenu = getMenuLayout().findViewById(R.id.rcv_menu);
        menuItemAdapter = new MenuItemAdapter(context, this);
        rcvMenu.setAdapter(menuItemAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
                MenuPreference.getInstance(context).getMenuOrientation(), false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        rcvMenu.setLayoutManager(layoutManager);
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
        ActionHandle actionHandle = new ActionHandle(context, action, getNumericalOrderAction(), this, this::showSettingActionDialog, this::getScreenMetrics);
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
        if (actionManager.isStart()) {
            return;
        }
        new SettingActionDialog(context, actionHandle.getAction(), actionHandle.getIndex(), () -> {
            removeActionView(actionHandle, true);
            listActionHandle.remove(actionHandle);
            for (int i = actionHandle.getIndex() - 1; i < listActionHandle.size(); i++) {
                listActionHandle.get(i).setIndex(i + 1);
            }
        }).create(null);
    }

    private int getNumericalOrderAction() {
        return listActionHandle.size() + 1;
    }

    private boolean checkAction = false;

    private boolean onPlayTouched(@NonNull MotionEvent event) {
        if (listActionHandle.isEmpty()) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (actionManager.isStart()) {
                checkAction = true;
                onConfigureStateChanged(true);
            } else {
                checkAction = false;
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!checkAction && !actionManager.isStart()) {
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
            actionManager.stop();
        } else {
            actionManager.init(configure);
            actionManager.start();
            actionManager.setOnStopListener(() -> onConfigureStateChanged(true));
        }
        menuItemAdapter.setStart(actionManager.isStart());
    }

    @Override
    protected void onOrientationChanged() {
        super.onOrientationChanged();
        configure.setOrientation(getScreenMetrics().getOrientation(), getScreenMetrics().getScreenSize());
        listActionHandle.forEach(ActionHandle::update);
    }

    @Override
    public boolean onTouchItem(View view, @NonNull MotionEvent event, @NonNull MenuItemType item, int pos) {
        if (item == MenuItemType.MENU_ITEM_PLAY_PAUSE) {
            return onPlayTouched(event);
        }
        return onItemTouched(event, item);
    }

    @Override
    public void onItemStateChange(View view, boolean enable) {
        setMenuItemViewEnabled(view, enable);
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

    @Override
    public void onDismissed() {
        super.onDismissed();
        listActionHandle.forEach(actionHandle -> removeActionView(actionHandle, false));
        listActionHandle.clear();
        configure = null;
        if (actionManager != null) {
            actionManager.release();
            actionManager = null;
        }
    }

    public interface OnStopListener {
        void onStop();
    }
}
