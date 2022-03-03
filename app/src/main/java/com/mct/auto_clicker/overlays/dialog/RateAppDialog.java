package com.mct.auto_clicker.overlays.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;

public class RateAppDialog extends OverlayDialogController implements View.OnClickListener {

    private final OnClickRateListener onClickRateListener;

    public RateAppDialog(@NonNull Context context, OnClickRateListener onClickRateListener) {
        super(context);
        this.onClickRateListener = onClickRateListener;
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rate_app, null);
        initUi(view);
        return new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_rate_app, R.drawable.ic_rate, R.color.textTitle))
                .setView(view);
    }

    private void initUi(@NonNull View view) {
        view.findViewById(R.id.btn_rate_now).setOnClickListener(this);
        view.findViewById(R.id.btn_no_thank).setOnClickListener(this);
        view.findViewById(R.id.btn_remind_later).setOnClickListener(this);
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    @Override
    protected boolean isOverlay() {
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_rate_now:
                if (onClickRateListener != null) {
                    onClickRateListener.onRateNowClicked();
                }
                dismiss();
                break;
            case R.id.btn_no_thank:
                if (onClickRateListener != null) {
                    onClickRateListener.onNoThankClicked();
                }
                dismiss();
                break;
            case R.id.btn_remind_later:
                dismiss();
                break;
        }
    }

    public interface OnClickRateListener {
        void onRateNowClicked();

        void onNoThankClicked();
    }
}
