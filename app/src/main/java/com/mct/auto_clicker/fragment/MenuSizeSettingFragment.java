package com.mct.auto_clicker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.baseui.views.ColorSeekBar;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

public class MenuSizeSettingFragment extends Fragment {

    private static final int SEEK_BAR_SMOOTH = 10;
    private View mView;
    private SeekBar sbSize, sbAlpha;
    private ColorSeekBar sbColor;
    private SwitchCompat swOrientation;
    private MenuPreference mMenuPreference;
    private MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired;
    private MenuSettingFragment.OnSettingChange mOnSettingChange;


    public MenuSizeSettingFragment() {
    }

    public MenuSizeSettingFragment(MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired, MenuSettingFragment.OnSettingChange mOnSettingChange) {
        this.mMenuItemAdapterRequired = mMenuItemAdapterRequired;
        this.mOnSettingChange = mOnSettingChange;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting_menu_size, container, false);
        mMenuPreference = MenuPreference.getInstance(requireContext());

        initUi();

        initData();

        return mView;
    }

    private void initUi() {
        sbSize = mView.findViewById(R.id.sb_size);
        sbAlpha = mView.findViewById(R.id.sb_alpha);
        sbColor = mView.findViewById(R.id.sb_color);
        swOrientation = mView.findViewById(R.id.sw_orientation);
    }

    private void initData() {
        int menuSizeDiff = MenuPreference.MAX_BUTTON_MENU_SIZE - MenuPreference.MIN_BUTTON_MENU_SIZE;
        int currentSizeMenu = mMenuPreference.getButtonMenuSize();
        sbSize.setMax(menuSizeDiff * SEEK_BAR_SMOOTH);
        sbSize.setProgress((currentSizeMenu - MenuPreference.MIN_BUTTON_MENU_SIZE) * SEEK_BAR_SMOOTH);

        int alphaDiff = MenuPreference.MAX_ALPHA - MenuPreference.MIN_ALPHA;
        int currentAlpha = mMenuPreference.getButtonMenuAlpha();
        sbAlpha.setMax(alphaDiff * SEEK_BAR_SMOOTH);
        sbAlpha.setProgress((currentAlpha - MenuPreference.MIN_ALPHA) * SEEK_BAR_SMOOTH);

        initSeekBarListener(sbSize);
        initSeekBarListener(sbAlpha);

        sbColor.setPosition(mMenuPreference.getMenuSbPosition(), mMenuPreference.getMenuColor());
        sbColor.setOnColorChangeListener((position, color) -> {
            mMenuPreference.setMenuSbPosition(position).setMenuColor(color).commit();
            if (mMenuItemAdapterRequired != null) {
                mMenuItemAdapterRequired.getMenuItemAdapter().setColor(color);
            }
        });

        int currentOrientation = mMenuPreference.getMenuOrientation();
        swOrientation.setChecked(currentOrientation == RecyclerView.HORIZONTAL);
        swOrientation.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (mOnSettingChange != null) {
                int orientation = checked ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL;
                mOnSettingChange.onOrientationChange(orientation);
                mMenuPreference.setMenuOrientation(orientation).commit();
            }
        });
    }

    private void initSeekBarListener(@NonNull SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (seekBar == sbSize) {
                    int value = progress / SEEK_BAR_SMOOTH + MenuPreference.MIN_BUTTON_MENU_SIZE;
                    if (mMenuItemAdapterRequired != null) {
                        mMenuItemAdapterRequired.getMenuItemAdapter().setMenuItemSize(value);
                    }
                } else {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_ALPHA;
                    if (mOnSettingChange != null) {
                        mOnSettingChange.onAlphaChange(value);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar == sbSize) {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_BUTTON_MENU_SIZE;
                    mMenuPreference.setButtonMenuSize(value).commit();
                } else {
                    int value = seekBar.getProgress() / SEEK_BAR_SMOOTH + MenuPreference.MIN_ALPHA;
                    mMenuPreference.setButtonMenuAlpha(value).commit();
                }
            }
        });
    }

}
