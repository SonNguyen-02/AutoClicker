package com.mct.auto_clicker.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class DialogHelper {

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    @StringRes int title) {
        return getTitleView(context, titleViewRes, title, -1, -1);
    }

    @NonNull
    public static View getTitleView(@NonNull Context context,
                                    @LayoutRes int titleViewRes,
                                    @StringRes int title,
                                    @DrawableRes int icon,
                                    @ColorRes int tint) {
        View titleView = context.getSystemService(LayoutInflater.class).inflate(titleViewRes, null);
        TextView tvTitle = titleView.findViewById(android.R.id.title);
        tvTitle.setText(title);
        if (icon == -1) {
            return titleView;
        }
        ImageView imgIcon = titleView.findViewById(android.R.id.icon);
        imgIcon.setImageResource(icon);
        imgIcon.setColorFilter(ContextCompat.getColor(context, tint), android.graphics.PorterDuff.Mode.SRC_IN);

        return titleView;
    }

    public static long timeToMillisecond(int hour, int minute, int seconds) {
        return (hour * 3600L + minute * 60L + seconds) * 1000;
    }

    /**
     * @param millisecond > 0
     * @return arr[0] => hour, arr[1] => minute, arr[2] => second
     */
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
        int[] arr = new int[3];
        int second = (int) (millisecond / 1000);
        arr[0] = second / 3600;
        second %= 3600;
        arr[1] = second / 60;
        arr[2] = second % 60;
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
