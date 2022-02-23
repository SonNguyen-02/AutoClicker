package com.mct.auto_clicker.dialog;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.dialog.DialogHelper.millisecondToTime;
import static com.mct.auto_clicker.dialog.DialogHelper.timeToMillisecond;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.presenter.SettingSharedPreference;
import com.mct.auto_clicker.view.MyTimePickerDialog;
import com.mct.auto_clicker.view.NestedRadioGroup;
import com.mct.auto_clicker.view.TimePicker;

public class SettingStopLoopDialog extends OverlayDialogController implements DialogInterface.OnClickListener, NestedRadioGroup.OnSelectedListener, View.OnClickListener {

    private NestedRadioGroup nestedRadioGroup;
    private RadioButton rbInfinityLoop, rbNumberOfLoops, rbCountdownTimer;
    private EditText edtNumberOfLoop;
    private TextView tvCountdownTimer;

    private final SettingSharedPreference mSettingSharedPref;

    private int hour, minute, seconds;
    private int typeStopLoop;

    public SettingStopLoopDialog(@NonNull Context context) {
        super(context);
        mSettingSharedPref = SettingSharedPreference.getInstance(context);
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stop_loop, null);
        initUi(view);
        return new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_select_loop_stop_condition))
                .setView(view)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, null);
    }

    private void initUi(@NonNull View view) {
        nestedRadioGroup = view.findViewById(R.id.nested_group);
        rbInfinityLoop = view.findViewById(R.id.rb_infinity_loop);
        rbNumberOfLoops = view.findViewById(R.id.rb_number_of_loops);
        rbCountdownTimer = view.findViewById(R.id.rb_countdown_timer);
        edtNumberOfLoop = view.findViewById(R.id.edt_number_of_loop);
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer);
        initData();
    }

    @SuppressLint("DefaultLocale")
    private void initData() {
        nestedRadioGroup.setOnSelectedListener(this);
        initRadioBtn(mSettingSharedPref.getTypeStopTheLoop());
        edtNumberOfLoop.setText(String.valueOf(mSettingSharedPref.getStopLoopByAmount()));
        int[] times = millisecondToTime(mSettingSharedPref.getStopLoopByTime());
        hour = times[0];
        minute = times[1];
        seconds = times[2];
        tvCountdownTimer.setText(getFormatTime(hour, minute, seconds));
        tvCountdownTimer.setOnClickListener(this);
    }

    private void initRadioBtn(int typeStopLoop) {
        this.typeStopLoop = typeStopLoop;
        switch (typeStopLoop) {
            case RUN_TYPE_INFINITY:
                rbInfinityLoop.setChecked(true);
                rbNumberOfLoops.setChecked(false);
                rbCountdownTimer.setChecked(false);
                nestedRadioGroup.setActiveChild(rbInfinityLoop);
                break;
            case RUN_TYPE_AMOUNT:
                rbInfinityLoop.setChecked(false);
                rbNumberOfLoops.setChecked(true);
                rbCountdownTimer.setChecked(false);
                nestedRadioGroup.setActiveChild(rbNumberOfLoops);
                break;
            case RUN_TYPE_TIME:
                rbInfinityLoop.setChecked(false);
                rbNumberOfLoops.setChecked(false);
                rbCountdownTimer.setChecked(true);
                nestedRadioGroup.setActiveChild(rbCountdownTimer);
                break;
        }
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    private String getFormatTime(int hour, int minute, int second) {
        return String.format("%02d", hour) +
                ":" + String.format("%02d", minute) +
                ":" + String.format("%02d", second);
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    @Override
    protected boolean isOverlay() {
        return false;
    }

    @Override
    public void onClick(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (v, hourOfDay, minute, seconds) -> {
            this.hour = hourOfDay;
            this.minute = minute;
            this.seconds = seconds;
            tvCountdownTimer.setText(getFormatTime(hourOfDay, minute, seconds));
        }, hour, minute, seconds);
        showSubOverlay(timePickerDialog, true);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        mSettingSharedPref.setTypeStopTheLoop(typeStopLoop).commit();
        if (typeStopLoop == RUN_TYPE_AMOUNT) {
            try {
                int amount = Integer.parseInt(edtNumberOfLoop.getText().toString());
                if (amount > 1) {
                    mSettingSharedPref.setStopLoopByAmount(amount).commit();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (typeStopLoop == RUN_TYPE_TIME) {
            long millisecond = timeToMillisecond(hour, minute, seconds);
            mSettingSharedPref.setStopLoopByTime(millisecond).commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onSelected(@NonNull RadioButton rb) {
        switch (rb.getId()) {
            case R.id.rb_infinity_loop:
                typeStopLoop = RUN_TYPE_INFINITY;
                break;
            case R.id.rb_number_of_loops:
                typeStopLoop = RUN_TYPE_AMOUNT;
                break;
            case R.id.rb_countdown_timer:
                typeStopLoop = RUN_TYPE_TIME;
                break;
        }
    }
}
