package com.mct.auto_clicker.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mct.auto_clicker.fragment.MenuSettingFragment;
import com.mct.auto_clicker.fragment.TargetSettingFragment;

public class GeneralSettingViewPagerAdapter extends FragmentStateAdapter {

    public GeneralSettingViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new TargetSettingFragment();
        }
        return new MenuSettingFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
