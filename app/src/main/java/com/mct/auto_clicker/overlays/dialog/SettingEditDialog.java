package com.mct.auto_clicker.overlays.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

public class SettingEditDialog extends OverlayDialogController implements DialogInterface.OnClickListener {

    private final EditType mEditType;
    private EditText edtEdit;

    public SettingEditDialog(@NonNull Context context, EditType mEditType) {
        super(context);
        this.mEditType = mEditType;
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting_edit, null);
        initUi(view);
        return new AlertDialog.Builder(context)
                .setCustomTitle(mEditType.getTitleView(context))
                .setView(view)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, null);
    }

    private void initUi(@NonNull View view) {
        edtEdit = view.findViewById(R.id.edt_edit);
        initEditText();
        edtEdit.selectAll();
        TextInputLayout txtLayout;
        txtLayout = view.findViewById(R.id.text_input_layout);
        txtLayout.setSuffixText(mEditType == EditType.ADT_RAND_LOCATION ? context.getString(R.string.pixel) : context.getString(R.string.milliseconds));

        TextView tvDescription, tvMinimum, tvMaximum;
        tvDescription = view.findViewById(R.id.tv_description);
        tvMinimum = view.findViewById(R.id.tv_minimum);
        tvMaximum = view.findViewById(R.id.tv_maximum);

        tvDescription.setText(mEditType.descRes);
        tvMinimum.setText(context.getString(R.string.dialog_edit_minimum, mEditType.minimum));
        if (mEditType.maximum == -1) {
            tvMaximum.setVisibility(View.GONE);
        } else {
            tvMaximum.setVisibility(View.VISIBLE);
            tvMaximum.setText(context.getString(R.string.dialog_edit_maximum, mEditType.maximum));
        }
    }

    private void initEditText() {
        SettingSharedPreference sharedPreference = SettingSharedPreference.getInstance(context);
        switch (mEditType) {
            case CF_DELAY_LOOP:
                edtEdit.setText(String.valueOf(sharedPreference.getLoopDelay()));
                break;
            case ACT_DELAY_ACTION:
                edtEdit.setText(String.valueOf(sharedPreference.getActionDelay()));
                break;
            case ACT_CLICK_DURATION:
                edtEdit.setText(String.valueOf(sharedPreference.getClickExecTime()));
                break;
            case ACT_SWIPE_DURATION:
                edtEdit.setText(String.valueOf(sharedPreference.getSwipeExecTime()));
                break;
            case ACT_ZOOM_DURATION:
                edtEdit.setText(String.valueOf(sharedPreference.getZoomExecTime()));
                break;
            case ADT_INCREASE_RAND_ACTION_DELAY_TIME:
                edtEdit.setText(String.valueOf(sharedPreference.getIncreaseRandomActionDelayTime()));
                break;
            case ADT_RAND_LOCATION:
                edtEdit.setText(String.valueOf(sharedPreference.getRandomLocation()));
                break;
        }
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
        SettingSharedPreference sharedPreference = SettingSharedPreference.getInstance(context);
        int value = Integer.parseInt(edtEdit.getText().toString());
        if (value < mEditType.minimum) {
            value = mEditType.minimum;
        }
        if (mEditType.maximum != -1 && value > mEditType.maximum) {
            value = mEditType.maximum;
        }
        switch (mEditType) {
            case CF_DELAY_LOOP:
                sharedPreference.setLoopDelay(value);
                break;
            case ACT_DELAY_ACTION:
                sharedPreference.setActionDelay(value);
                break;
            case ACT_CLICK_DURATION:
                sharedPreference.setClickExecTime(value);
                break;
            case ACT_SWIPE_DURATION:
                sharedPreference.setSwipeExecTime(value);
                break;
            case ACT_ZOOM_DURATION:
                sharedPreference.setZoomExecTime(value);
                break;
            case ADT_INCREASE_RAND_ACTION_DELAY_TIME:
                sharedPreference.setIncreaseRandomActionDelayTime(value);
                break;
            case ADT_RAND_LOCATION:
                sharedPreference.setRandomLocation(value);
                break;
        }
        sharedPreference.commit();
    }

    public enum EditType {
        CF_DELAY_LOOP(R.drawable.ic_setting,
                R.string.dialog_title_loop_delay_time,
                R.string.dialog_desc_loop_delay_time,
                0, -1),
        ACT_DELAY_ACTION(R.drawable.ic_sand_time,
                R.string.dialog_title_time_wait_next_action,
                R.string.dialog_desc_time_wait_next_action,
                0, -1),
        ACT_CLICK_DURATION(R.drawable.ic_click_duration,
                R.string.dialog_title_time_click_action_exec,
                R.string.dialog_desc_how_long_will_the_action_take,
                1, 60000),
        ACT_SWIPE_DURATION(R.drawable.ic_swipe_duration,
                R.string.dialog_title_time_swipe_action_exec,
                R.string.dialog_desc_how_long_will_the_action_take,
                200, 60000),
        ACT_ZOOM_DURATION(R.drawable.ic_arrow_opposite,
                R.string.dialog_title_time_zoom_action_exec,
                R.string.dialog_desc_how_long_will_the_action_take,
                400, 60000),
        ADT_INCREASE_RAND_ACTION_DELAY_TIME(R.drawable.ic_add_time,
                R.string.dialog_title_increase_random_wait_time,
                R.string.dialog_desc_increase_random_wait_time,
                0, 60000),
        ADT_RAND_LOCATION(R.drawable.ic_random_location,
                R.string.dialog_title_random_location,
                R.string.dialog_desc_random_location,
                0, 50);

        private final int iconRes;
        private final int titleRes;
        private final int descRes;
        private final int minimum;
        private final int maximum;

        EditType(int iconRes, int titleRes, int descRes, int minimum, int maximum) {
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.descRes = descRes;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @NonNull
        public View getTitleView(Context context) {
            return DialogHelper.getTitleView(context, R.layout.view_dialog_title, titleRes, iconRes, R.color.textTitle);
        }
    }
}
