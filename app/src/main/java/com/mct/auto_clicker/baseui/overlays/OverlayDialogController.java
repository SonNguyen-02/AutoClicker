package com.mct.auto_clicker.baseui.overlays;

import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

public abstract class OverlayDialogController extends OverlayController {

    protected Context context;
    /**
     * The Android InputMethodManger, for ensuring the keyboard dismiss on dialog dismiss.
     */
    private final InputMethodManager inputMethodManager;

    public OverlayDialogController(@NonNull Context context) {
        this.context = context;
        inputMethodManager = context.getSystemService(InputMethodManager.class);
    }

    /**
     * The dialog currently displayed by this controller.
     * Null until [onCreate] is called, or if it has been dismissed.
     */
    private AlertDialog dialog = null;

    /**
     * Creates the dialog shown by this controller.
     * Note that the cancelable value and the dismiss listener will be overridden with internal values once, so any
     * values for them defined here will not be kept.
     *
     * @return the builder for the dialog to be created.
     */
    protected abstract AlertDialog.Builder onCreateDialog();

    /**
     * Setup the dialog view.
     * Called once the dialog is created and first show, it allows the implementation to initialize the content views.
     *
     * @param dialog the newly created dialog.
     */
    protected abstract void onDialogCreated(AlertDialog dialog);

    @Override
    protected void onCreate() {
        dialog = onCreateDialog()
                .setOnDismissListener(dialogInterface -> {
                    dismiss();
                    onDialogDismissed();
                })
                .setCancelable(false)
                .setOnKeyListener((dialogInterface, i, keyEvent) -> {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        dismiss();
                        return true;
                    } else {
                        return false;
                    }
                }).create();
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        Window window = dialog.getWindow();
        window.setType(type);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        window.getDecorView().setOnTouchListener((view, motionEvent) -> {
            hideSoftInput();
            view.performClick();
            return false;
        });
        isShowing = true;
        dialog.show();

        onDialogCreated(dialog);
    }

    @Override
    public final void start() {
        if (!isShowing) {
            isShowing = true;
            if (dialog != null) dialog.show();
        }
        super.start();
    }

    @Override
    public final void stop(Boolean hideUi) {
        if (hideUi && isShowing) {
            hideSoftInput();
            if (dialog != null) dialog.hide();
            isShowing = false;
        }
        super.stop(hideUi);
    }

    @Override
    protected void onDismissed() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public final void showSubOverlay(OverlayController overlayController, Boolean hideCurrent) {
        super.showSubOverlay(overlayController, hideCurrent);
        hideSoftInput();
    }

    /**
     * Handle the dialog dismissing.
     */
    @CallSuper
    protected void onDialogDismissed() {
        isShowing = false;
        dialog = null;
    }

    /**
     * Update the selected button display.
     *
     * @param button     the button to be updated.
     * @param visibility the new button visibility.
     * @param textId     the string resource identifier for the text of the button.
     * @param listener   the new click listener of the button. Can be null if none is needed.
     */
    protected void changeButtonState(@NonNull Button button, int visibility, @StringRes int textId, @Nullable View.OnClickListener listener) {
        switch (visibility) {
            case View.VISIBLE:
                button.setVisibility(View.VISIBLE);
                if (textId != -1) {
                    button.setText(textId);
                }
                button.setOnClickListener(listener);
                button.setEnabled(true);
                break;
            case View.INVISIBLE:
                button.setVisibility(View.VISIBLE);
                if (textId != -1) {
                    button.setText(textId);
                }
                button.setEnabled(false);
                break;
            case View.GONE:
                button.setVisibility(visibility);
                break;
        }
    }

    /**
     * Hide the software keyboard.
     */
    private void hideSoftInput() {
        if (dialog != null) {
            inputMethodManager.hideSoftInputFromWindow(dialog.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
