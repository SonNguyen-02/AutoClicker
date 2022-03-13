package com.mct.auto_clicker.baseui.overlays;

import static android.view.MotionEvent.ACTION_DOWN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;
import com.mct.auto_clicker.utils.ScreenMetrics;

public abstract class OverlayMenuController extends OverlayController {

    protected Context context;

    /**
     * Name of the preference file.
     */
    private static final String PREFERENCE_NAME = "OverlayMenuController";
    /**
     * Preference key referring to the landscape X position of the menu during the last call to [dismiss].
     */
    private static final String PREFERENCE_MENU_X_LANDSCAPE_KEY = "Menu_X_Landscape_Position";
    /**
     * Preference key referring to the landscape Y position of the menu during the last call to [dismiss].
     */
    private static final String PREFERENCE_MENU_Y_LANDSCAPE_KEY = "Menu_Y_Landscape_Position";
    /**
     * Preference key referring to the portrait X position of the menu during the last call to [dismiss].
     */
    private static final String PREFERENCE_MENU_X_PORTRAIT_KEY = "Menu_X_Portrait_Position";
    /**
     * Preference key referring to the portrait Y position of the menu during the last call to [dismiss].
     */
    private static final String PREFERENCE_MENU_Y_PORTRAIT_KEY = "Menu_Y_Portrait_Position";

    /**
     * Monitors the state of the screen.
     */
    private final ScreenMetrics screenMetrics;

    /**
     * The layout parameters of the menu layout.
     */
    private final WindowManager.LayoutParams menuLayoutParams;

    /**
     * The shared preference storing the position of the menu  the lasin order to save/restoret user position.
     */
    private final SharedPreferences sharedPreferences;
    /**
     * The Android window manager. Used to add/remove the overlay menu and view.
     */
    private final WindowManager windowManager;
    /**
     * Value of the alpha for a disabled item view in the menu.
     */
    private final float disabledItemAlpha;
    /**
     * The root view of the menu overlay. Retrieved from [onCreateMenu] implementation.
     */
    private ViewGroup menuLayout = null;

    protected final int PADDING_SCREEN = 8;

    /**
     * Listener upon the screen orientation changes.
     */
    private final ScreenMetrics.OrientationListener orientationListener;

    @SuppressLint("ResourceType")
    public OverlayMenuController(Context context) {
        this.context = context;
        screenMetrics = new ScreenMetrics(context);
        menuLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                ScreenMetrics.TYPE_COMPAT_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        disabledItemAlpha = context.getResources().getFraction(R.dimen.alpha_menu_item_disabled, 1, 1);
        orientationListener = this::onOrientationChanged;
    }

    /**
     * Creates the root view of the menu overlay.
     *
     * @param layoutInflater the Android layout inflater.
     * @return the menu root view. It MUST contains a view group within a depth of 2 that contains all menu items in
     * order for move and hide to work as expected.
     */
    @NonNull
    protected abstract ViewGroup onCreateMenu(LayoutInflater layoutInflater);

    @SuppressLint("ClickableViewAccessibility")
    @CallSuper
    @Override
    protected void onCreate() {
        // First, call implementation methods to check what we should display
        menuLayout = onCreateMenu(context.getSystemService(LayoutInflater.class));
        // Restore the last menu position, if any.
        menuLayoutParams.gravity = Gravity.TOP | Gravity.START;
        loadMenuPosition(screenMetrics.getOrientation());
    }


    @CallSuper
    public void onStart() {
        screenMetrics.registerOrientationListener(orientationListener);
        windowManager.addView(menuLayout, menuLayoutParams);
    }

    @CallSuper
    public void onStop() {
        windowManager.removeView(menuLayout);
        screenMetrics.unregisterOrientationListener();
    }

    @CallSuper
    public void onDismissed() {
        // Save last user position
        saveMenuPosition(screenMetrics.getOrientation(), false);
    }

    public ViewGroup getMenuLayout() {
        return menuLayout;
    }

    protected WindowManager getWindowManager() {
        return windowManager;
    }

    protected ScreenMetrics getScreenMetrics() {
        return screenMetrics;
    }

    /**
     * Set the enabled state of a menu item.
     *
     * @param view    the view identifier of the menu item to change the state of.
     * @param enabled true to enable the view, false to disable it.
     */
    protected void setMenuItemViewEnabled(@NonNull View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setClickable(enabled);
        view.setFocusable(enabled);
        view.setAlpha(enabled ? 1.0f : disabledItemAlpha);
    }

    private Pair<Integer, Integer> moveInitialPosition;
    private Pair<Integer, Integer> moveInitialTouchPosition;
    private boolean stageMenu = true;
    private int yExpand, yCollapse;

    /**
     * Called when the user touch the menu item except for[MENU_ITEM_PLAY_PAUSE].
     * Handle the press and move on this button in order to drag and drop the overlay menu on the screen.
     *
     * @param event the touch event occurring on the menu item.
     * @return true if the event is handled, false if not.
     */
    protected boolean onItemTouched(@NonNull MotionEvent event, MenuItemType item) {
        switch (event.getAction()) {
            case ACTION_DOWN:
                moveInitialPosition = Pair.create(menuLayoutParams.x, menuLayoutParams.y);
                moveInitialTouchPosition = Pair.create((int) event.getRawX(), (int) event.getRawY());
                if (stageMenu) {
                    if (yExpand != menuLayout.getHeight()) yExpand = menuLayout.getHeight();
                } else {
                    if (yCollapse != menuLayout.getHeight()) yCollapse = menuLayout.getHeight();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (moveInitialPosition.first == menuLayoutParams.x && moveInitialPosition.second == menuLayoutParams.y) {
                    if (item == MenuItemType.MENU_ITEM_EXPAND_COLLAPSE) {
                        stageMenu = !stageMenu;
                        onStageMenuChange(stageMenu);
                        resizeMenuLayout();
                    } else {
                        onMenuItemClicked(item);
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                setMenuLayoutPosition(
                        moveInitialPosition.first + (int) (event.getRawX() - moveInitialTouchPosition.first),
                        moveInitialPosition.second + (int) (event.getRawY() - moveInitialTouchPosition.second));
                windowManager.updateViewLayout(menuLayout, menuLayoutParams);
                return true;
            default:
                return false;
        }
    }

    protected void onMenuItemClicked(MenuItemType item) {
    }

    /**
     * change stage of menu
     *
     * @param stageMenu true => menu is expand | false => menu is collapse
     */
    protected void onStageMenuChange(boolean stageMenu) {
        Log.e("ddd", "onStageMenuChange: " + (stageMenu ? "expand" : "collapse"));
    }

    private void resizeMenuLayout() {
        Point displaySize = screenMetrics.getScreenSize();
        if (stageMenu) {
            if (yExpand + menuLayoutParams.y > displaySize.y - PADDING_SCREEN) {
                menuLayoutParams.y = displaySize.y - yExpand - PADDING_SCREEN;
                windowManager.updateViewLayout(menuLayout, menuLayoutParams);
            }
        } else {
            if (yCollapse + menuLayoutParams.y > displaySize.y - PADDING_SCREEN) {
                menuLayoutParams.y = displaySize.y - yCollapse - PADDING_SCREEN;
                windowManager.updateViewLayout(menuLayout, menuLayoutParams);
            }
        }
    }

    protected void setLayoutPosition(View view, @NonNull WindowManager.LayoutParams layoutParams, int x, int y) {
        Point displaySize = screenMetrics.getScreenSize();
        layoutParams.x = x < 0 ? PADDING_SCREEN : Math.min(x, displaySize.x - view.getWidth() - PADDING_SCREEN);
        layoutParams.y = y < 0 ? PADDING_SCREEN : Math.min(y, displaySize.y - view.getHeight() - PADDING_SCREEN);
    }

    /**
     * Safe setter for the position of the overlay menu ensuring it will not be displayed outside the screen.
     *
     * @param x the horizontal position.
     * @param y the vertical position.
     */
    private void setMenuLayoutPosition(int x, int y) {
        setLayoutPosition(menuLayout, menuLayoutParams, x, y);
    }

    /**
     * Handles the screen orientation changes.
     * It will save the menu position for the previous orientation and load and apply the correct position
     * for the new orientation.
     */
    protected void onOrientationChanged() {
        saveMenuPosition(screenMetrics.getOrientation() == Configuration.ORIENTATION_LANDSCAPE ? Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE, true);
        loadMenuPosition(screenMetrics.getOrientation());
        windowManager.updateViewLayout(menuLayout, menuLayoutParams);
    }

    /**
     * Load last user menu position for the current orientation, if any.
     *
     * @param orientation the orientation to load the position for.
     */
    private void loadMenuPosition(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setMenuLayoutPosition(
                    sharedPreferences.getInt(PREFERENCE_MENU_X_LANDSCAPE_KEY, 0),
                    sharedPreferences.getInt(PREFERENCE_MENU_Y_LANDSCAPE_KEY, 0)
            );
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setMenuLayoutPosition(
                    sharedPreferences.getInt(PREFERENCE_MENU_X_PORTRAIT_KEY, 0),
                    sharedPreferences.getInt(PREFERENCE_MENU_Y_PORTRAIT_KEY, 0)
            );
        }
    }

    /**
     * Save the last user menu position for the current orientation.
     *
     * @param orientation the orientation to save the position for.
     */
    private void saveMenuPosition(int orientation, boolean isOrientationChanged) {
        Point displaySize = screenMetrics.getScreenSize();
        int screenHeight = isOrientationChanged ? displaySize.x : displaySize.y;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sharedPreferences.edit()
                    .putInt(PREFERENCE_MENU_X_LANDSCAPE_KEY, menuLayoutParams.x)
                    .putInt(PREFERENCE_MENU_Y_LANDSCAPE_KEY, menuLayoutParams.y + yExpand > screenHeight
                            ? screenHeight - yExpand : menuLayoutParams.y)
                    .apply();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            sharedPreferences.edit()
                    .putInt(PREFERENCE_MENU_X_PORTRAIT_KEY, menuLayoutParams.x)
                    .putInt(PREFERENCE_MENU_Y_PORTRAIT_KEY, menuLayoutParams.y + yExpand > screenHeight
                            ? screenHeight - yExpand : menuLayoutParams.y)
                    .apply();
        }
    }

}
