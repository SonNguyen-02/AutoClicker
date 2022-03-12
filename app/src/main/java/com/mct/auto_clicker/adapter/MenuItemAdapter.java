package com.mct.auto_clicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuPreference;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private MenuItemListener mMenuItemListener;
    private final MenuPreference mMenuPreference;

    private int menuItemSize;
    private boolean isStart;
    private int orientation;
    private int color;
    private List<MenuItemType> mListMenuItem;

    private MenuItemAdapter(Context mContext) {
        this.mMenuPreference = MenuPreference.getInstance(mContext);
        this.menuItemSize = mMenuPreference.getButtonMenuSize();
        this.orientation = mMenuPreference.getMenuOrientation();
        this.color = mMenuPreference.getMenuColor();
    }

    public MenuItemAdapter(Context mContext, MenuItemListener mMenuItemListener) {
        this(mContext);
        this.mMenuItemListener = mMenuItemListener;
        initState(true, false, true);
    }

    public MenuItemAdapter(Context mContext, List<MenuItemType> mListMenuItem) {
        this(mContext);
        this.mListMenuItem = mListMenuItem;
    }

    public List<MenuItemType> getListMenuItem() {
        return mListMenuItem;
    }

    public void setMenuItemSize(int menuItemSize) {
        if (this.menuItemSize != menuItemSize) {
            this.menuItemSize = menuItemSize;
            notifyData();
        }
    }

    public void initState(boolean isExpand, boolean isStart, boolean isShowing) {
        this.isStart = isStart;
        if (isExpand) {
            mListMenuItem = mMenuPreference.getListMenuItem();
        } else {
            mListMenuItem = MenuItemType.getDefaultCollapseItem();
        }
        mListMenuItem.forEach(item -> {
            if (item == MenuItemType.MENU_ITEM_EXPAND_COLLAPSE) {
                item.setIcon(isExpand ? R.drawable.ic_collapse : R.drawable.ic_expand);
            }
            if (item == MenuItemType.MENU_ITEM_PLAY_PAUSE) {
                item.setIcon(isStart ? R.drawable.ic_pause : R.drawable.ic_start);
            }
            if (item == MenuItemType.MENU_ITEM_SHOW_HIDDEN) {
                item.setIcon(isShowing ? R.drawable.ic_eye : R.drawable.ic_eye_slash);
            }
        });
        notifyData();
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
        for (MenuItemType item : mListMenuItem) {
            if (item == MenuItemType.MENU_ITEM_PLAY_PAUSE) {
                item.setIcon(isStart ? R.drawable.ic_pause : R.drawable.ic_start);
                break;
            }
        }
        notifyData();
    }

    public void setShowing(boolean isShowing) {
        for (int i = 0; i < mListMenuItem.size(); i++) {
            MenuItemType item = mListMenuItem.get(i);
            if (item == MenuItemType.MENU_ITEM_SHOW_HIDDEN) {
                item.setIcon(isShowing ? R.drawable.ic_eye : R.drawable.ic_eye_slash);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
        notifyData();
    }

    public void setColor(int color) {
        this.color = color;
        notifyData();
    }

    private void notifyData() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItemType item = mListMenuItem.get(position);
        if (item == null) {
            return;
        }
        holder.imageBtn.setImageResource(item.getIcon());
        holder.imageBtn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        if (orientation == RecyclerView.HORIZONTAL) {
            holder.imageBtn.getLayoutParams().width = menuItemSize - 8;
            holder.imageBtn.getLayoutParams().height = menuItemSize;
        } else {
            holder.imageBtn.getLayoutParams().width = menuItemSize;
            holder.imageBtn.getLayoutParams().height = menuItemSize - 8;
        }
        holder.imageBtn.setLayoutParams(holder.imageBtn.getLayoutParams());

        if (mMenuItemListener == null) {
            return;
        }
        if (MenuItemType.isCanDisable(item)) {
            mMenuItemListener.onItemStateChange(holder.imageBtn, !isStart);
        } else {
            mMenuItemListener.onItemStateChange(holder.imageBtn, true);
        }
        holder.imageBtn.setOnTouchListener((view, motionEvent) -> mMenuItemListener.onTouchItem(view, motionEvent, item, position));
    }

    @Override
    public int getItemCount() {
        if (mListMenuItem != null) {
            return mListMenuItem.size();
        }
        return 0;
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {

        ImageButton imageBtn;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBtn = itemView.findViewById(R.id.btn_menu_item);
        }
    }

    public interface MenuItemListener {
        boolean onTouchItem(View view, @NonNull MotionEvent event, MenuItemType item, int pos);

        void onItemStateChange(View view, boolean enable);
    }
}
