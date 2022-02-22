package com.mct.auto_clicker.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.mct.auto_clicker.database.domain.Configure;

public class MySharedPreference {

    private static final String SHARED_NAME = "AUTO_CLICKER.SHARED";

    // default setting for configure
    private static final String K_TYPE_STOP_THE_LOOP = "k_type_stop_the_loop";
    private static final int DEFAULT_TYPE_STOP_THE_LOOP = Configure.RUN_TYPE_INFINITY;

    private static final String K_STOP_LOOP_BY_AMOUNT = "k_stop_loop_by_amount";
    // save a long number
    private static final String K_STOP_LOOP_BY_TIME = "k_stop_loop_by_time";

    private static final String K_LOOP_DELAY_TIME = "k_loop_delay_time";
    private static final int DEFAULT_LOOP_DELAY_TIME = 0;

    // default setting for action
    private static final String K_TIME_WAIT_NEXT_ACTION = "k_time_wait_next_action";
    private static final int DEFAULT_TIME_WAIT_NEXT_ACTION = 50;

    private static final String K_CLICK_EXEC_TIME = "k_click_exec_time";
    private static final int DEFAULT_CLICK_EXEC_TIME = 75;

    private static final String K_SWIPE_EXEC_TIME = "k_swipe_exec_time";
    private static final int DEFAULT_SWIPE_EXEC_TIME = 500;

    private static final String K_ZOOM_EXEC_TIME = "k_zoom_exec_time";
    private static final int DEFAULT_ZOOM_EXEC_TIME = 2000;

    // Anti-detection
    private static final String K_INCREASE_RANDOM_WAIT_TIME = "k_increase_random_wait_time";
    private static final int DEFAULT_INCREASE_RANDOM_WAIT_TIME = 0;

    private static final String K_RANDOM_LOCATION = "k_random_location";
    private static final int DEFAULT_RANDOM_LOCATION = 0; //PX

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;
    private static MySharedPreference instance;

    private MySharedPreference(@NonNull Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized static MySharedPreference getInstance(Context mContext) {
        if (instance == null) {
            instance = new MySharedPreference(mContext);
        }
        return instance;
    }

    public int getTypeStopTheLoop() {
        return mSharedPreferences.getInt(K_TYPE_STOP_THE_LOOP, DEFAULT_TYPE_STOP_THE_LOOP);
    }

    public MySharedPreference setTypeStopTheLoop(int value) {
        mEditor.putInt(K_TYPE_STOP_THE_LOOP, value);
        return this;
    }

    public int getStopLoopByAmount() {
        return mSharedPreferences.getInt(K_STOP_LOOP_BY_AMOUNT, 1);
    }

    public MySharedPreference setStopLoopByAmount(int value) {
        mEditor.putInt(K_STOP_LOOP_BY_AMOUNT, value);
        return this;
    }

    public long getStopLoopByTime() {
        return mSharedPreferences.getLong(K_STOP_LOOP_BY_TIME, 0);
    }

    public MySharedPreference setStopLoopByTime(long value) {
        mEditor.putLong(K_STOP_LOOP_BY_TIME, value);
        return this;
    }

    public int getLoopDelayTime() {
        return mSharedPreferences.getInt(K_LOOP_DELAY_TIME, DEFAULT_LOOP_DELAY_TIME);
    }

    public MySharedPreference setLoopDelayTime(int value) {
        mEditor.putInt(K_LOOP_DELAY_TIME, value);
        return this;
    }

    public int getTimeWaitNextAction() {
        return mSharedPreferences.getInt(K_TIME_WAIT_NEXT_ACTION, DEFAULT_TIME_WAIT_NEXT_ACTION);
    }

    public MySharedPreference setTimeWaitNextAction(int value) {
        mEditor.putInt(K_TIME_WAIT_NEXT_ACTION, value);
        return this;
    }

    public int getClickExecTime() {
        return mSharedPreferences.getInt(K_CLICK_EXEC_TIME, DEFAULT_CLICK_EXEC_TIME);
    }

    public MySharedPreference setClickExecTime(int value) {
        mEditor.putInt(K_CLICK_EXEC_TIME, value);
        return this;
    }

    public int getSwipeExecTime() {
        return mSharedPreferences.getInt(K_SWIPE_EXEC_TIME, DEFAULT_SWIPE_EXEC_TIME);
    }

    public MySharedPreference setSwipeExecTime(int value) {
        mEditor.putInt(K_SWIPE_EXEC_TIME, value);
        return this;
    }

    public int getZoomExecTime() {
        return mSharedPreferences.getInt(K_ZOOM_EXEC_TIME, DEFAULT_ZOOM_EXEC_TIME);
    }

    public MySharedPreference setZoomExecTime(int value) {
        mEditor.putInt(K_ZOOM_EXEC_TIME, value);
        return this;
    }

    public int getIncreaseRandomWaitTime() {
        return mSharedPreferences.getInt(K_INCREASE_RANDOM_WAIT_TIME, DEFAULT_INCREASE_RANDOM_WAIT_TIME);
    }

    public MySharedPreference setIncreaseRandomWaitTime(int value) {
        mEditor.putInt(K_INCREASE_RANDOM_WAIT_TIME, value);
        return this;
    }

    public int getRandomLocation() {
        return mSharedPreferences.getInt(K_RANDOM_LOCATION, DEFAULT_RANDOM_LOCATION);
    }

    public MySharedPreference setRandomLocation(int value) {
        mEditor.putInt(K_RANDOM_LOCATION, value);
        return this;
    }

    public void commit() {
        mEditor.commit();
    }
}
