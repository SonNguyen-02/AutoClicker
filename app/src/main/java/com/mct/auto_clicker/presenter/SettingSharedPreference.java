package com.mct.auto_clicker.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Configure;

public class SettingSharedPreference {

    private static final String SHARED_NAME = "AUTO_CLICKER.SHARED.SETTING";

    // default setting for configure
    private static final String K_TYPE_STOP_THE_LOOP = "k_type_stop_the_loop";
    private static final int DEFAULT_TYPE_STOP_THE_LOOP = Configure.RUN_TYPE_INFINITY;

    private static final String K_STOP_LOOP_BY_AMOUNT = "k_stop_loop_by_amount";
    private static final int DEFAULT_STOP_LOOP_BY_AMOUNT = 1;
    // save a long number
    private static final String K_STOP_LOOP_BY_TIME = "k_stop_loop_by_time";
    private static final int DEFAULT_STOP_LOOP_BY_TIME = 0;

    private static final String K_LOOP_DELAY = "k_loop_delay_time";
    private static final int DEFAULT_LOOP_DELAY = 0;

    // default setting for action
    public static final int MIN_CLICK_EXEC_TIME = 10;
    public static final int MIN_SWIPE_EXEC_TIME = 200;
    public static final int MIN_ZOOM_EXEC_TIME = 500;

    public static final int MAX_EXEC_TIME = 60000;

    private static final String K_ACTION_DELAY = "k_time_wait_next_action";
    private static final int DEFAULT_ACTION_DELAY = 50;

    private static final String K_CLICK_EXEC_TIME = "k_click_exec_time";
    private static final int DEFAULT_CLICK_EXEC_TIME = 75;

    private static final String K_SWIPE_EXEC_TIME = "k_swipe_exec_time";
    private static final int DEFAULT_SWIPE_EXEC_TIME = 500;

    private static final String K_ZOOM_EXEC_TIME = "k_zoom_exec_time";
    private static final int DEFAULT_ZOOM_EXEC_TIME = 2000;

    // Anti-detection
    private static final String K_INCREASE_RANDOM_ACTION_DELAY_TIME = "k_increase_random_wait_time";
    private static final int DEFAULT_INCREASE_RANDOM_ACTION_DELAY_TIME = 0;

    private static final String K_RANDOM_LOCATION = "k_random_location";
    private static final int DEFAULT_RANDOM_LOCATION = 0; //PX

    // general setting
    public static final int MIN_BUTTON_ACTION_SIZE = 80;
    public static final int MAX_BUTTON_ACTION_SIZE = 140;
    private static final int DEFAULT_BUTTON_ACTION_SIZE = 110;// PX | min 80 max 140
    private static final String K_BUTTON_ACTION_SIZE = "k_action_size";

    public static final int MIN_BUTTON_MENU_SIZE = 90;
    public static final int MAX_BUTTON_MENU_SIZE = 120;
    private static final int DEFAULT_BUTTON_MENU_SIZE = 105;
    private static final String K_BUTTON_MENU_SIZE = "k_menu_size";

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    private static SettingSharedPreference instance;

    private SettingSharedPreference(@NonNull Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static SettingSharedPreference getInstance(Context mContext) {
        if (instance == null) {
            instance = new SettingSharedPreference(mContext);
        }
        return instance;
    }

    public int getTypeStopTheLoop() {
        return mSharedPreferences.getInt(K_TYPE_STOP_THE_LOOP, DEFAULT_TYPE_STOP_THE_LOOP);
    }

    public SettingSharedPreference setTypeStopTheLoop(int value) {
        mEditor.putInt(K_TYPE_STOP_THE_LOOP, value);
        return this;
    }

    public int getStopLoopByAmount() {
        return mSharedPreferences.getInt(K_STOP_LOOP_BY_AMOUNT, DEFAULT_STOP_LOOP_BY_AMOUNT);
    }

    public SettingSharedPreference setStopLoopByAmount(int value) {
        mEditor.putInt(K_STOP_LOOP_BY_AMOUNT, value);
        return this;
    }

    public long getStopLoopByTime() {
        return mSharedPreferences.getLong(K_STOP_LOOP_BY_TIME, DEFAULT_STOP_LOOP_BY_TIME);
    }

    public SettingSharedPreference setStopLoopByTime(long value) {
        mEditor.putLong(K_STOP_LOOP_BY_TIME, value);
        return this;
    }

    public int getLoopDelay() {
        return mSharedPreferences.getInt(K_LOOP_DELAY, DEFAULT_LOOP_DELAY);
    }

    public SettingSharedPreference setLoopDelay(int value) {
        mEditor.putInt(K_LOOP_DELAY, value);
        return this;
    }

    public int getActionDelay() {
        return mSharedPreferences.getInt(K_ACTION_DELAY, DEFAULT_ACTION_DELAY);
    }

    public SettingSharedPreference setActionDelay(int value) {
        mEditor.putInt(K_ACTION_DELAY, value);
        return this;
    }

    public int getClickExecTime() {
        return mSharedPreferences.getInt(K_CLICK_EXEC_TIME, DEFAULT_CLICK_EXEC_TIME);
    }

    public SettingSharedPreference setClickExecTime(int value) {
        mEditor.putInt(K_CLICK_EXEC_TIME, value);
        return this;
    }

    public int getSwipeExecTime() {
        return mSharedPreferences.getInt(K_SWIPE_EXEC_TIME, DEFAULT_SWIPE_EXEC_TIME);
    }

    public SettingSharedPreference setSwipeExecTime(int value) {
        mEditor.putInt(K_SWIPE_EXEC_TIME, value);
        return this;
    }

    public int getZoomExecTime() {
        return mSharedPreferences.getInt(K_ZOOM_EXEC_TIME, DEFAULT_ZOOM_EXEC_TIME);
    }

    public SettingSharedPreference setZoomExecTime(int value) {
        mEditor.putInt(K_ZOOM_EXEC_TIME, value);
        return this;
    }

    public int getIncreaseRandomActionDelayTime() {
        return mSharedPreferences.getInt(K_INCREASE_RANDOM_ACTION_DELAY_TIME, DEFAULT_INCREASE_RANDOM_ACTION_DELAY_TIME);
    }

    public SettingSharedPreference setIncreaseRandomActionDelayTime(int value) {
        mEditor.putInt(K_INCREASE_RANDOM_ACTION_DELAY_TIME, value);
        return this;
    }

    public int getRandomLocation() {
        return mSharedPreferences.getInt(K_RANDOM_LOCATION, DEFAULT_RANDOM_LOCATION);
    }

    public SettingSharedPreference setRandomLocation(int value) {
        mEditor.putInt(K_RANDOM_LOCATION, value);
        return this;
    }

    public int getButtonActionSize() {
        return mSharedPreferences.getInt(K_BUTTON_ACTION_SIZE, DEFAULT_BUTTON_ACTION_SIZE);
    }

    public SettingSharedPreference setButtonActionSize(int value) {
        if (value < MIN_BUTTON_ACTION_SIZE) value = MIN_BUTTON_ACTION_SIZE;
        if (value > MAX_BUTTON_ACTION_SIZE) value = MAX_BUTTON_ACTION_SIZE;
        mEditor.putInt(K_BUTTON_ACTION_SIZE, value);
        return this;
    }

    public int getButtonMenuSize() {
        return mSharedPreferences.getInt(K_BUTTON_MENU_SIZE, DEFAULT_BUTTON_MENU_SIZE);
    }

    public SettingSharedPreference setButtonMenuSize(int value) {
        if (value < MIN_BUTTON_MENU_SIZE) value = MIN_BUTTON_MENU_SIZE;
        if (value > MAX_BUTTON_MENU_SIZE) value = MAX_BUTTON_MENU_SIZE;
        mEditor.putInt(K_BUTTON_MENU_SIZE, value);
        return this;
    }

    public void commit() {
        mEditor.commit();
    }
}
