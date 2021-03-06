package com.mct.auto_clicker.baseui.overlays;

import android.content.Context;
import android.content.DialogInterface;
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

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.utils.ScreenMetrics;

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
     * The AlertDialog in this method have higher priority {@link #onCreateDialog()}
     *
     * @return != null => {@link #dialog} = {@link #onInitDialog()}
     */
    protected AlertDialog onInitDialog() {
        return null;
    }

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

    /**
     * isOverlay
     *
     * @return true if the dialog is overlay, false isn't
     */
    protected abstract boolean isOverlay();

    @Override
    protected void onCreate() {
        // u can init a alert dialog or builder
        if (onInitDialog() != null) {
            dialog = onInitDialog();
        } else {
            dialog = onCreateDialog().create();
        }

        // init dialog listener
        dialog.setOnDismissListener(dialogInterface -> {
            dismiss();
            onDialogDismissed();
        });
        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                dismiss();
                return true;
            }
            return false;
        });
        // init color btn
        dialog.setOnShowListener(dialogInterface -> {
            Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (buttonNegative != null) {
                buttonNegative.setTextColor(context.getColor(R.color.btn_negative));
            }
            Button buttonNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            if (buttonNeutral != null) {
                buttonNeutral.setTextColor(context.getColor(R.color.btn_neutral));
            }
        });
        // init window type...
        Window window = dialog.getWindow();
        if (isOverlay()) {
            window.setType(ScreenMetrics.TYPE_COMPAT_OVERLAY);
        }
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

    /**
     * Called when the visibility of the dialog has changed due to a call to [start] or [stop].
     * <p>
     * Once the sub element is dismissed, this method will be called again, notifying for the new visibility of the
     * dialog.
     *
     * @param visible the dialog visibility value. True for visible, false for hidden.
     */
    protected void onVisibilityChanged(boolean visible) {
    }

    @Override
    public final void start() {
        if (!isShowing) {
            isShowing = true;
            if (dialog != null) dialog.show();
            onVisibilityChanged(true);
        }
        super.start();
    }

    @Override
    public final void stop(Boolean hideUi) {
        if (hideUi && isShowing) {
            hideSoftInput();
            if (dialog != null) dialog.hide();
            isShowing = false;
            onVisibilityChanged(false);
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
