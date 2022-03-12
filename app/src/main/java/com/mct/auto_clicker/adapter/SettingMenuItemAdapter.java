package com.mct.auto_clicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mct.auto_clicker.R;
import com.mct.auto_clicker.overlays.mainmenu.menu.MenuItemType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SettingMenuItemAdapter extends RecyclerView.Adapter<SettingMenuItemAdapter.SettingMenuItemViewHolder> {

    private final Context mContext;

    private final boolean isEnable;

    private final List<MenuItemType> mListMenuItem;

    private final OnItemClickListener mOnItemClickListener;

    private OnDragListener dragListener;

    public SettingMenuItemAdapter(Context mContext, boolean isEnable, List<MenuItemType> mListMenuItem, OnItemClickListener mOnItemClickListener) {
        this.mContext = mContext;
        this.isEnable = isEnable;
        this.mListMenuItem = mListMenuItem;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setDragListener(OnDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void addMenuItem(MenuItemType item, boolean sort) {
        mListMenuItem.add(item);
        if (sort) {
            mListMenuItem.sort(Comparator.comparingInt(MenuItemType::getId));
            notifyDataSetChanged();
        } else {
            notifyItemInserted(mListMenuItem.size() - 1);
        }
    }

    public void removeMenuItem(int position) {

        if (mListMenuItem.remove(position) != null) {
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public SettingMenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_menu_item, parent, false);
        return new SettingMenuItemViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SettingMenuItemViewHolder holder, int position) {
        MenuItemType item = mListMenuItem.get(position);
        if (item == null) {
            return;
        }
        holder.imgIcon.setImageResource(item.getIcon());
        holder.tvIconDescription.setText(item.getIconDesc());
        if (isEnable) {
            holder.imgAddRemove.setImageResource(R.drawable.ic_circle_multiply);
            if (MenuItemType.isCanRemove(item)) {
                holder.imgAddRemove.setEnabled(true);
                holder.imgAddRemove.setColorFilter(ContextCompat.getColor(mContext, R.color.md_deep_orange_A400), PorterDuff.Mode.SRC_IN);
            } else {
                holder.imgAddRemove.setEnabled(false);
                holder.imgAddRemove.setColorFilter(ContextCompat.getColor(mContext, R.color.md_grey_800), PorterDuff.Mode.SRC_IN);
            }
            holder.imgMove.setVisibility(View.VISIBLE);
            holder.imgMove.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dragListener.requestDrag(holder);
                }
                return false;
            });
        } else {
            holder.imgMove.setVisibility(View.GONE);
            holder.imgAddRemove.setImageResource(R.drawable.ic_circle_plus);
            holder.imgAddRemove.setColorFilter(ContextCompat.getColor(mContext, R.color.md_green_700), PorterDuff.Mode.SRC_IN);
        }
        holder.imgAddRemove.setOnClickListener(view -> mOnItemClickListener.onClick(item, holder.getAdapterPosition(), isEnable));
    }

    @Override
    public int getItemCount() {
        if (mListMenuItem != null) {
            return mListMenuItem.size();
        }
        return 0;
    }

    static class SettingMenuItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imgIcon, imgAddRemove, imgMove;
        TextView tvIconDescription;

        public SettingMenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            imgAddRemove = itemView.findViewById(R.id.img_add_remove);
            imgMove = itemView.findViewById(R.id.img_move);
            tvIconDescription = itemView.findViewById(R.id.tv_icon_description);
        }
    }

    public interface OnItemClickListener {
        void onClick(MenuItemType item, int position, boolean isEnable);
    }

    public interface OnDragListener {
        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }
}
