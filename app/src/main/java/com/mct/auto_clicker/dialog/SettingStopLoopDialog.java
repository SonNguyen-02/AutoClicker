package com.mct.auto_clicker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.view.NestedRadioGroup;

public class SettingStopLoopDialog extends OverlayDialogController {


    public SettingStopLoopDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_setting_stop_loop, null);
        init(view);

        View titleView = context.getSystemService(LayoutInflater.class).inflate(R.layout.view_dialog_title, null);
        ((TextView) titleView.findViewById(android.R.id.title)).setText(R.string.dialog_title_select_loop_stop_condition);

        return new AlertDialog.Builder(context)
                .setCustomTitle(titleView)
                .setView(view)
                .setPositiveButton(android.R.string.ok, this::onClickPositive)
                .setNegativeButton(android.R.string.cancel, null);
    }

    private void init(@NonNull View view) {
        NestedRadioGroup nestedRadioGroup = view.findViewById(R.id.nested_group);
        nestedRadioGroup.setOnSelectedListener(v -> {
            Log.e("ddd", "init: "+v.getId() );
        });
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
    }

    private void onClickPositive(DialogInterface dialogInterface, int i) {
        Toast.makeText(context, "Click OKE", Toast.LENGTH_SHORT).show();
    }
}
