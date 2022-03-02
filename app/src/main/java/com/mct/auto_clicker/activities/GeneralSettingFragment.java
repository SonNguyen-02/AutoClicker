package com.mct.auto_clicker.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.presenter.SettingSharedPreference;

public class GeneralSettingFragment extends Fragment {

    private TextView tvIndex;
    private ViewGroup layoutAction, layoutButtons;
    private SeekBar sbSettingAction, sbSettingMenu;

    private SettingSharedPreference settingSharedPreference;

    private final int actionSizeDiff;
    private final int menuSizeDiff;

    private boolean isExpand = true;

    @NonNull
    public static GeneralSettingFragment newInstance() {
        return new GeneralSettingFragment();
    }

    public GeneralSettingFragment() {
        actionSizeDiff = SettingSharedPreference.MAX_BUTTON_ACTION_SIZE - SettingSharedPreference.MIN_BUTTON_ACTION_SIZE;
        menuSizeDiff = SettingSharedPreference.MAX_BUTTON_MENU_SIZE - SettingSharedPreference.MIN_BUTTON_MENU_SIZE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_setting, container, false);
        settingSharedPreference = SettingSharedPreference.getInstance(requireContext());
        initUi(view);
        return view;
    }

    private void initUi(@NonNull View view) {
        layoutAction = view.findViewById(R.id.layout_action);
        tvIndex = layoutAction.findViewById(R.id.tv_action_index);
        layoutButtons = view.findViewById(R.id.layout_buttons);
        sbSettingAction = view.findViewById(R.id.sb_setting_action);
        sbSettingMenu = view.findViewById(R.id.sb_setting_menu);
        ((Toolbar) view.findViewById(R.id.toolbar)).setNavigationOnClickListener(v ->
                ((AutoClickerActivity) requireActivity()).removeFragmentFromMainFrame());
        view.findViewById(R.id.btn_move).setOnClickListener(this::onClickCollapseExpand);
        initData();
    }

    private void onClickCollapseExpand(View view) {
        if (isExpand) {
            setVisible(View.VISIBLE, R.id.btn_exists);
            setVisible(View.GONE, R.id.btn_add_touch, R.id.btn_add_swipe, R.id.btn_add_zoom_in, R.id.btn_add_zoom_out, R.id.btn_remove, R.id.btn_setting);
            isExpand = false;
        } else {
            setVisible(View.GONE, R.id.btn_exists);
            setVisible(View.VISIBLE, R.id.btn_add_touch, R.id.btn_add_swipe, R.id.btn_add_zoom_in, R.id.btn_add_zoom_out, R.id.btn_remove, R.id.btn_setting);
            isExpand = true;
        }

    }

    private void setVisible(int visibility, @NonNull @IdRes int... idRes) {
        for (int id : idRes) {
            layoutButtons.findViewById(id).setVisibility(visibility);
        }
    }


    private void initData() {
        int currentSizeAction = settingSharedPreference.getButtonActionSize();
        int currentSizeMenu = settingSharedPreference.getButtonMenuSize();

        sbSettingAction.setMax(actionSizeDiff);
        sbSettingAction.setProgress(currentSizeAction - SettingSharedPreference.MIN_BUTTON_ACTION_SIZE);
        setLayoutActionSize(currentSizeAction);

        sbSettingAction.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int value = progress + SettingSharedPreference.MIN_BUTTON_ACTION_SIZE;
                setLayoutActionSize(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress() + SettingSharedPreference.MIN_BUTTON_ACTION_SIZE;
                settingSharedPreference.setButtonActionSize(value).commit();
            }
        });

        sbSettingMenu.setMax(menuSizeDiff);
        sbSettingMenu.setProgress(currentSizeMenu - SettingSharedPreference.MIN_BUTTON_MENU_SIZE);
        setLayoutButtonSize(currentSizeMenu);

        sbSettingMenu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int value = progress + SettingSharedPreference.MIN_BUTTON_MENU_SIZE;
                setLayoutButtonSize(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress() + SettingSharedPreference.MIN_BUTTON_MENU_SIZE;
                settingSharedPreference.setButtonMenuSize(value).commit();
            }
        });
    }

    private void setLayoutActionSize(int currentSizeAction) {
        tvIndex.setTextSize(currentSizeAction / 5.5f);
        layoutAction.getLayoutParams().width = currentSizeAction;
        layoutAction.getLayoutParams().height = currentSizeAction;
        layoutAction.setLayoutParams(layoutAction.getLayoutParams());
    }

    private void setLayoutButtonSize(int currentSizeMenu) {
        for (int i = 0; i < layoutButtons.getChildCount(); i++) {
            layoutButtons.getChildAt(i).getLayoutParams().width = currentSizeMenu;
            layoutButtons.getChildAt(i).getLayoutParams().height = currentSizeMenu;
            layoutButtons.getChildAt(i).setLayoutParams(layoutButtons.getChildAt(i).getLayoutParams());
        }
    }

}
