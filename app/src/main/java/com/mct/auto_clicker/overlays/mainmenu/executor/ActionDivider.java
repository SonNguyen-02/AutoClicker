package com.mct.auto_clicker.overlays.mainmenu.executor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ActionDivider extends View {

    private final Path path;
    private static final Paint linePaint;

    static {
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#aeaeae"));
        linePaint.setAlpha(99);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    public ActionDivider(Context context) {
        this(context, null);
    }

    public ActionDivider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ActionDivider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
    }

    public static Paint getLinePaint() {
        return linePaint;
    }

    public static void setStrokeWidth(int actionBtnSize) {
        linePaint.setStrokeWidth(Math.min(actionBtnSize / 1.5f, 55f));
    }

    public static void setAlpha(int alpha) {
        linePaint.setAlpha(alpha - 1);
    }

    public void set(int x1, int y1, int x2, int y2) {
        path.reset();
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
