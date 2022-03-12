package com.mct.auto_clicker.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mct.auto_clicker.fragment.MenuListSettingFragment;
import com.mct.auto_clicker.fragment.MenuSettingFragment;
import com.mct.auto_clicker.fragment.MenuSizeSettingFragment;

public class GeneralSettingMenuViewPagerAdapter extends FragmentStateAdapter {

    private final MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired;
    private final MenuSettingFragment.OnSettingChange mOnSettingChange;


    public GeneralSettingMenuViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                                              MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired,
                                              MenuSettingFragment.OnSettingChange mOnSettingChange) {
        super(fragmentActivity);
        this.mMenuItemAdapterRequired = mMenuItemAdapterRequired;
        this.mOnSettingChange = mOnSettingChange;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new MenuSizeSettingFragment(mMenuItemAdapterRequired, mOnSettingChange);
        }
        return new MenuListSettingFragment(mMenuItemAdapterRequired);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
