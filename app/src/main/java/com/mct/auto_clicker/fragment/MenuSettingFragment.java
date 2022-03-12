package com.mct.auto_clicker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.GeneralSettingMenuViewPagerAdapter;
import com.mct.auto_clicker.adapter.MenuItemAdapter;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

import java.util.List;

public class MenuSettingFragment extends Fragment {

    private View mView;
    private LinearLayout llFloatingMenu, llBoxSetting;
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private MenuItemAdapter menuItemAdapter;
    private RecyclerView rcvMenu;
    private LinearLayoutManager layoutManager;

    private MenuPreference mMenuPreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting_menu, container, false);
        mMenuPreference = MenuPreference.getInstance(requireContext());

        initUi();

        initData();

        return mView;
    }

    private void initUi() {
        llFloatingMenu = mView.findViewById(R.id.ll_floating_menu);
        llBoxSetting = mView.findViewById(R.id.ll_box_setting);
        mTabLayout = mView.findViewById(R.id.tab_layout);
        mViewPager = mView.findViewById(R.id.view_pager);
        rcvMenu = mView.findViewById(R.id.rcv_menu);
    }

    private void initData() {
        int menuAlpha = mMenuPreference.getButtonMenuAlpha();
        setMenuAlpha(menuAlpha);

        List<MenuItemType> mListMenuItem = MenuPreference.getInstance(requireContext()).getListMenuItem();
        menuItemAdapter = new MenuItemAdapter(requireContext(), mListMenuItem);
        rcvMenu.setAdapter(menuItemAdapter);
        int menuOrientation = mMenuPreference.getMenuOrientation();
        layoutManager = new LinearLayoutManager(requireContext());
        rcvMenu.setLayoutManager(layoutManager);
        initBoxOrientation(menuOrientation, true);

        GeneralSettingMenuViewPagerAdapter adapter = new GeneralSettingMenuViewPagerAdapter(requireActivity(), this::getMenuItemAdapter, new OnSettingChange() {
            @Override
            public void onOrientationChange(int menuOrientation) {
                initBoxOrientation(menuOrientation, false);
            }

            @Override
            public void onAlphaChange(int alpha) {
                setMenuAlpha(alpha);
            }
        });
        mViewPager.setAdapter(adapter);
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            if (position == 0) {
                tab.setIcon(R.drawable.ic_setting_menu_size);
            } else {
                tab.setIcon(R.drawable.ic_setting_menu_list);
            }
        }).attach();
    }

    private void setMenuAlpha(int menuAlpha) {
        llFloatingMenu.setAlpha(menuAlpha / 100f);
    }

    private void initBoxOrientation(int menuOrientation, boolean isCreate) {
        if (!isCreate) {
            menuItemAdapter.setOrientation(menuOrientation);
        }
        int boxOrientation = menuOrientation == RecyclerView.VERTICAL ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL;
        llBoxSetting.setOrientation(boxOrientation);
        layoutManager.setOrientation(menuOrientation);
        if (boxOrientation == LinearLayout.VERTICAL) {
            int padding = requireContext().getResources().getDimensionPixelSize(R.dimen.margin_vertical_large);
            llBoxSetting.setPadding(0, padding, 0, 0);
        } else {
            int padding = requireContext().getResources().getDimensionPixelSize(R.dimen.margin_horizontal_default);
            llBoxSetting.setPadding(padding, 0, 0, 0);
        }
    }

    public MenuItemAdapter getMenuItemAdapter() {
        return menuItemAdapter;
    }

    public interface MenuItemAdapterRequired {
        MenuItemAdapter getMenuItemAdapter();
    }

    public interface OnSettingChange {
        void onOrientationChange(int menuOrientation);

        void onAlphaChange(int alpha);
    }
}
