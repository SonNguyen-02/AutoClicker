package com.mct.auto_clicker.fragment;

import static com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType.getListItemDisable;
import static com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference.MAX_ITEM_SHOW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.adapter.SettingMenuItemAdapter;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

import java.util.Collections;
import java.util.List;

public class MenuListSettingFragment extends Fragment implements SettingMenuItemAdapter.OnItemClickListener {

    private View mView;
    private RecyclerView rcvMenuEnable, rcvMenuDisable;
    private final MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired;
    private SettingMenuItemAdapter menuEnableAdapter, menuDisableAdapter;
    private long lastTimeClick;

    public MenuListSettingFragment(MenuSettingFragment.MenuItemAdapterRequired mMenuItemAdapterRequired) {
        this.mMenuItemAdapterRequired = mMenuItemAdapterRequired;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting_menu_list, container, false);

        initUi();

        initData();

        return mView;
    }

    private void initUi() {
        rcvMenuEnable = mView.findViewById(R.id.rcv_menu_enable);
        rcvMenuDisable = mView.findViewById(R.id.rcv_menu_disable);
    }

    private void initData() {
        menuEnableAdapter = new SettingMenuItemAdapter(requireContext(), true, getListMenuItem(), this);

        LinearLayoutManager enableLayoutManager = new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rcvMenuEnable.setLayoutManager(enableLayoutManager);
        // create item touch helper
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int positionDrag = viewHolder.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();
                Collections.swap(getListMenuItem(), positionDrag, positionTarget);
                menuEnableAdapter.notifyItemMoved(positionDrag, positionTarget);
                mMenuItemAdapterRequired.getMenuItemAdapter().notifyItemMoved(positionDrag, positionTarget);
                saveCurrentControlMenu();
                return false;
            }

            // disable onLongPressItem
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        // set drag for interface
        menuEnableAdapter.setDragListener(touchHelper::startDrag);
        // set touch for recyclerview
        touchHelper.attachToRecyclerView(rcvMenuEnable);
        // set adapter
        rcvMenuEnable.setAdapter(menuEnableAdapter);

        menuDisableAdapter = new SettingMenuItemAdapter(requireContext(), false, getListItemDisable(getListMenuItem()), this);
        rcvMenuDisable.setAdapter(menuDisableAdapter);
        LinearLayoutManager disableLayoutManager = new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rcvMenuDisable.setLayoutManager(disableLayoutManager);
    }

    @Override
    public void onClick(MenuItemType item, int position, boolean isEnable) {
        if (lastTimeClick + 500 > System.currentTimeMillis()) {
            return;
        }
        lastTimeClick = System.currentTimeMillis();
        if (isEnable) {
            menuEnableAdapter.removeMenuItem(position);
            mMenuItemAdapterRequired.getMenuItemAdapter().notifyItemRemoved(position);
            menuDisableAdapter.addMenuItem(item, true);
            saveCurrentControlMenu();
        } else {
            if (getListMenuItem().size() >= MAX_ITEM_SHOW) {
                Toast.makeText(requireContext(), R.string.menu_limit, Toast.LENGTH_SHORT).show();
                return;
            }
            menuEnableAdapter.addMenuItem(item, false);
            mMenuItemAdapterRequired.getMenuItemAdapter().notifyItemInserted(getListMenuItem().size() - 1);
            menuDisableAdapter.removeMenuItem(position);
            saveCurrentControlMenu();
        }

    }

    private void saveCurrentControlMenu() {
        MenuPreference.getInstance(requireContext()).setListMenuItem(getListMenuItem()).commit();
    }

    private List<MenuItemType> getListMenuItem() {
        return mMenuItemAdapterRequired.getMenuItemAdapter().getListMenuItem();
    }

}
