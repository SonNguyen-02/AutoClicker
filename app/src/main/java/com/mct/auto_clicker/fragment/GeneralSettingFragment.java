package com.mct.auto_clicker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.activities.AutoClickerActivity;
import com.mct.auto_clicker.adapter.GeneralSettingViewPagerAdapter;
import com.mct.auto_clicker.overlays.dialog.DialogHelper;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

public class GeneralSettingFragment extends Fragment {

    private View mView;
    private ViewPager2 mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private OnExistsSetting mOnExistsSetting;

    @NonNull
    public static GeneralSettingFragment newInstance() {
        return new GeneralSettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_general_setting, container, false);

        initUi();

        initData();

        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setOnExistsSetting(OnExistsSetting mOnExistsSetting) {
        this.mOnExistsSetting = mOnExistsSetting;
    }

    private void initUi() {
        mViewPager = mView.findViewById(R.id.view_pager);
        mBottomNavigationView = mView.findViewById(R.id.bottom_navigation);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mView.findViewById(R.id.toolbar));
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset_setting:
                showResetSettingDialog();
                return true;
            case android.R.id.home:
                ((AutoClickerActivity) requireActivity()).removeFragmentFromMainFrame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_reset_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showResetSettingDialog() {
        new AlertDialog.Builder(requireContext())
                .setCustomTitle(DialogHelper.getTitleView(requireContext(), R.layout.view_dialog_title, R.string.dialog_title_reset_setting, R.drawable.ic_reset, R.color.textTitle))
                .setMessage(requireContext().getString(R.string.dialog_desc_reset_setting_warning))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    int currentPage = mViewPager.getCurrentItem();
                    MenuPreference.getInstance(requireContext()).clear();
                    initData();
                    mViewPager.setCurrentItem(currentPage, false);
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOnExistsSetting != null) {
            mOnExistsSetting.onExists();
        }
    }

    public interface OnExistsSetting {
        void onExists();
    }

}
