package com.mct.auto_clicker.overlays.dialog;

import static com.mct.auto_clicker.presenter.SettingSharedPreference.MAX_EXEC_TIME;
import static com.mct.auto_clicker.presenter.SettingSharedPreference.MIN_CLICK_EXEC_TIME;
import static com.mct.auto_clicker.presenter.SettingSharedPreference.MIN_SWIPE_EXEC_TIME;
import static com.mct.auto_clicker.presenter.SettingSharedPreference.MIN_ZOOM_EXEC_TIME;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.database.domain.Action;

public class SettingActionDialog extends OverlayDialogController {

    private final Action action;
    private final int index;
    private final OnClickDelete onClickDelete;

    private EditText edtActionDelay, edtActionDuration;
    private CheckBox cbAntiDetection;

    public SettingActionDialog(@NonNull Context context, Action action, int index, OnClickDelete onClickDelete) {
        super(context);
        this.action = action;
        this.index = index;
        this.onClickDelete = onClickDelete;
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting_action, null);
        initUi(view);
        String title = action.getClass().getSimpleName() + " #" + index;
        return new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, title, R.drawable.ic_setting, -1, R.color.textTitle))
                .setView(view)
                .setPositiveButton(R.string.save, this::onClickSave)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.delete, (dialogInterface, i) -> onClickDelete.onDelete());
    }

    private void initUi(@NonNull View view) {
        edtActionDelay = view.findViewById(R.id.edt_action_delay);
        edtActionDuration = view.findViewById(R.id.edt_action_duration);
        edtActionDelay.setText(String.valueOf(action.getTimeDelay()));
        edtActionDuration.setText(String.valueOf(action.getActionDuration()));
        if (action instanceof Action.Click) {
            cbAntiDetection = view.findViewById(R.id.cb_anti_detection);
            cbAntiDetection.setVisibility(View.VISIBLE);
            cbAntiDetection.setChecked(((Action.Click) action).isAntiDetection());
        }
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    @Override
    protected boolean isOverlay() {
        return true;
    }

    private void onClickSave(DialogInterface dialogInterface, int i) {
        try {
            if (action instanceof Action.Click && cbAntiDetection != null) {
                ((Action.Click) action).setAntiDetection(cbAntiDetection.isChecked());
            }

            int timeDelay = Integer.parseInt(edtActionDelay.getText().toString().trim());
            if (timeDelay < 0) timeDelay = 0;
            action.setTimeDelay(timeDelay);

            int MIN_DEFAULT_DURATION = action instanceof Action.Click ? MIN_CLICK_EXEC_TIME :
                    action instanceof Action.Swipe ? MIN_SWIPE_EXEC_TIME : MIN_ZOOM_EXEC_TIME;
            int duration = Integer.parseInt(edtActionDuration.getText().toString().trim());
            if (duration < MIN_DEFAULT_DURATION) duration = MIN_DEFAULT_DURATION;
            if (duration > MAX_EXEC_TIME) duration = MAX_EXEC_TIME;
            action.setActionDuration(duration);
        } catch (NumberFormatException ignored) {
        }
    }

    public interface OnClickDelete {
        void onDelete();
    }

}
