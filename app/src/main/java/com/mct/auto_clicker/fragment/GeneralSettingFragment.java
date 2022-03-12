package com.mct.auto_clicker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.activities.AutoClickerActivity;
import com.mct.auto_clicker.adapter.GeneralSettingViewPagerAdapter;

public class GeneralSettingFragment extends Fragment {

    private View mView;
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;

    @NonNull
    public static GeneralSettingFragment newInstance() {
        return new GeneralSettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_general_setting, container, false);

        initUi();
        initToolBar();
        initData();

        return mView;
    }

    private void initUi() {
        mViewPager = mView.findViewById(R.id.view_pager);
        mBottomNavigationView = mView.findViewById(R.id.bottom_navigation);
    }

    private void initToolBar() {
        ((Toolbar) mView.findViewById(R.id.toolbar)).setNavigationOnClickListener(v ->
                ((AutoClickerActivity) requireActivity()).removeFragmentFromMainFrame());
    }

    private void initData() {
        GeneralSettingViewPagerAdapter adapter = new GeneralSettingViewPagerAdapter(requireActivity());
        mViewPager.setAdapter(adapter);
        mViewPager.setUserInputEnabled(false);

        mBottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.bottom_nav_setting_target) {
                mViewPager.setCurrentItem(0, false);
                return true;
            }
            if (id == R.id.bottom_nav_setting_menu) {
                mViewPager.setCurrentItem(1, false);
                return true;
            }
            return false;
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBottomNavigationView.getMenu().findItem(R.id.bottom_nav_setting_target).setChecked(true);
                } else {
                    mBottomNavigationView.getMenu().findItem(R.id.bottom_nav_setting_menu).setChecked(true);
                }
                super.onPageSelected(position);
            }
        });
    }

}
