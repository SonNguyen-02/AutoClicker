package com.mct.auto_clicker.overlays.dialog;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.database.domain.Configure;

public class SettingStopLoopDialog extends SettingConfigureOverlay {

    public SettingStopLoopDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View onCreateTitleView() {
        return DialogHelper.getTitleView(context, R.layout.view_dialog_title,
                R.string.dialog_title_select_loop_stop_condition);
    }

    @Override
    protected Enum<SettingType> settingSaveType() {
        return SettingType.GLOBAL;
    }

    @Override
    protected Configure initConfigure() {
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
    public void onClick(DialogInterface dialogInterface, int i) {
        mSettingSharedPref.setTypeStopTheLoop(getTypeStopLoop());
        if (getTypeStopLoop() == RUN_TYPE_AMOUNT) {
            mSettingSharedPref.setStopLoopByAmount(getAmount());
        }
        if (getTypeStopLoop() == RUN_TYPE_TIME) {
            mSettingSharedPref.setStopLoopByTime(getTimeStop());
        }
        mSettingSharedPref.commit();
    }
}
