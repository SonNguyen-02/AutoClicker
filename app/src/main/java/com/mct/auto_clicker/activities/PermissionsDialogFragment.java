package com.mct.auto_clicker.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

import java.util.Objects;

public class PermissionsDialogFragment extends DialogFragment {

    private ConfigurePermissionPresenter configurePermission;

    private ImageView overlayStateView;
    private ImageView accessibilityStateView;

    private PermissionDialogListener mPermissionDialogListener;

    public interface PermissionDialogListener {
        void onPermissionsGranted();
    }

    public static PermissionsDialogFragment newInstance() {
        return new PermissionsDialogFragment();
    }

    public static PermissionsDialogFragment newInstance(PermissionDialogListener listener) {
        return new PermissionsDialogFragment(listener);
    }

    public PermissionsDialogFragment() {
    }

    private PermissionsDialogFragment(PermissionDialogListener mPermissionDialogListener) {
        this.mPermissionDialogListener = mPermissionDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_permissions))
                .setView(R.layout.dialog_permissions)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    if (mPermissionDialogListener != null) {
                        mPermissionDialogListener.onPermissionsGranted();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        configurePermission = new ConfigurePermissionPresenter(requireContext());
        overlayStateView = dialog.findViewById(R.id.img_config_overlay_status);
        View overlayView = dialog.findViewById(R.id.item_overlay_permission);
        overlayView.setOnClickListener(view -> onOverlayClicked());
        accessibilityStateView = dialog.findViewById(R.id.img_config_accessibility_status);
        View accessibilityView = dialog.findViewById(R.id.item_accessibility_permission);
        accessibilityView.setOnClickListener(view -> onAccessibilityClicked());
    }

    @Override
    public void onResume() {
        super.onResume();
        setConfigStateDrawable(overlayStateView, configurePermission.isOverlayPermissionValid());
        setConfigStateDrawable(accessibilityStateView, configurePermission.isAccessibilityPermissionValid());
        ((AlertDialog) Objects.requireNonNull(getDialog()))
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(configurePermission.arePermissionsGranted());
    }

    private void onOverlayClicked() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
        startActivity(intent);
    }

    private void onAccessibilityClicked() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void setConfigStateDrawable(ImageView view, boolean state) {
        if (state) {
            view.setImageResource(R.drawable.ic_confirm);
            view.getDrawable().setTint(Color.GREEN);
        } else {
            view.setImageResource(R.drawable.ic_cancel);
            view.getDrawable().setTint(Color.RED);
        }
    }
}
