package com.mct.auto_clicker.overlays.dialog;

import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_AMOUNT;
import static com.mct.auto_clicker.database.domain.Configure.RUN_TYPE_TIME;
import static com.mct.auto_clicker.overlays.dialog.DialogHelper.getTitleView;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.ConfigureListSimpleAdapter;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;
import com.mct.auto_clicker.utils.ScreenMetrics;

public class SettingConfigureDialog extends SettingConfigureOverlay {

    private final Configure configure;
    private View titleView;
    private TextView tvTitle;
    private EditText edtLoopDelay;
    private AlertDialog dialog;
    private final ConfigureListSimpleAdapter.OnConfigureChooseListener onConfigureChooseListener;

    public SettingConfigureDialog(@NonNull Context context, Configure configure, ConfigureListSimpleAdapter.OnConfigureChooseListener onConfigureChooseListener) {
        super(context);
        this.configure = configure;
        this.onConfigureChooseListener = onConfigureChooseListener;
    }

    @Override
    protected View onCreateTitleView() {
        titleView = getTitleView(context, R.layout.view_dialog_title, configure.getName(),
                -1, R.drawable.ic_rename, R.color.textTitle);
        return titleView;
    }

    @Override
    protected Enum<SettingType> settingSaveType() {
        return SettingType.CONFIGURE;
    }

    @Override
    protected Configure initConfigure() {
        return configure;
    }

    @Override
    protected void onDialogCreated(@NonNull AlertDialog dialog) {
        View view = getView();
        TextView tvMinimum;
        view.findViewById(R.id.tv_maximum).setVisibility(View.GONE);
        tvMinimum = view.findViewById(R.id.tv_minimum);
        tvMinimum.setText(context.getString(R.string.dialog_edit_minimum, 0));

        edtLoopDelay = view.findViewById(R.id.edt_edit);
        edtLoopDelay.setText(String.valueOf(configure.getTimeDelay()));

        tvTitle = titleView.findViewById(android.R.id.title);
        titleView.setOnClickListener(this::onTitleViewClicked);
        titleView.setBackground(ContextCompat.getDrawable(context, R.drawable.ripple_white_bg_primary));

        // set click to btnNeutral (disable auto close of dialog)
        Button btnNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        btnNeutral.setOnClickListener(v -> showSubOverlay(new ChooseConfigureDialog(context, configure -> {
            onConfigureChooseListener.onConfigureChoose(configure);
            dismiss();
        }, isOverlay()), false));
    }

    private void onTitleViewClicked(View view) {
        View titleDialogEditView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        EditText edtName = titleDialogEditView.findViewById(R.id.tv_name);
        edtName.setText(configure.getName());
        edtName.selectAll();
        showDialog(new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_rename_configure, R.drawable.ic_rename, R.color.textTitle))
                .setView(titleDialogEditView)
                .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                    if (!TextUtils.isEmpty(edtName.getText())) {
                        String name = edtName.getText().toString().trim();
                        configure.setName(name);
                        tvTitle.setText(name);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create());
    }

    private void showDialog(AlertDialog newDialog) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dialog = newDialog;
        newDialog.getWindow().setType(ScreenMetrics.TYPE_COMPAT_OVERLAY);
        newDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        newDialog.setOnDismissListener(dialogInterface -> dialog = null);
        newDialog.show();
    }

    @Override
    protected boolean isOverlay() {
        return true;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == DialogInterface.BUTTON_POSITIVE) {
            configure.setRunType(getTypeStopLoop());
            if (getTypeStopLoop() == RUN_TYPE_AMOUNT) {
                configure.setAmountExec(getAmount());
            }
            if (getTypeStopLoop() == RUN_TYPE_TIME) {
                configure.setTimeStop(getTimeStop());
            }
            ConfigurePermissionPresenter configurePresenter = new ConfigurePermissionPresenter(context);
            long id = configurePresenter.saveConfigure(configure);
            configure.setId(id);
            try {
                int timeDelay = Integer.parseInt(edtLoopDelay.getText().toString().trim());
                configure.setTimeDelay(timeDelay);
            } catch (NumberFormatException ignored) {
            }
        }
    }


}
