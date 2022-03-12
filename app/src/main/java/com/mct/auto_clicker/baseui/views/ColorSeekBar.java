package com.mct.auto_clicker.baseui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mct.auto_clicker.R;

public class ColorSeekBar extends View {

    private static final float minThumbRadius = 16f;
    private int[] colorSeeds = new int[]{Color.parseColor("#000000"), Color.parseColor("#FF5252"), Color.parseColor("#FFEB3B"), Color.parseColor("#00C853"), Color.parseColor("#00B0FF"), Color.parseColor("#D500F9"), Color.parseColor("#8D6E63")};
    private int canvasHeight = 60;
    private int barHeight = 20;
    private final RectF rectf = new RectF();
    private final Paint rectPaint = new Paint();
    private final Paint thumbBorderPaint = new Paint();
    private final Paint thumbPaint = new Paint();
    private float thumbX = 24f;
    private float thumbY = canvasHeight / 2f;
    private float thumbBorder = 4f;
    private float thumbRadius = 16f;
    private float thumbBorderRadius = thumbRadius + thumbBorder;
    private final float paddingStart = 30f;
    private final float paddingEnd = 30f;
    private float barCornerRadius = 8f;
    private float oldThumbRadius = thumbRadius;
    private float oldThumbBorderRadius = thumbBorderRadius;
    private OnColorChangeListener colorChangeListener;

    public ColorSeekBar(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public ColorSeekBar(Context context, @Nullable AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.ColorSeekBar);
        int colorsId = typedArray.getResourceId(R.styleable.ColorSeekBar_colorSeeds, 0);
        if (colorsId != 0) colorSeeds = getColorsById(colorsId);
        barCornerRadius = typedArray.getDimension(R.styleable.ColorSeekBar_cornerRadius, 8f);
        barHeight = (int) typedArray.getDimension(R.styleable.ColorSeekBar_barHeight, 20f);
        thumbBorder = typedArray.getDimension(R.styleable.ColorSeekBar_thumbBorder, 4f);
        int thumbBorderColor = typedArray.getColor(R.styleable.ColorSeekBar_thumbBorderColor, Color.WHITE);
        typedArray.recycle();

        rectPaint.setAntiAlias(true);

        thumbBorderPaint.setAntiAlias(true);
        thumbBorderPaint.setColor(thumbBorderColor);

        thumbPaint.setAntiAlias(true);

        thumbRadius = (barHeight / 2f);
        if (thumbRadius < minThumbRadius) {
            thumbRadius = minThumbRadius;
        }
        thumbBorderRadius = thumbRadius + thumbBorder;
        canvasHeight = (int) (thumbBorderRadius * 3);
        thumbY = canvasHeight / 2f;

        oldThumbRadius = thumbRadius;
        oldThumbBorderRadius = thumbBorderRadius;
    }

    @NonNull
    private int[] getColorsById(@ArrayRes int id) {
        if (isInEditMode()) {
            String[] s = getContext().getResources().getStringArray(id);
            int[] colors = new int[s.length];
            for (int i = 0; i < s.length; i++) {
                colors[i] = Color.parseColor(s[i]);
            }
            return colors;
        } else {
            TypedArray typedArray = getContext().getResources().obtainTypedArray(id);
            int[] colors = new int[typedArray.length()];
            for (int i = 0; i < typedArray.length(); i++) {
                colors[i] = typedArray.getColor(i, Color.BLACK);
            }
            typedArray.recycle();
            return colors;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //color bar position
        float barLeft = paddingStart;
        float barRight = getWidth() - paddingEnd;
        float barTop = ((canvasHeight / 2f) - (barHeight / 2f));
        float barBottom = ((canvasHeight / 2f) + (barHeight / 2f));

        //draw color bar
        rectf.set(barLeft, barTop, barRight, barBottom);
        canvas.drawRoundRect(rectf, barCornerRadius, barCornerRadius, rectPaint);

        if (thumbX < barLeft) {
            thumbX = barLeft;
        } else if (thumbX > barRight) {
            thumbX = barRight;
        }
        int color = pickColor(thumbX, getWidth());
        thumbPaint.setColor(color);

        // draw color bar thumb
        canvas.drawCircle(thumbX, thumbY, thumbBorderRadius, thumbBorderPaint);
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint);
    }

    private int pickColor(float position, int canvasWidth) {
        float value = (position - paddingStart) / (canvasWidth - (paddingStart + paddingEnd));

        if (value <= 0f) {
            return colorSeeds[0];
        }
        if (value >= 1) {
            return colorSeeds[colorSeeds.length - 1];
        }

        float colorPosition = value * (colorSeeds.length - 1);
        int i = (int) colorPosition;
        colorPosition -= i;
        int c0 = colorSeeds[i];
        int c1 = colorSeeds[i + 1];

        int red = mix(Color.red(c0), Color.red(c1), colorPosition);
        int green = mix(Color.green(c0), Color.green(c1), colorPosition);
        int blue = mix(Color.blue(c0), Color.blue(c1), colorPosition);
        return Color.rgb(red, green, blue);
    }

    private int mix(int start, int end, float position) {
        return start + Math.round(position * (end - start));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient colorGradient = new LinearGradient(0f, 0f, w, 0f, colorSeeds, null, Shader.TileMode.CLAMP);
        rectPaint.setShader(colorGradient);

        if (oldw == 0) return;
        thumbX *= (float) w / (float) oldw;
        int curColor = getColor();
        colorChangeListener.onColorChangeListener(thumbX, curColor);
        new Handler().postDelayed(() -> thumbPaint.setColor(curColor), 50);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, canvasHeight);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                thumbBorderRadius = (oldThumbBorderRadius * 1.5f);
                thumbRadius = (oldThumbRadius * 1.5f);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                thumbX = event.getX();
                invalidate();
                if (colorChangeListener != null) {
                    colorChangeListener.onColorChangeListener(getPosition(), getColor());
                }
                break;
            case MotionEvent.ACTION_UP:
                thumbBorderRadius = oldThumbBorderRadius;
                thumbRadius = oldThumbRadius;
                invalidate();
                break;
        }
        return true;
    }

    public void setPosition(float position, int color) {
        thumbX = position;
        invalidate();
        thumbPaint.setColor(color);
    }

    public float getPosition() {
        return thumbX;
    }

    public int getColor() {
        return thumbPaint.getColor();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.colorChangeListener = onColorChangeListener;
    }

    public interface OnColorChangeListener {
        void onColorChangeListener(float position, int color);
    }
}