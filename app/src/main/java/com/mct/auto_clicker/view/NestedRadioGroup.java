package com.mct.auto_clicker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NestedRadioGroup extends LinearLayout {

    private RadioButton activeChild = null;
    private OnSelectedListener mOnSelectedListener;

    public interface OnSelectedListener {
        void onSelected(RadioButton view);
    }

    public NestedRadioGroup(Context context) {
        super(context);
        init();
    }

    public NestedRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initChild();
            }
        });
    }

    public void setOnSelectedListener(OnSelectedListener mOnSelectedListener) {
        this.mOnSelectedListener = mOnSelectedListener;
    }

    private void initChild() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // chỉ áp dụng cho radio btn đầu tiên khi tìm thấy
            RadioButton rb = findRadioButtonOnChild(child);
            if (rb == null) {
                continue;
            }
            rb.setOnClickListener(view -> {
                if (activeChild != rb) {
                    rb.setChecked(true);
                    if (activeChild != null) {
                        activeChild.setChecked(false);
                    }
                    activeChild = rb;
                    if (mOnSelectedListener != null) {
                        mOnSelectedListener.onSelected(rb);
                    }
                }
            });
        }
    }

    @Nullable
    private RadioButton findRadioButtonOnChild(@NonNull View child) {
        if (child instanceof RadioButton) {
            return (RadioButton) child;
        }
        if (child instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) child).getChildCount(); i++) {
                View view = ((ViewGroup) child).getChildAt(i);
                if (view instanceof RadioButton) {
                    return (RadioButton) view;
                }

                if (view instanceof ViewGroup) {
                    return findRadioButtonOnChild(view);
                }
            }
        }
        return null;
    }


}
