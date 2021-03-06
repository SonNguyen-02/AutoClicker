package com.mct.auto_clicker.overlays.mainmenu.executor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.domain.Action;
import com.mct.auto_clicker.utils.ScreenMetrics;

public class ActionHandle implements View.OnTouchListener {

    private final Action action;
    /**
     * <pre>
     * view1 -> never null
     *      | action is click -> view of click
     *      | action is swipe -> view of swipe from
     *      | action is zoom  -> view of zoom 1
     *
     * view2 -> null if action is click
     *      | action is click -> null
     *      | action is swipe -> view of swipe to
     *      | action is zoom  -> view of zoom 2
     *
     * divider -> null if action is click
     * </pre>
     */
    private View view1, view2;

    private WindowManager.LayoutParams paramsView1, paramsView2, paramsDivider;

    private ActionDivider divider;

    // index in text view
    private int index;

    private static int ACTION_BTN_SIZE;
    private static float ALPHA;

    private final OnViewChangeListener onViewChangeListener;

    private final OnClickActionListener onClickActionListener;

    private final RequestScreenMetrics mRequestScreenMetrics;

    public ActionHandle(Context context, Action action, int index, OnViewChangeListener onViewChangeListener, OnClickActionListener onClickActionListener, RequestScreenMetrics requestScreenMetrics) {
        this.action = action;
        this.index = index;
        this.onViewChangeListener = onViewChangeListener;
        this.onClickActionListener = onClickActionListener;
        this.mRequestScreenMetrics = requestScreenMetrics;
        init(context);
    }

    /**
     * <pre>
     * k thay ?????i khi user config. Ch??? khi t???t menu ??i b???t l???i th?? m???i ?????i.
     * only init in {@MainMenu}</pre>
     */
    public static void setActionBtnSize(int actionBtnSize) {
        ACTION_BTN_SIZE = actionBtnSize;
    }

    public static void setAlpha(float alpha) {
        ALPHA = alpha;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        changeIndex();
    }

    public Action getAction() {
        return action;
    }

    public View getView1() {
        return view1;
    }

    public View getView2() {
        return view2;
    }

    public ActionDivider getDivider() {
        return divider;
    }

    public WindowManager.LayoutParams getParamsView1() {
        return paramsView1;
    }

    public WindowManager.LayoutParams getParamsView2() {
        return paramsView2;
    }

    public WindowManager.LayoutParams getParamsDivider() {
        return paramsDivider;
    }

    public void update() {
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (action instanceof Action.Click) {
            x1 = ((Action.Click) action).getX();
            y1 = ((Action.Click) action).getY();
        }
        if (action instanceof Action.Swipe) {
            x1 = ((Action.Swipe) action).getFromX();
            y1 = ((Action.Swipe) action).getFromY();
            x2 = ((Action.Swipe) action).getToX();
            y2 = ((Action.Swipe) action).getToY();

        }
        if (action instanceof Action.Zoom) {
            x1 = ((Action.Zoom) action).getX1();
            y1 = ((Action.Zoom) action).getY1();
            x2 = ((Action.Zoom) action).getX2();
            y2 = ((Action.Zoom) action).getY2();
        }
        if (action instanceof Action.GlobalAction) {
            x1 = ((Action.GlobalAction) action).getX();
            y1 = ((Action.GlobalAction) action).getY();
        }
        setPosition(x1, y1, x2, y2);
        int fitSize = ACTION_BTN_SIZE / 2;
        x1 -= fitSize;
        x2 -= fitSize;
        y1 -= fitSize;
        y2 -= fitSize;
        updateView(view1, paramsView1, x1, y1);
        updateView(view2, paramsView2, x2, y2);
    }

    private void updateView(View view, WindowManager.LayoutParams layoutParams, int x, int y) {
        if (view != null) {
            onViewChangeListener.onMove(view, layoutParams, x, y);
        }
    }

    private void init(Context context) {
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (action instanceof Action.Click) {
            x1 = ((Action.Click) action).getX();
            y1 = ((Action.Click) action).getY();
            view1 = createActionView(context, ViewType.CLICK);
        }
        if (action instanceof Action.Swipe) {
            Action.Swipe swipeAction = (Action.Swipe) action;
            x1 = swipeAction.getFromX();
            y1 = swipeAction.getFromY();
            x2 = swipeAction.getToX();
            y2 = swipeAction.getToY();
            divider = new ActionDivider(context);
            divider.set(x1, y1, x2, y2);
            view1 = createActionView(context, ViewType.SWIPE_FROM);
            view2 = createActionView(context, ViewType.SWIPE_TO);
        }
        if (action instanceof Action.Zoom) {
            Action.Zoom zoomAction = (Action.Zoom) action;
            x1 = zoomAction.getX1();
            y1 = zoomAction.getY1();
            x2 = zoomAction.getX2();
            y2 = zoomAction.getY2();
            divider = new ActionDivider(context);
            divider.set(x1, y1, x2, y2);
            if (zoomAction.getZoomType() == Action.Zoom.ZOOM_IN) {
                view1 = createActionView(context, ViewType.ZOOM_IN);
                view2 = createActionView(context, ViewType.ZOOM_IN);
            } else {
                view1 = createActionView(context, ViewType.ZOOM_OUT);
                view2 = createActionView(context, ViewType.ZOOM_OUT);
            }
        }
        if (action instanceof Action.GlobalAction) {
            x1 = ((Action.GlobalAction) action).getX();
            y1 = ((Action.GlobalAction) action).getY();
            ViewType viewType;
            switch (((Action.GlobalAction) action).getGlobalType()) {
                case OPEN_RECENT:
                    viewType = ViewType.GLOBAL_ACTION_OPEN_RECENT;
                    break;
                case GO_HOME:
                default:
                    viewType = ViewType.GLOBAL_ACTION_GO_HOME;
                    break;
                case GO_BACK:
                    viewType = ViewType.GLOBAL_ACTION_GO_BACK;
                    break;
                case OPEN_NOTIFICATIONS:
                    viewType = ViewType.GLOBAL_ACTION_OPEN_NOTIFICATIONS;
                    break;
            }
            view1 = createActionView(context, viewType);
        }
        changeIndex();
        int fitSize = ACTION_BTN_SIZE / 2;
        x1 -= fitSize;
        x2 -= fitSize;
        y1 -= fitSize;
        y2 -= fitSize;

        if (action instanceof Action.GlobalAction) {
            paramsView1 = getLayoutParam(ACTION_BTN_SIZE * 2, ACTION_BTN_SIZE, x1, y1);
        } else {
            paramsView1 = getLayoutParam(x1, y1);
        }

        view1.setAlpha(ALPHA);
        view1.setOnTouchListener(this);
        ((TextView) view1.findViewById(R.id.tv_action_index)).setTextSize(ACTION_BTN_SIZE / 5.5f);
        if (view2 != null) {
            paramsView2 = getLayoutParam(x2, y2);
            view2.setAlpha(ALPHA);
            view2.setOnTouchListener(this);
            ((TextView) view2.findViewById(R.id.tv_action_index)).setTextSize(ACTION_BTN_SIZE / 5.5f);
            paramsDivider = getLayoutParamDivider();
        }
    }

    private void changeIndex() {
        if (view1 != null)
            ((TextView) view1.findViewById(R.id.tv_action_index)).setText(String.valueOf(index));
        if (view2 != null)
            ((TextView) view2.findViewById(R.id.tv_action_index)).setText(String.valueOf(index));
    }

    public void setEnable(boolean enable) {
        setEnable(view1, paramsView1, enable);
        setEnable(view2, paramsView2, enable);
    }

    private void setEnable(View view, WindowManager.LayoutParams layoutParams, boolean enable) {
        if (view != null) {
            if (enable) {
                layoutParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            } else {
                layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            }
            onViewChangeListener.onChange(view, layoutParams);
        }
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParam(int x, int y) {
        return getLayoutParam(ACTION_BTN_SIZE, ACTION_BTN_SIZE, x, y);
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParam(int width, int height, int x, int y) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                width, height, x, y,
                ScreenMetrics.TYPE_COMPAT_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        return layoutParams;
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParamDivider() {
        int width, height;
        if (ScreenMetrics.isHasNavigationHeight()) {
            width = mRequestScreenMetrics.getScreenMetrics().getScreenSize().x;
            height = mRequestScreenMetrics.getScreenMetrics().getScreenSize().y;
        } else {
            width = WindowManager.LayoutParams.MATCH_PARENT;
            height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        return new WindowManager.LayoutParams(
                width, height, 0, 0, ScreenMetrics.TYPE_COMPAT_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);
    }

    /**
     * @param viewType action of view
     */
    @NonNull
    private View createActionView(Context context, @NonNull ViewType viewType) {
        int layout = action instanceof Action.GlobalAction ? R.layout.floating_action_global : R.layout.floating_action;
        View view = LayoutInflater.from(context).inflate(layout, null);
        ImageView imgAction = view.findViewById(R.id.iv_action);
        if(action instanceof Action.GlobalAction){
            imgAction.getLayoutParams().width = ACTION_BTN_SIZE;
            imgAction.setLayoutParams(imgAction.getLayoutParams());
        }
        switch (viewType) {
            case CLICK:
            case SWIPE_FROM:
                imgAction.setImageResource(R.drawable.ic_circle_target);
                break;
            case SWIPE_TO:
                imgAction.setImageResource(android.R.color.transparent);
                break;
            case ZOOM_IN:
                imgAction.setImageResource(R.drawable.ic_circle_zoom_in);
                break;
            case ZOOM_OUT:
                imgAction.setImageResource(R.drawable.ic_circle_zoom_out);
                break;
            case GLOBAL_ACTION_OPEN_RECENT:
                imgAction.setImageResource(R.drawable.ic_recent);
                break;
            case GLOBAL_ACTION_GO_HOME:
                imgAction.setImageResource(R.drawable.ic_home);
                break;
            case GLOBAL_ACTION_GO_BACK:
                imgAction.setImageResource(R.drawable.ic_go_back);
                break;
            case GLOBAL_ACTION_OPEN_NOTIFICATIONS:
                imgAction.setImageResource(R.drawable.ic_notification);
                break;
        }
        return view;
    }

    private Pair<Integer, Integer> moveInitialPosition;
    private Pair<Integer, Integer> moveInitialTouchPosition;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, @NonNull MotionEvent event) {
        WindowManager.LayoutParams layoutParams = view == view1 ? paramsView1 : paramsView2;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveInitialPosition = Pair.create(layoutParams.x, layoutParams.y);
                moveInitialTouchPosition = Pair.create((int) event.getRawX(), (int) event.getRawY());
                return true;
            case MotionEvent.ACTION_UP:
                if (moveInitialPosition.first == layoutParams.x && moveInitialPosition.second == layoutParams.y) {
                    // open dialog
                    onClickActionListener.onClick(this);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                int x = moveInitialPosition.first + (int) (event.getRawX() - moveInitialTouchPosition.first);
                int y = moveInitialPosition.second + (int) (event.getRawY() - moveInitialTouchPosition.second);
                onViewChangeListener.onMove(view, layoutParams, x, y);
                setPosition(view, layoutParams.x, layoutParams.y);
                return true;
        }
        return false;
    }

    private void setPosition(int x1, int y1, int x2, int y2) {
        if (divider != null) {
            if (ScreenMetrics.isHasNavigationHeight()) {
                paramsDivider.width = mRequestScreenMetrics.getScreenMetrics().getScreenSize().x;
                paramsDivider.height = mRequestScreenMetrics.getScreenMetrics().getScreenSize().y;
                onViewChangeListener.onChange(divider, paramsDivider);
            }
            divider.set(x1, y1, x2, y2);
        }
    }

    private void setPosition(View view, int x, int y) {
        int fitSize = ACTION_BTN_SIZE / 2;
        x += fitSize;
        y += fitSize;
        if (action instanceof Action.Click) {
            ((Action.Click) action).setX(x);
            ((Action.Click) action).setY(y);
        }
        if (action instanceof Action.GlobalAction) {
            ((Action.GlobalAction) action).setX(x);
            ((Action.GlobalAction) action).setY(y);
        }
        if (divider == null) {
            return;
        }
        int x2 = 0, y2 = 0;
        if (action instanceof Action.Swipe) {
            Action.Swipe swipeAction = (Action.Swipe) action;
            if (view1 == view) {
                swipeAction.setFromX(x);
                swipeAction.setFromY(y);
                x2 = swipeAction.getToX();
                y2 = swipeAction.getToY();
            } else {
                swipeAction.setToX(x);
                swipeAction.setToY(y);
                x2 = swipeAction.getFromX();
                y2 = swipeAction.getFromY();
            }
        }
        if (action instanceof Action.Zoom) {
            Action.Zoom zoomAction = (Action.Zoom) action;
            if (view1 == view) {
                zoomAction.setX1(x);
                zoomAction.setY1(y);
                x2 = zoomAction.getX2();
                y2 = zoomAction.getY2();
            } else {
                zoomAction.setX2(x);
                zoomAction.setY2(y);
                x2 = zoomAction.getX1();
                y2 = zoomAction.getY1();
            }
        }
        divider.set(x, y, x2, y2);
    }

    public interface OnViewChangeListener {
        void onMove(View view, WindowManager.LayoutParams layoutParams, int x, int y);

        void onChange(View view, WindowManager.LayoutParams layoutParams);
    }

    public interface OnClickActionListener {
        void onClick(ActionHandle actionHandle);
    }

    public interface RequestScreenMetrics {
        ScreenMetrics getScreenMetrics();
    }

    private enum ViewType {
        CLICK,
        SWIPE_FROM,
        SWIPE_TO,
        ZOOM_IN,
        ZOOM_OUT,
        GLOBAL_ACTION_OPEN_RECENT,
        GLOBAL_ACTION_GO_HOME,
        GLOBAL_ACTION_GO_BACK,
        GLOBAL_ACTION_OPEN_NOTIFICATIONS,
    }

}
