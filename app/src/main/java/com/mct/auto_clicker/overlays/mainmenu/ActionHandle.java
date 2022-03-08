package com.mct.auto_clicker.overlays.mainmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
     * k thay đổi khi user config. Chỉ khi tắt menu đi bật lại thì mới đổi.
     * only init in {@MainMenu}</pre>
     */
    public static void setActionBtnSize(int actionBtnSize) {
        ACTION_BTN_SIZE = actionBtnSize;
        if (ActionDivider.linePaint != null) {
            ActionDivider.linePaint.setStrokeWidth(Math.min(ACTION_BTN_SIZE / 1.5f, 55f));
        }
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
            divider.init(x1, y1, x2, y2);
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
            divider.init(x1, y1, x2, y2);
            if (zoomAction.getZoomType() == Action.Zoom.ZOOM_IN) {
                view1 = createActionView(context, ViewType.ZOOM_IN);
                view2 = createActionView(context, ViewType.ZOOM_IN);
            } else {
                view1 = createActionView(context, ViewType.ZOOM_OUT);
                view2 = createActionView(context, ViewType.ZOOM_OUT);
            }
        }
        changeIndex();
        int fitSize = ACTION_BTN_SIZE / 2;
        x1 -= fitSize;
        x2 -= fitSize;
        y1 -= fitSize;
        y2 -= fitSize;
        paramsView1 = getLayoutParam(x1, y1);
        view1.setOnTouchListener(this);
        ((TextView) view1.findViewById(R.id.tv_action_index)).setTextSize(ACTION_BTN_SIZE / 5.5f);
        if (view2 != null) {
            paramsView2 = getLayoutParam(x2, y2);
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
        int flags;
        if (enable) {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        } else {
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }
        paramsView1.flags = flags;
        onViewChangeListener.onChange(view1, paramsView1);
        if (view2 != null) {
            paramsView2.flags = flags;
            onViewChangeListener.onChange(view2, paramsView2);
        }
    }

    @NonNull
    private WindowManager.LayoutParams getLayoutParam(int x, int y) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ACTION_BTN_SIZE, ACTION_BTN_SIZE, x, y,
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
        View view = LayoutInflater.from(context).inflate(R.layout.floating_action, null);
        ImageView imgAction = view.findViewById(R.id.iv_action);
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
                if (divider != null) {
                    setPosition(view, layoutParams.x, layoutParams.y, divider.path);
                    divider.invalidate();
                } else {
                    setPosition(view, layoutParams.x, layoutParams.y, null);
                }
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
            divider.path.reset();
            divider.path.moveTo(x1, y1);
            divider.path.lineTo(x2, y2);
            divider.invalidate();
        }
    }

    private void setPosition(View view, int x, int y, Path path) {
        int fitSize = ACTION_BTN_SIZE / 2;
        x += fitSize;
        y += fitSize;
        if (action instanceof Action.Click) {
            ((Action.Click) action).setX(x);
            ((Action.Click) action).setY(y);
        }
        if (path == null) {
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
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x2, y2);
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

    private enum ViewType {CLICK, SWIPE_FROM, SWIPE_TO, ZOOM_IN, ZOOM_OUT}

    public static class ActionDivider extends View {

        private final Path path;
        private static Paint linePaint;

        public ActionDivider(Context context) {
            super(context);
            path = new Path();
            if (linePaint == null) {
                linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                linePaint.setStrokeWidth(Math.min(ACTION_BTN_SIZE / 1.5f, 55f));
                linePaint.setColor(Color.parseColor("#bbbbbb"));
                linePaint.setAlpha(99);
                linePaint.setStyle(Paint.Style.STROKE);
            }
        }

        public void init(int x1, int y1, int x2, int y2) {
            path.moveTo(x1, y1);
            path.lineTo(x2, y2);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, linePaint);
        }

    }
}
