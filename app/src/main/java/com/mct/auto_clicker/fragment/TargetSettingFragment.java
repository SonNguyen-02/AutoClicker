package com.mct.auto_clicker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.mainmenu.executor.ActionDivider;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

public class TargetSettingFragment extends Fragment {

    private static final int SEEK_BAR_SMOOTH = 10;
    private View mView;
    private View[] actionsView;
    private ActionDivider actionDivider;
    private SeekBar sbSize, sbAlpha;

    private int boxHeight, boxWidth;

    private MenuPreference mMenuPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuPreference = MenuPreference.getInstance(requireContext());
        int actionBtnSize = mMenuPreference.getButtonActionSize();
        ActionDivider.setStrokeWidth(actionBtnSize);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting_target, container, false);

        initUi();
        initData();
        measuredBox();

        return mView;
    }

    private void initUi() {
        actionsView = new View[4];
        actionsView[0] = mView.findViewById(R.id.layout_action_1);
        actionsView[1] = mView.findViewById(R.id.layout_action_2);
        actionsView[2] = mView.findViewById(R.id.layout_action_3);
        actionsView[3] = mView.findViewById(R.id.layout_action_4);
        actionDivider = mView.findViewById(R.id.action_divider);
        sbSize = mView.findViewById(R.id.sb_size);
        sbAlpha = mView.findViewById(R.id.sb_alpha);
    }

    private void measuredBox() {
        actionDivider.post(() -> {
            boxHeight = actionDivider.getMeasuredHeight();
            boxWidth = actionDivider.getMeasuredWidth();
            actionDivider.set(boxWidth * 3 / 4, boxHeight / 4, boxWidth / 4, boxHeight * 3 / 4);
        });
    }

    private void initData() {

        int actionSizeDiff = MenuPreference.MAX_BUTTON_ACTION_SIZE - MenuPreference.MIN_BUTTON_ACTION_SIZE;
        int currentSizeAction = mMenuPreference.getButtonActionSize();
        sbSize.setMax(actionSizeDiff * SEEK_BAR_SMOOTH);
        sbSize.setProgress((currentSizeAction - MenuPreference.MIN_BUTTON_ACTION_SIZE) * SEEK_BAR_SMOOTH);

        int alphaDiff = MenuPreference.MAX_ALPHA - MenuPreference.MIN_ALPHA;
        int currentAlpha = mMenuPreference.getButtonActionAlpha();
        sbAlpha.setMax(alphaDiff * SEEK_BAR_SMOOTH);
        sbAlpha.setProgress((currentAlpha - MenuPreference.MIN_ALPHA) * SEEK_BAR_SMOOTH);

        initSeekBarListener(sbSize);
        initSeekBarListener(sbAlpha);

        setActionSize(currentSizeAction);
        setActionAlpha(currentAlpha);
    }

    private void setActionSize(int actionBtnSize) {
        for (View view : actionsView) {
            view.getLayoutParams().width = actionBtnSize;
            view.getLayoutParams().height = actionBtnSize;
            view.setLayoutParams(view.getLayoutParams());
        }
        ActionDivider.setStrokeWidth(actionBtnSize);
        actionDivider.invalidate();
    }

    private void setActionAlpha(int alpha) {
        for (View view : actionsView) {
            view.setAlpha(alpha / 100f);
        }
        ActionDivider.setAlpha(alpha);
        actionDivider.invalidate();
    }

    private void initSeekBarListener(@NonNull SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (seekBar == sbSize) {
                    int value = progress / SEEK_BAR_SMOOTH + MenuPreference.MIN_BUTTON_ACTION_SIZE;
                    setActionSize(value);
                } else {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_ALPHA;
                    setActionAlpha(value);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar == sbSize) {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_BUTTON_ACTION_SIZE;
                    mMenuPreference.setButtonActionSize(value).commit();
                } else {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_ALPHA;
                    mMenuPreference.setButtonActionAlpha(value).commit();
                }
            }
        });
    }

}
