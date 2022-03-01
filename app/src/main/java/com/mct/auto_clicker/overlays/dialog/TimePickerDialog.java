package com.mct.auto_clicker.overlays.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.baseui.views.MyTimePickerDialog;

public class TimePickerDialog extends OverlayDialogController {

    private MyTimePickerDialog.OnTimeSetListener mOnSaveTimeListener;
    private final int hour, minute, seconds;
    private final boolean isOverlay;

    public TimePickerDialog(@NonNull Context context, MyTimePickerDialog.OnTimeSetListener mOnSaveTimeListener, int hour, int minute, int seconds, boolean isOverlay) {
        super(context);
        this.mOnSaveTimeListener = mOnSaveTimeListener;
        this.hour = hour;
        this.minute = minute;
        this.seconds = seconds;
        this.isOverlay = isOverlay;
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
        return isOverlay;
    }

    @Override
    protected void onVisibilityChanged(boolean visible) {
        if (!visible) {
            mOnSaveTimeListener = null;
        }
    }
}
