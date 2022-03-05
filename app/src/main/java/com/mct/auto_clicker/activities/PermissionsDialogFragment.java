package com.mct.auto_clicker.activities;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mct.auto_clicker.AutoClickerService;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.presenter.ConfigurePermissionPresenter;

import java.util.Objects;

public class PermissionsDialogFragment extends DialogFragment {

    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    private static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":settings:show_fragment_args";

    // phải delay vì khi thay đổi -> nó gọi observer trc khi gọi đến hàm onServiceConnected của service
    private static final int DELAY_PERMISSION_GRANTED = 100;

    private ConfigurePermissionPresenter configurePermission;

    private ImageView overlayStateView;
    private ImageView accessibilityStateView;

    private PermissionDialogListener mPermissionDialogListener;

    private final ContentObserver observer = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new Handler().postDelayed(() -> {
                if (configurePermission.isAccessibilityPermissionValid()) {
                    ActivityManager activityManager = requireActivity().getSystemService(ActivityManager.class);
                    activityManager.getAppTasks().get(0).moveToFront();
                }
                if (configurePermission.arePermissionsGranted()) {
                    callPermissionGranted();
                } else {
                    onResume();
                }
            }, DELAY_PERMISSION_GRANTED);
        }
    };

    public interface PermissionDialogListener {
        void onPermissionsGranted();
    }

    @NonNull
    public static PermissionsDialogFragment newInstance() {
        return new PermissionsDialogFragment();
    }

    @NonNull
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
        Uri uri = Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        requireContext().getContentResolver().registerContentObserver(uri, false, observer);
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
        if (configurePermission.arePermissionsGranted()) {
            callPermissionGranted();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireContext().getContentResolver().unregisterContentObserver(observer);
    }

    private void callPermissionGranted() {
        if (mPermissionDialogListener != null) {
            mPermissionDialogListener.onPermissionsGranted();
            dismissAllowingStateLoss();
        }
    }

    private void onOverlayClicked() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void onAccessibilityClicked() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);

        Bundle bundle = new Bundle();
        String showArgs = requireContext().getPackageName() + "/" + AutoClickerService.class.getName();
        bundle.putString(EXTRA_FRAGMENT_ARG_KEY, showArgs);
        intent.putExtra(EXTRA_FRAGMENT_ARG_KEY, showArgs);
        intent.putExtra(EXTRA_SHOW_FRAGMENT_ARGUMENTS, bundle);

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
