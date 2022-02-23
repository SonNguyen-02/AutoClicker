package com.mct.auto_clicker.dialog;

import static com.mct.auto_clicker.dialog.DialogHelper.millisecondToTime;
import static com.mct.auto_clicker.dialog.DialogHelper.timeToMillisecond;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.presenter.SettingSharedPreference;
import com.mct.auto_clicker.view.MyTimePickerDialog;
import com.mct.auto_clicker.view.TimePicker;

public class TimePickerDialog extends OverlayDialogController {

    private MyTimePickerDialog.OnTimeSetListener mOnSaveTimeListener;
    private final int hour, minute, seconds;

    public TimePickerDialog(@NonNull Context context, MyTimePickerDialog.OnTimeSetListener mOnSaveTimeListener, int hour, int minute, int seconds) {
        super(context);
        this.mOnSaveTimeListener = mOnSaveTimeListener;
        this.hour = hour;
        this.minute = minute;
        this.seconds = seconds;
    }

    @Override
    protected AlertDialog onInitDialog() {
        View titleView = DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_time_setting);
        return new MyTimePickerDialog(context, mOnSaveTimeListener, hour, minute, seconds, true, titleView);
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        return null;
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    @Override
    protected boolean isOverlay() {
        return false;
    }

    @Override
    protected void onVisibilityChanged(boolean visible) {
        if (!visible) {
            mOnSaveTimeListener = null;
        }
    }
}
