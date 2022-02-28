package com.mct.auto_clicker.overlays.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.ConfigureListSimpleAdapter;
import com.mct.auto_clicker.baseui.overlays.OverlayDialogController;
import com.mct.auto_clicker.database.domain.Configure;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

import java.util.List;

public class ChooseConfigureDialog extends OverlayDialogController {

    private final ConfigureListSimpleAdapter.OnConfigureChooseListener mListener;
    private final ConfigurePermissionPresenter configurePresenter;
    private RecyclerView rcvConfigure;
    private TextView tvNoData;

    public ChooseConfigureDialog(@NonNull Context context, ConfigureListSimpleAdapter.OnConfigureChooseListener listener) {
        super(context);
        this.mListener = listener;
        configurePresenter = new ConfigurePermissionPresenter(context);
    }

    @Override
    protected AlertDialog.Builder onCreateDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose_configure, null);
        init(view);
        return new AlertDialog.Builder(context)
                .setCustomTitle(DialogHelper.getTitleView(context, R.layout.view_dialog_title, R.string.dialog_title_choose_configure))
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.create_new, (dialogInterface, i) ->
                        onConfigureChoose(configurePresenter.getNewConfigure("Configure " + configurePresenter.getCountConfigures()))
                );
    }

    private void init(@NonNull View view) {
        rcvConfigure = view.findViewById(R.id.rcv_configure_list);
        tvNoData = view.findViewById(R.id.tv_no_data);
    }

    @Override
    protected void onDialogCreated(AlertDialog dialog) {
        List<Configure> mListConfigure = configurePresenter.getAllConfigure();
        if (mListConfigure.isEmpty()) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            ConfigureListSimpleAdapter adapter = new ConfigureListSimpleAdapter(context, mListConfigure, this::onConfigureChoose);
            rcvConfigure.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rcvConfigure.setLayoutManager(layoutManager);
        }
    }

    private void onConfigureChoose(Configure configure) {
        mListener.onConfigureChoose(configure);
        dismiss();
    }

    @Override
    protected boolean isOverlay() {
        return false;
    }
}
