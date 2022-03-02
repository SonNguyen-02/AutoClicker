package com.mct.auto_clicker.overlays.dialog;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_INFINITY;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.getFormatTime;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.millisecondToTime;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.timeToMillisecond;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.baseui.views.NestedRadioGroup;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

public abstract class SettingConfigureOverlay extends OverlayDialogController implements DialogInterface.OnClickListener, NestedRadioGroup.OnSelectedListener {

    private View view;
    private NestedRadioGroup nestedRadioGroup;
    private RadioButton rbInfinityLoop, rbNumberOfLoops, rbCountdownTimer;
    private EditText edtNumberOfLoop;
    private TextView tvCountdownTimer;

    protected final SettingSharedPreference mSettingSharedPref;

    private int hour, minute, seconds;
    private int typeStopLoop;

    public SettingConfigureOverlay(@NonNull Context context) {
        super(context);
        mSettingSharedPref = SettingSharedPreference.getInstance(context);
    }

    protected abstract View onCreateTitleView();

    protected abstract Enum<SettingType> settingSaveType();

    protected abstract Configure initConfigure();

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        int layoutRes = SettingType.CONFIGURE.equals(settingSaveType()) ?
                R.layout.dialog_setting_configure : R.layout.dialog_setting_stop_loop;
        view = LayoutInflater.from(context).inflate(layoutRes, null);
        initUi(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCustomTitle(onCreateTitleView())
                .setView(view)
                .setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, null);
        if (SettingType.CONFIGURE.equals(settingSaveType())) {
            builder.setNeutralButton(R.string.dialog_setting_configure_load_configure, null);
        }
        return builder;
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
        Configure configure = initConfigure();
        int typeStopLoop;
        int stopAmount;
        long stopTime;
        if (SettingType.CONFIGURE.equals(settingSaveType()) && configure != null) {
            typeStopLoop = configure.getRunType();
            stopAmount = configure.getAmountExec() == null ? 1 : configure.getAmountExec();
            stopTime = configure.getTimeStop() == null ? 0 : configure.getTimeStop();
        } else {
            typeStopLoop = mSettingSharedPref.getTypeStopTheLoop();
            stopAmount = mSettingSharedPref.getStopLoopByAmount();
            stopTime = mSettingSharedPref.getStopLoopByTime();
        }
        nestedRadioGroup.setOnSelectedListener(this);
        initRadioBtn(typeStopLoop);
        edtNumberOfLoop.setText(String.valueOf(stopAmount));
        int[] times = millisecondToTime(stopTime);
        hour = times[0];
        minute = times[1];
        seconds = times[2];
        tvCountdownTimer.setText(getFormatTime(hour, minute, seconds));
        tvCountdownTimer.setOnClickListener(this::onCountDownClicked);
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

    protected View getView() {
        return view;
    }

    protected int getTypeStopLoop() {
        return typeStopLoop;
    }

    protected int getAmount() {
        try {
            int amount = Integer.parseInt(edtNumberOfLoop.getText().toString().trim());
            if (amount > 1) {
                return amount;
            }
        } catch (NumberFormatException ignored) {
        }
        return 1;
    }

    protected long getTimeStop() {
        return timeToMillisecond(hour, minute, seconds);
    }

    private void onCountDownClicked(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (v, hourOfDay, minute, seconds) -> {
            this.hour = hourOfDay;
            this.minute = minute;
            this.seconds = seconds;
            tvCountdownTimer.setText(getFormatTime(hourOfDay, minute, seconds));
        }, hour, minute, seconds, isOverlay());
        showSubOverlay(timePickerDialog, false);
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

    public enum SettingType {
        GLOBAL, CONFIGURE
    }
}
