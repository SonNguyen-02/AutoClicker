package com.mct.auto_clicker.overlays.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;

public class WarningExistsDialog extends OverlayDialogController {

    private final DialogInterface.OnClickListener mOnClickListener;

    public WarningExistsDialog(@NonNull Context context, DialogInterface.OnClickListener onClickListener) {
        super(context);
        this.mOnClickListener = onClickListener;
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        return new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_stop_service, R.drawable.ic_save, R.color.textTitle))
                .setMessage(R.string.dialog_desc_discard_content)
                .setPositiveButton(R.string.ok, mOnClickListener)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.save, mOnClickListener);
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    @Override
    protected boolean isOverlay() {
        return true;
    }
}
