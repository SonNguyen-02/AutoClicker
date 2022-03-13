package com.mct.auto_clicker.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.config.Constant;
import com.mct.auto_clicker.overlays.dialog.RateAppDialog;

public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 2;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;

    private static final String PREF_NAME = "app_rater";
    private static final String PREF_KEY_LAUNCH_COUNT = "launch_count";
    private static final String PREF_KEY_LAUNCH_DATE = "launch_date";
    private static final String PREF_KEY_DONT_SHOW_AGAIN = "dont_show_again";

    public static boolean launch(@NonNull Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(PREF_KEY_DONT_SHOW_AGAIN, false)) {
            return false;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        int launchCount = prefs.getInt(PREF_KEY_LAUNCH_COUNT, 0) + 1;
        editor.putInt(PREF_KEY_LAUNCH_COUNT, launchCount);

        // Get date of launch
        long dateLaunch = prefs.getLong(PREF_KEY_LAUNCH_DATE, 0);
        if (dateLaunch == 0) {
            dateLaunch = System.currentTimeMillis();
            editor.putLong(PREF_KEY_LAUNCH_DATE, dateLaunch);
        }
        editor.apply();

        // Wait at least n days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT &&
                System.currentTimeMillis() >= dateLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
            editor.putInt(PREF_KEY_LAUNCH_COUNT, 0);
            editor.putLong(PREF_KEY_LAUNCH_DATE, System.currentTimeMillis());
            editor.apply();
            new RateAppDialog(mContext, new RateAppDialog.OnClickRateListener() {
                @Override
                public void onRateNowClicked() {
                    goToPlayStore(mContext);
                }

                @Override
                public void onNoThankClicked() {
                    editor.putBoolean(PREF_KEY_DONT_SHOW_AGAIN, true);
                    editor.apply();
                }
            }).create(null);
            return true;
        }
        return false;
    }

    public static void goToPlayStore(@NonNull Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.CH_PLAY_URI));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }
}