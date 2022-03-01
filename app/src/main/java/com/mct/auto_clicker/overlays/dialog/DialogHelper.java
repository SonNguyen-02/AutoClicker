package com.mct.auto_clicker.overlays.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.mct.auto_clicker.R;

public class DialogHelper {

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    @StringRes int title) {
        return getTitleView(context, titleViewRes, title, R.drawable.ic_setting, R.color.textTitle);
    }

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    @StringRes int title,
                                    @DrawableRes int icon,
                                    @ColorRes int tint) {
        return getTitleView(context, titleViewRes, title, icon, -1, tint);
    }

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    @StringRes int title,
                                    @DrawableRes int icon,
                                    @DrawableRes int icon1,
                                    @ColorRes int tint) {
        View titleView = context.getSystemService(LayoutInflater.class).inflate(titleViewRes, null);
        TextView tvTitle = titleView.findViewById(android.R.id.title);
        tvTitle.setText(title);
        if (icon != -1) {
            setImageConfig(context, titleView.findViewById(android.R.id.icon), icon, tint);
        }
        if (icon1 != -1) {
            setImageConfig(context, titleView.findViewById(android.R.id.icon1), icon1, tint);
        }
        return titleView;
    }

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    String title,
                                    @DrawableRes int icon,
                                    @DrawableRes int icon1,
                                    @ColorRes int tint) {
        View titleView = context.getSystemService(LayoutInflater.class).inflate(titleViewRes, null);
        TextView tvTitle = titleView.findViewById(android.R.id.title);
        tvTitle.setText(title);
        if (icon != -1) {
            setImageConfig(context, titleView.findViewById(android.R.id.icon), icon, tint);
        }
        if (icon1 != -1) {
            setImageConfig(context, titleView.findViewById(android.R.id.icon1), icon1, tint);
        }
        return titleView;
    }

    private static void setImageConfig(Context context, @NonNull ImageView imageView, @DrawableRes int icon, @ColorRes int tint) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(icon);
        imageView.setColorFilter(ContextCompat.getColor(context, tint), PorterDuff.Mode.SRC_IN);
    }

    public static long timeToMillisecond(int hour, int minute, int seconds) {
        return (hour * 3600L + minute * 60L + seconds) * 1000;
    }

    /**
     * @param millisecond > 0
     * @return arr[0] => hour, arr[1] => minute, arr[2] => second
     */
    @NonNull
    public static int[] millisecondToTime(long millisecond) {
        int[] arr = new int[3];
        int second = (int) (millisecond / 1000);
        arr[0] = second / 3600;
        second %= 3600;
        arr[1] = second / 60;
        arr[2] = second % 60;
        return arr;
    }

    @NonNull
    public static String getFormatTime(long millisecond) {
        int[] arr = millisecondToTime(millisecond);
        return getFormatTime(arr[0], arr[1], arr[2]);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public static String getFormatTime(int hour, int minute, int second) {
        return String.format("%02d", hour) +
                ":" + String.format("%02d", minute) +
                ":" + String.format("%02d", second);
    }
}
